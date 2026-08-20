package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GlobalEmergencyData
import com.example.data.db.AppDatabase
import com.example.data.model.EmergencyContact
import com.example.data.repository.EmergencyContactRepository
import com.example.service.EmergencyAlertController
import com.example.service.EmergencyLocationProvider
import com.example.service.LocationInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SosState {
    object Idle : SosState()
    data class CountingDown(val remainingSeconds: Int, val progress: Float) : SosState()
    data class ActiveAlert(val isMuted: Boolean, val elapsedSeconds: Long) : SosState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = EmergencyContactRepository(database.emergencyContactDao())
    val alertController = EmergencyAlertController(application)
    val locationProvider = EmergencyLocationProvider(application)

    // SOS Trigger State
    private val _sosState = MutableStateFlow<SosState>(SosState.Idle)
    val sosState: StateFlow<SosState> = _sosState.asStateFlow()

    private var countdownJob: Job? = null
    private var activeTimerJob: Job? = null

    // Screen Strobe state (visual full-screen strobe toggle)
    private val _isScreenStrobeActive = MutableStateFlow(false)
    val isScreenStrobeActive: StateFlow<Boolean> = _isScreenStrobeActive.asStateFlow()

    private val _strobeColorToggle = MutableStateFlow(false) // alternates true/false for red/white
    val strobeColorToggle: StateFlow<Boolean> = _strobeColorToggle.asStateFlow()
    private var strobeJob: Job? = null

    // Location State
    val locationState: StateFlow<LocationInfo> = locationProvider.locationState

    // Contacts State from Room
    val contacts: StateFlow<List<EmergencyContact>> = repository.allContacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Active Navigation Tab (0: SOS, 1: Location & Share, 2: Contacts, 3: Global Numbers, 4: First Aid)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Global Numbers Filter
    private val _selectedRegion = MutableStateFlow("ALL")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // CPR Metronome State
    private val _isCprMetronomeRunning = MutableStateFlow(false)
    val isCprMetronomeRunning: StateFlow<Boolean> = _isCprMetronomeRunning.asStateFlow()

    private val _cprBeatPulse = MutableStateFlow(false)
    val cprBeatPulse: StateFlow<Boolean> = _cprBeatPulse.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setSelectedRegion(region: String) {
        _selectedRegion.value = region
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- SOS TRIGGER LOGIC ---

    fun startSosCountdown() {
        if (_sosState.value !is SosState.Idle) return

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val totalSeconds = 3
            val steps = 30
            val delayPerStep = (totalSeconds * 1000L) / steps

            for (step in steps downTo 1) {
                val remainingSec = (step * totalSeconds + steps - 1) / steps
                val progress = 1f - (step.toFloat() / steps.toFloat())
                _sosState.value = SosState.CountingDown(remainingSec, progress)
                delay(delayPerStep)
            }

            // Trigger actual SOS Alert
            triggerEmergencyAlert()
        }
    }

    fun cancelSosCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _sosState.value = SosState.Idle
    }

    private fun triggerEmergencyAlert() {
        _sosState.value = SosState.ActiveAlert(isMuted = false, elapsedSeconds = 0)
        alertController.startEmergencyAlarm(muted = false)
        startScreenStrobe()

        activeTimerJob?.cancel()
        activeTimerJob = viewModelScope.launch {
            var elapsed = 0L
            while (_sosState.value is SosState.ActiveAlert) {
                delay(1000)
                elapsed += 1
                val current = _sosState.value
                if (current is SosState.ActiveAlert) {
                    _sosState.value = current.copy(elapsedSeconds = elapsed)
                }
            }
        }
    }

    fun deactivateEmergencyAlert() {
        countdownJob?.cancel()
        countdownJob = null
        activeTimerJob?.cancel()
        activeTimerJob = null
        stopScreenStrobe()
        alertController.stopEmergencyAlarm()
        _sosState.value = SosState.Idle
    }

    fun toggleMute() {
        val current = _sosState.value
        if (current is SosState.ActiveAlert) {
            val newMuted = !current.isMuted
            _sosState.value = current.copy(isMuted = newMuted)
            alertController.setMuted(newMuted)
        }
    }

    private fun startScreenStrobe() {
        _isScreenStrobeActive.value = true
        strobeJob?.cancel()
        strobeJob = viewModelScope.launch {
            while (_isScreenStrobeActive.value) {
                _strobeColorToggle.value = !_strobeColorToggle.value
                delay(250) // 2Hz flash rate
            }
        }
    }

    private fun stopScreenStrobe() {
        _isScreenStrobeActive.value = false
        strobeJob?.cancel()
        strobeJob = null
    }

    // --- CPR METRONOME LOGIC ---

    fun toggleCprMetronome() {
        if (_isCprMetronomeRunning.value) {
            alertController.stopCprMetronome()
            _isCprMetronomeRunning.value = false
        } else {
            _isCprMetronomeRunning.value = true
            alertController.startCprMetronome {
                viewModelScope.launch {
                    _cprBeatPulse.value = true
                    delay(120)
                    _cprBeatPulse.value = false
                }
            }
        }
    }

    // --- CONTACTS CRUD LOGIC ---

    fun addContact(name: String, phone: String, relationship: String, isPrimary: Boolean, notes: String) {
        viewModelScope.launch {
            val newContact = EmergencyContact(
                name = name.trim(),
                phone = phone.trim(),
                relationship = relationship.trim().ifEmpty { "General Emergency" },
                isPrimary = isPrimary,
                notes = notes.trim()
            )
            repository.addContact(newContact)
        }
    }

    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun setPrimaryContact(contactId: Long) {
        viewModelScope.launch {
            repository.setPrimaryContact(contactId)
        }
    }

    // Location updates
    fun updateLocationPermission(hasPermission: Boolean) {
        locationProvider.startLocationUpdates(hasPermission)
    }

    fun setManualLocationAddress(address: String) {
        locationProvider.setManualLocation(address)
    }

    override fun onCleared() {
        super.onCleared()
        alertController.stopEmergencyAlarm()
        alertController.stopCprMetronome()
        locationProvider.stopLocationUpdates()
    }
}
