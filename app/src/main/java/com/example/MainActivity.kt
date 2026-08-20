package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ContactsSection
import com.example.ui.components.FirstAidSection
import com.example.ui.components.GlobalNumbersSection
import com.example.ui.components.LocationShareSection
import com.example.ui.components.ScreenStrobeOverlay
import com.example.ui.components.SosTriggerSection
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SleekAccent
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekNavBg
import com.example.ui.theme.SleekNavIndicator
import com.example.ui.theme.SleekNavIndicatorText
import com.example.ui.theme.SleekOnAccent
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SosRed
import com.example.ui.theme.SosRedBright
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SosState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SosApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosApp(viewModel: MainViewModel) {
    val sosState by viewModel.sosState.collectAsStateWithLifecycle()
    val locationInfo by viewModel.locationState.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isCprMetronomeRunning by viewModel.isCprMetronomeRunning.collectAsStateWithLifecycle()
    val cprBeatPulse by viewModel.cprBeatPulse.collectAsStateWithLifecycle()
    val isScreenStrobeActive by viewModel.isScreenStrobeActive.collectAsStateWithLifecycle()
    val strobeColorToggle by viewModel.strobeColorToggle.collectAsStateWithLifecycle()

    val primaryContact = contacts.firstOrNull { it.isPrimary } ?: contacts.firstOrNull()

    // Location & Hardware Permissions Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.updateLocationPermission(fineGranted || coarseGranted)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = SleekBackground,
            topBar = {
                Surface(
                    color = SleekBackground,
                    border = androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (sosState is SosState.ActiveAlert) SosRed else SleekAccent,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "One-Tap SOS",
                                            tint = if (sosState is SosState.ActiveAlert) Color.White else SleekOnAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "EMERGENCY MODE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.8.sp,
                                        color = SleekTextSecondary
                                    )
                                    Text(
                                        text = "One-Tap SOS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = SleekTextPrimary
                                    )
                                }
                            }
                        },
                        actions = {
                            // GPS High Accuracy Indicator Chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SleekSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (locationInfo.latitude != null) EmergencyGreen else EmergencyAmber,
                                        modifier = Modifier.size(7.dp)
                                    ) {}
                                    Text(
                                        text = if (locationInfo.latitude != null) "GPS: HIGH ACCURACY" else "GPS: LOCATING",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = if (locationInfo.latitude != null) EmergencyGreen else EmergencyAmber
                                    )
                                }
                            }

                            if (sosState is SosState.ActiveAlert) {
                                val activeState = sosState as SosState.ActiveAlert
                                IconButton(onClick = { viewModel.toggleMute() }) {
                                    Icon(
                                        imageVector = if (activeState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "Toggle Mute",
                                        tint = SleekAccent
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = SleekBackground,
                            titleContentColor = SleekTextPrimary
                        )
                    )
                }
            },
            bottomBar = {
                Surface(
                    color = SleekNavBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = SleekNavBg,
                        contentColor = SleekTextPrimary
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            icon = {
                                Icon(Icons.Default.Shield, contentDescription = "SOS")
                            },
                            label = { Text("SOS", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SleekNavIndicatorText,
                                selectedTextColor = SleekAccent,
                                unselectedIconColor = SleekTextMuted,
                                unselectedTextColor = SleekTextMuted,
                                indicatorColor = SleekNavIndicator
                            ),
                            modifier = Modifier.testTag("nav_sos_tab")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            icon = {
                                Icon(Icons.Default.LocationOn, contentDescription = "GPS & Share")
                            },
                            label = { Text("Location", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SleekNavIndicatorText,
                                selectedTextColor = SleekAccent,
                                unselectedIconColor = SleekTextMuted,
                                unselectedTextColor = SleekTextMuted,
                                indicatorColor = SleekNavIndicator
                            ),
                            modifier = Modifier.testTag("nav_gps_tab")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { viewModel.selectTab(2) },
                            icon = {
                                BadgedBox(badge = {
                                    if (contacts.isNotEmpty()) {
                                        Badge(containerColor = SosRed, contentColor = Color.White) {
                                            Text("${contacts.size}")
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Contacts, contentDescription = "Contacts")
                                }
                            },
                            label = { Text("Contacts", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SleekNavIndicatorText,
                                selectedTextColor = SleekAccent,
                                unselectedIconColor = SleekTextMuted,
                                unselectedTextColor = SleekTextMuted,
                                indicatorColor = SleekNavIndicator
                            ),
                            modifier = Modifier.testTag("nav_contacts_tab")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { viewModel.selectTab(3) },
                            icon = {
                                Icon(Icons.Default.Call, contentDescription = "Quick Dial")
                            },
                            label = { Text("Numbers", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SleekNavIndicatorText,
                                selectedTextColor = SleekAccent,
                                unselectedIconColor = SleekTextMuted,
                                unselectedTextColor = SleekTextMuted,
                                indicatorColor = SleekNavIndicator
                            ),
                            modifier = Modifier.testTag("nav_global_numbers_tab")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 4,
                            onClick = { viewModel.selectTab(4) },
                            icon = {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = "First Aid")
                            },
                            label = { Text("First Aid", fontSize = 11.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SleekNavIndicatorText,
                                selectedTextColor = SleekAccent,
                                unselectedIconColor = SleekTextMuted,
                                unselectedTextColor = SleekTextMuted,
                                indicatorColor = SleekNavIndicator
                            ),
                            modifier = Modifier.testTag("nav_first_aid_tab")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> SosTriggerSection(
                        sosState = sosState,
                        locationInfo = locationInfo,
                        primaryContact = primaryContact,
                        onStartCountdown = { viewModel.startSosCountdown() },
                        onCancelCountdown = { viewModel.cancelSosCountdown() },
                        onDeactivateAlert = { viewModel.deactivateEmergencyAlert() },
                        onToggleMute = { viewModel.toggleMute() },
                        onNavigateToTab = { viewModel.selectTab(it) }
                    )
                    1 -> LocationShareSection(
                        locationInfo = locationInfo,
                        onRequestLocationUpdate = { viewModel.updateLocationPermission(true) },
                        onSetManualAddress = { viewModel.setManualLocationAddress(it) }
                    )
                    2 -> ContactsSection(
                        contacts = contacts,
                        locationInfo = locationInfo,
                        onAddContact = { name, phone, rel, isPrimary, notes ->
                            viewModel.addContact(name, phone, rel, isPrimary, notes)
                        },
                        onUpdateContact = { viewModel.updateContact(it) },
                        onDeleteContact = { viewModel.deleteContact(it) },
                        onSetPrimary = { viewModel.setPrimaryContact(it) }
                    )
                    3 -> GlobalNumbersSection(
                        selectedRegion = selectedRegion,
                        searchQuery = searchQuery,
                        onSelectRegion = { viewModel.setSelectedRegion(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) }
                    )
                    4 -> FirstAidSection(
                        isMetronomeRunning = isCprMetronomeRunning,
                        cprBeatPulse = cprBeatPulse,
                        onToggleMetronome = { viewModel.toggleCprMetronome() }
                    )
                }
            }
        }

        // Screen Strobe Overlay during Active Distress
        ScreenStrobeOverlay(
            isActive = isScreenStrobeActive,
            strobeToggle = strobeColorToggle,
            isMuted = (sosState as? SosState.ActiveAlert)?.isMuted ?: false,
            onToggleMute = { viewModel.toggleMute() },
            onDeactivate = { viewModel.deactivateEmergencyAlert() }
        )
    }
}
