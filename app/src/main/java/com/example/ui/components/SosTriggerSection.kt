package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.service.EmergencyCommunicationHelper
import com.example.service.LocationInfo
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.SleekAccent
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekOnAccent
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SosRed
import com.example.ui.theme.SosRedBorder
import com.example.ui.theme.SosRedBright
import com.example.ui.theme.SosRedDark
import com.example.ui.theme.SosRedGlow
import com.example.ui.theme.SosRedText
import com.example.ui.viewmodel.SosState

@Composable
fun SosTriggerSection(
    sosState: SosState,
    locationInfo: LocationInfo,
    primaryContact: EmergencyContact?,
    onStartCountdown: () -> Unit,
    onCancelCountdown: () -> Unit,
    onDeactivateAlert: () -> Unit,
    onToggleMute: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Sleek Status Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (sosState) {
                    is SosState.ActiveAlert -> SosRedDark
                    is SosState.CountingDown -> SleekSurfaceVariant
                    is SosState.Idle -> SleekSurface
                }
            ),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when (sosState) {
                    is SosState.ActiveAlert -> SosRedBorder
                    is SosState.CountingDown -> EmergencyAmber
                    is SosState.Idle -> SleekBorder
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = when (sosState) {
                        is SosState.ActiveAlert -> SosRedBorder
                        is SosState.CountingDown -> EmergencyAmber
                        is SosState.Idle -> EmergencyGreen
                    },
                    modifier = Modifier.size(10.dp)
                ) {}

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (sosState) {
                            is SosState.ActiveAlert -> "EMERGENCY BROADCAST ACTIVE"
                            is SosState.CountingDown -> "TRIGGER IN PROGRESS..."
                            is SosState.Idle -> "READY FOR RAPID TRIGGER"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = when (sosState) {
                            is SosState.ActiveAlert -> SosRedText
                            is SosState.CountingDown -> EmergencyAmber
                            is SosState.Idle -> SleekTextPrimary
                        }
                    )
                    Text(
                        text = when (sosState) {
                            is SosState.ActiveAlert -> "Siren, Strobe & Haptic Broadcast running"
                            is SosState.CountingDown -> "3-second safety window to cancel false triggers"
                            is SosState.Idle -> if (locationInfo.latitude != null) "GPS Locked: ±${locationInfo.accuracy?.toInt() ?: 10}m" else "Tap SOS anytime in distress"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekTextSecondary
                    )
                }

                if (sosState is SosState.ActiveAlert) {
                    Button(
                        onClick = onToggleMute,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sosState.isMuted) SleekSurfaceVariant else SosRedBright
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mute_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (sosState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (sosState.isMuted) "Unmute Siren" else "Mute Siren",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Center SOS Trigger or Countdown or Active Panel
        when (sosState) {
            is SosState.Idle -> {
                IdleSosButton(onStartCountdown = onStartCountdown)
            }
            is SosState.CountingDown -> {
                CountdownSosPanel(
                    remainingSeconds = sosState.remainingSeconds,
                    progress = sosState.progress,
                    onCancel = onCancelCountdown
                )
            }
            is SosState.ActiveAlert -> {
                ActiveAlertControlPanel(
                    elapsedSeconds = sosState.elapsedSeconds,
                    isMuted = sosState.isMuted,
                    locationInfo = locationInfo,
                    onDeactivate = onDeactivateAlert,
                    onToggleMute = onToggleMute
                )
            }
        }

        // Quick Actions Grid (One-Tap Emergency Services, Primary Contact)
        Text(
            text = "INSTANT ACTION CHANNELS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = SleekTextMuted,
            modifier = Modifier.fillMaxWidth(),
            letterSpacing = 1.5.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Direct 911/112 Call
            Button(
                onClick = {
                    EmergencyCommunicationHelper.dialNumber(context, "911")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .testTag("quick_dial_911_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Emergency",
                        tint = SosRedText
                    )
                    Column {
                        Text("Dial 911", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Emergency", fontSize = 11.sp, color = SosRedText)
                    }
                }
            }

            // Primary Contact Quick Dial
            Button(
                onClick = {
                    if (primaryContact != null) {
                        EmergencyCommunicationHelper.dialNumber(context, primaryContact.phone)
                    } else {
                        onNavigateToTab(2) // navigate to contacts tab
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(68.dp)
                    .testTag("quick_call_primary_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SleekSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Primary Contact",
                        tint = SleekAccent
                    )
                    Column {
                        Text(
                            text = primaryContact?.name?.take(12) ?: "Add Contact",
                            fontWeight = FontWeight.Bold,
                            color = SleekTextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (primaryContact != null) "Primary" else "Set up now",
                            fontSize = 11.sp,
                            color = SleekAccent
                        )
                    }
                }
            }
        }

        // Live Location Card Preview - Sleek Styled
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTab(1) }
                .testTag("location_preview_card"),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekAccent,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = SleekOnAccent
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CURRENT COORDINATES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SleekAccent
                    )
                    Text(
                        text = if (locationInfo.latitude != null) {
                            "${"%.4f".format(locationInfo.latitude)}, ${"%.4f".format(locationInfo.longitude)}"
                        } else {
                            "Locating GPS..."
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                    Text(
                        text = locationInfo.address ?: "Tap to share with contacts",
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekTextSecondary,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = SleekAccent
                )
            }
        }
    }
}

@Composable
private fun IdleSosButton(onStartCountdown: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            // Outermost concentric aura ring
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(SosRedGlow.copy(alpha = 0.25f))
                    .border(1.dp, SosRed.copy(alpha = 0.4f), CircleShape)
            )

            // Inner concentric aura ring
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(SosRed.copy(alpha = 0.15f))
                    .border(2.dp, SosRed.copy(alpha = 0.5f), CircleShape)
            )

            // Inner Red SOS Main Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SosRedBright, SosRed)
                        )
                    )
                    .border(4.dp, SosRedBorder, CircleShape)
                    .shadow(20.dp, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStartCountdown
                    )
                    .testTag("main_sos_button")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS Warning",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SOS",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "HOLD OR TAP FOR SOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosRedText,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Activates 3-second safety window with Siren, Flash & GPS Broadcast",
            style = MaterialTheme.typography.bodySmall,
            color = SleekTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CountdownSosPanel(
    remainingSeconds: Int,
    progress: Float,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("sos_countdown_panel"),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, EmergencyAmber)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "EMERGENCY TRIGGERING",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = EmergencyAmber,
                letterSpacing = 1.5.sp
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(150.dp),
                    color = SosRedBright,
                    trackColor = SleekSurfaceVariant,
                    strokeWidth = 10.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$remainingSeconds",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "SECONDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary
                    )
                }
            }

            Text(
                text = "Accidental press? Tap Cancel below to abort.",
                style = MaterialTheme.typography.bodySmall,
                color = SleekTextSecondary,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("cancel_sos_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SleekSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancel",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CANCEL SOS NOW",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ActiveAlertControlPanel(
    elapsedSeconds: Long,
    isMuted: Boolean,
    locationInfo: LocationInfo,
    onDeactivate: () -> Unit,
    onToggleMute: () -> Unit
) {
    val context = LocalContext.current
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_alert_panel"),
        colors = CardDefaults.cardColors(containerColor = SosRedDark),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, SosRedBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Active Alert",
                    tint = SosRedBorder,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "SOS ACTIVE ($formattedTime)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            // Status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusPill(
                    title = "SIREN",
                    value = if (isMuted) "MUTED" else "2-TONE",
                    isActive = !isMuted,
                    activeColor = SosRedBorder
                )
                StatusPill(
                    title = "STROBE",
                    value = "2Hz FLASH",
                    isActive = true,
                    activeColor = EmergencyAmber
                )
                StatusPill(
                    title = "HAPTIC",
                    value = "PULSING",
                    isActive = true,
                    activeColor = EmergencyGreen
                )
            }

            // Immediate SOS Broadcast Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        EmergencyCommunicationHelper.sendSms(
                            context = context,
                            phoneNumber = null,
                            messageBody = locationInfo.sosMessageBody
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("active_sms_broadcast_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.5f))
                ) {
                    Text("📢 Send Instant SMS Broadcast", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = {
                        EmergencyCommunicationHelper.shareWhatsApp(
                            context = context,
                            messageBody = locationInfo.sosMessageBody
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("active_whatsapp_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("💬 Share on WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Stop / Deactivate Alert Button
            Button(
                onClick = onDeactivate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("deactivate_alert_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Safe",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I AM SAFE NOW (STOP SOS)",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    title: String,
    value: String,
    isActive: Boolean,
    activeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SleekSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) activeColor else SleekBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (isActive) activeColor else SleekTextMuted)
        }
    }
}

