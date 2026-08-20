package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.EmergencyCommunicationHelper
import com.example.service.LocationInfo
import com.example.ui.theme.EmergencyAmber
import com.example.ui.theme.EmergencyBlue
import com.example.ui.theme.EmergencyGreen
import com.example.ui.theme.SleekAccent
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekOnAccent
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SleekTextMuted
import com.example.ui.theme.SleekTextPrimary
import com.example.ui.theme.SleekTextSecondary
import com.example.ui.theme.SosRed
import com.example.ui.theme.SosRedBorder
import java.util.Locale

@Composable
fun LocationShareSection(
    locationInfo: LocationInfo,
    onRequestLocationUpdate: () -> Unit,
    onSetManualAddress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showManualInput by remember { mutableStateOf(false) }
    var manualAddressInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Main Live GPS Card (Sleek Surface)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_gps_card"),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SleekAccent,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (locationInfo.latitude != null) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                                    contentDescription = "GPS Status",
                                    tint = SleekOnAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "LIVE GEOLOCATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = SleekAccent
                            )
                            Text(
                                text = if (locationInfo.isManualFallback) "Manual Address Fallback" else if (locationInfo.latitude != null) "High Accuracy Lock" else "Locating satellite signal...",
                                style = MaterialTheme.typography.bodySmall,
                                color = SleekTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onRequestLocationUpdate,
                        modifier = Modifier.testTag("refresh_gps_btn")
                    ) {
                        if (locationInfo.isLocating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = SleekAccent)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh GPS", tint = SleekAccent)
                        }
                    }
                }

                // Coordinates Grid
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SleekSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("LATITUDE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                                Text(
                                    text = if (locationInfo.latitude != null) "%.6f".format(Locale.US, locationInfo.latitude) else "--.------",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = SleekTextPrimary
                                )
                            }

                            Column {
                                Text("LONGITUDE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                                Text(
                                    text = if (locationInfo.longitude != null) "%.6f".format(Locale.US, locationInfo.longitude) else "---.------",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = SleekTextPrimary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Accuracy: ", fontSize = 12.sp, color = SleekTextMuted)
                                Text(
                                    text = if (locationInfo.accuracy != null) "±${locationInfo.accuracy.toInt()} meters" else "Searching...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((locationInfo.accuracy ?: 100f) < 25f) EmergencyGreen else EmergencyAmber
                                )
                            }

                            if (locationInfo.altitude != null) {
                                Text(
                                    text = "Alt: ${locationInfo.altitude.toInt()}m",
                                    fontSize = 12.sp,
                                    color = SleekTextMuted
                                )
                            }
                        }
                    }
                }

                // Reverse Geocoded Address
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("APPROXIMATE ADDRESS / LANDMARK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted, letterSpacing = 0.5.sp)
                    Text(
                        text = locationInfo.address ?: (if (locationInfo.latitude != null) "Resolving nearby street address..." else "Waiting for GPS satellite fix..."),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekTextPrimary
                    )
                }
            }
        }

        // Action Buttons Grid
        Text(
            text = "ONE-TAP INSTANT LOCATION SHARING",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = SleekTextMuted,
            letterSpacing = 1.2.sp
        )

        // SMS Broadcast & WhatsApp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    .weight(1f)
                    .height(54.dp)
                    .testTag("sms_broadcast_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.4f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Message, contentDescription = "SMS", tint = Color.White)
                    Text("SMS Broadcast", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Button(
                onClick = {
                    EmergencyCommunicationHelper.shareWhatsApp(
                        context = context,
                        messageBody = locationInfo.sosMessageBody
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("whatsapp_share_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = Color.White)
                    Text("WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Copy Link & Open in Maps
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    EmergencyCommunicationHelper.copyToClipboard(
                        context = context,
                        text = locationInfo.mapsUrl,
                        label = "Emergency Google Maps Link"
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("copy_maps_link_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekAccent),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SleekAccent)
                    Text("Copy Maps Link", fontWeight = FontWeight.SemiBold)
                }
            }

            OutlinedButton(
                onClick = {
                    val lat = locationInfo.latitude ?: 37.7749
                    val lng = locationInfo.longitude ?: -122.4194
                    EmergencyCommunicationHelper.openInMaps(context, lat, lng, "Emergency Distress Location")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("open_maps_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Maps", tint = EmergencyBlue)
                    Text("Open Maps", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Native Android Share Sheet Button
        Button(
            onClick = {
                EmergencyCommunicationHelper.shareText(
                    context = context,
                    messageBody = locationInfo.sosMessageBody,
                    title = "Share Emergency Coordinates"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("native_share_sheet_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = SleekSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = SleekTextPrimary)
                Text("Share via Other Apps (Telegram, Signal, Email)", color = SleekTextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        // Manual Location Search / Indoor Fallback Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SleekSurface),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.EditLocation, contentDescription = "Manual", tint = EmergencyAmber)
                        Text("Manual Location / Indoor Fallback", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SleekTextPrimary)
                    }

                    OutlinedButton(
                        onClick = { showManualInput = !showManualInput },
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (showManualInput) "Close" else "Edit", color = SleekAccent)
                    }
                }

                if (showManualInput) {
                    Text(
                        "If GPS is blocked by concrete or indoors, type your building, room number, or street address below:",
                        fontSize = 12.sp,
                        color = SleekTextSecondary
                    )

                    OutlinedTextField(
                        value = manualAddressInput,
                        onValueChange = { manualAddressInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_address_input"),
                        placeholder = { Text("e.g., 4th Floor, Suite 402, 120 Market St") },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SleekAccent,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = SleekTextPrimary,
                            unfocusedTextColor = SleekTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (manualAddressInput.isNotBlank()) {
                                onSetManualAddress(manualAddressInput)
                                showManualInput = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_manual_location_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Set Manual Emergency Location", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

