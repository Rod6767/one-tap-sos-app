package com.example.service

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class EmergencyAlertController(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var sirenJob: Job? = null
    private var vibrationJob: Job? = null
    private var torchStrobeJob: Job? = null
    private var metronomeJob: Job? = null

    private var isMuted = false
    private var isTorchAvailable = false
    private var torchCameraId: String? = null
    private val cameraManager: CameraManager? by lazy {
        try {
            context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        } catch (e: Exception) {
            null
        }
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    init {
        detectTorchCamera()
    }

    private fun detectTorchCamera() {
        try {
            cameraManager?.let { manager ->
                val cameraIds = manager.cameraIdList
                for (id in cameraIds) {
                    val characteristics = manager.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        torchCameraId = id
                        isTorchAvailable = true
                        break
                    }
                }
                if (torchCameraId == null && cameraIds.isNotEmpty()) {
                    for (id in cameraIds) {
                        val characteristics = manager.getCameraCharacteristics(id)
                        if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                            torchCameraId = id
                            isTorchAvailable = true
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EmergencyAlert", "Failed to detect camera torch: ${e.message}")
        }
    }

    fun startEmergencyAlarm(muted: Boolean = false) {
        isMuted = muted
        startSiren()
        startVibration()
        startTorchStrobe()
    }

    fun stopEmergencyAlarm() {
        stopSiren()
        stopVibration()
        stopTorchStrobe()
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            stopSiren()
        } else {
            if (sirenJob == null || sirenJob?.isActive != true) {
                startSiren()
            }
        }
    }

    private fun startSiren() {
        if (isMuted) return
        sirenJob?.cancel()
        sirenJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(sampleRate / 4)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build()

            var audioTrack: AudioTrack? = null
            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val buffer = ShortArray(sampleRate / 10) // 100ms chunk
                var currentFreq = 700.0
                var freqDirection = 1
                var phase = 0.0

                val minFreq = 650.0
                val maxFreq = 1400.0
                val freqStep = 18.0

                while (isActive && !isMuted) {
                    for (i in buffer.indices) {
                        val angle = 2.0 * PI * currentFreq / sampleRate
                        phase += angle
                        if (phase > 2.0 * PI) phase -= 2.0 * PI
                        
                        // Square-ish smoothed sine wave for piercing siren quality
                        val sampleValue = (sin(phase) * 0.9).coerceIn(-1.0, 1.0)
                        buffer[i] = (sampleValue * Short.MAX_VALUE * 0.85).toInt().toShort()

                        currentFreq += (freqDirection * (freqStep / 20.0))
                        if (currentFreq >= maxFreq) {
                            currentFreq = maxFreq
                            freqDirection = -1
                        } else if (currentFreq <= minFreq) {
                            currentFreq = minFreq
                            freqDirection = 1
                        }
                    }
                    audioTrack.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("EmergencyAlert", "AudioTrack Siren error: ${e.message}")
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (e: Exception) {
                    // Ignore cleanup error
                }
            }
        }
    }

    private fun stopSiren() {
        sirenJob?.cancel()
        sirenJob = null
    }

    private fun startVibration() {
        vibrationJob?.cancel()
        vibrationJob = scope.launch(Dispatchers.Default) {
            val pattern = longArrayOf(0, 250, 120, 250, 400)
            while (isActive) {
                try {
                    vibrator?.let { vib ->
                        if (vib.hasVibrator()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val effect = VibrationEffect.createWaveform(pattern, -1)
                                vib.vibrate(effect)
                            } else {
                                @Suppress("DEPRECATION")
                                vib.vibrate(pattern, -1)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EmergencyAlert", "Vibration error: ${e.message}")
                }
                delay(1050)
            }
        }
    }

    private fun stopVibration() {
        vibrationJob?.cancel()
        vibrationJob = null
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun startTorchStrobe() {
        if (!isTorchAvailable || torchCameraId == null) return
        torchStrobeJob?.cancel()
        torchStrobeJob = scope.launch(Dispatchers.Default) {
            val camId = torchCameraId ?: return@launch
            val manager = cameraManager ?: return@launch
            var torchOn = false

            while (isActive) {
                try {
                    torchOn = !torchOn
                    manager.setTorchMode(camId, torchOn)
                } catch (e: Exception) {
                    Log.e("EmergencyAlert", "Torch toggle failed: ${e.message}")
                }
                delay(250) // 2Hz strobe interval
            }

            // Ensure torch is off when loop finishes
            try {
                manager.setTorchMode(camId, false)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun stopTorchStrobe() {
        torchStrobeJob?.cancel()
        torchStrobeJob = null
        torchCameraId?.let { id ->
            try {
                cameraManager?.setTorchMode(id, false)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // CPR Metronome (110 BPM beep click)
    fun startCprMetronome(onBeat: () -> Unit) {
        stopCprMetronome()
        metronomeJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 22050
            val clickDurationSamples = (sampleRate * 0.05).toInt() // 50ms click
            val buffer = ShortArray(clickDurationSamples)
            for (i in buffer.indices) {
                val t = i.toDouble() / sampleRate
                val decay = 1.0 - (i.toDouble() / clickDurationSamples)
                buffer[i] = (sin(2.0 * PI * 880.0 * t) * Short.MAX_VALUE * 0.6 * decay).toInt().toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build()

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, buffer.size)

                // 110 BPM -> 60,000 / 110 = ~545ms interval
                val intervalMs = 545L

                while (isActive) {
                    track.stop()
                    track.reloadStaticData()
                    track.play()
                    
                    launch(Dispatchers.Main) {
                        onBeat()
                    }

                    // Single subtle haptic tap on beat
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(35)
                        }
                    } catch (e: Exception) {
                        // Ignore
                    }

                    delay(intervalMs)
                }
            } catch (e: Exception) {
                Log.e("EmergencyAlert", "CPR Metronome error: ${e.message}")
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }

    fun stopCprMetronome() {
        metronomeJob?.cancel()
        metronomeJob = null
    }
}
