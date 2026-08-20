package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirstAidData
import com.example.data.FirstAidGuide
import com.example.ui.theme.EmergencyAmber
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
import com.example.ui.theme.SosRedBright

@Composable
fun FirstAidSection(
    isMetronomeRunning: Boolean,
    cprBeatPulse: Boolean,
    onToggleMetronome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            // Expand first CPR guide by default
            put("cpr_adult", true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        item {
            Column {
                Text(
                    text = "OFFLINE FIRST AID & DISASTER GUIDES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = SleekTextMuted,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Instant life-saving protocols. 100% offline accessible without cellular data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekTextSecondary
                )
            }
        }

        // Interactive CPR Metronome Tool Card
        item {
            CprMetronomeCard(
                isRunning = isMetronomeRunning,
                beatPulse = cprBeatPulse,
                onToggle = onToggleMetronome
            )
        }

        // Accordion Guide Cards
        items(FirstAidData.guides, key = { it.id }) { guide ->
            val isExpanded = expandedMap[guide.id] == true
            FirstAidAccordionCard(
                guide = guide,
                isExpanded = isExpanded,
                onToggle = {
                    expandedMap[guide.id] = !isExpanded
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CprMetronomeCard(
    isRunning: Boolean,
    beatPulse: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cpr_metronome_card"),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isRunning) SosRedBright else SleekBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .scale(if (beatPulse) 1.2f else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRunning) SosRed else SleekSurfaceVariant)
                        .border(1.dp, if (isRunning) SosRedBorder else SleekBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heartbeat",
                        tint = if (isRunning) Color.White else EmergencyAmber,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = "CPR Metronome (110 BPM)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                    Text(
                        text = if (isRunning) "Compress on every audio beat & haptic tap" else "Tap Start for AHA 100-120 BPM tempo",
                        style = MaterialTheme.typography.bodySmall,
                        color = SleekTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) SleekSurfaceVariant else SosRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isRunning) SleekBorder else SosRedBorder.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("toggle_cpr_metronome_btn")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Stop" else "Start",
                    tint = if (isRunning) SleekTextPrimary else Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isRunning) "Stop" else "Start", color = if (isRunning) SleekTextPrimary else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FirstAidAccordionCard(
    guide: FirstAidGuide,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("first_aid_guide_${guide.id}"),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row (Clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (guide.priority) {
                            "IMMEDIATE" -> SosRed.copy(alpha = 0.2f)
                            "URGENT" -> EmergencyAmber.copy(alpha = 0.2f)
                            else -> EmergencyGreen.copy(alpha = 0.2f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (guide.priority) {
                                "IMMEDIATE" -> SosRedBorder.copy(alpha = 0.4f)
                                "URGENT" -> EmergencyAmber.copy(alpha = 0.4f)
                                else -> EmergencyGreen.copy(alpha = 0.4f)
                            }
                        ),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = guide.category,
                                tint = when (guide.priority) {
                                    "IMMEDIATE" -> SosRedBright
                                    "URGENT" -> EmergencyAmber
                                    else -> EmergencyGreen
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = guide.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextPrimary
                            )
                        }
                        Text(
                            text = guide.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = SleekTextSecondary
                )
            }

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Critical Warning Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SosRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = SosRedBright, modifier = Modifier.size(20.dp))
                            Text(
                                text = guide.warning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF8A80)
                            )
                        }
                    }

                    // Steps
                    Text(
                        text = "STEP-BY-STEP ACTION PROTOCOL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekTextMuted,
                        letterSpacing = 1.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        guide.steps.forEach { step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SleekAccent,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${step.stepNumber}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = SleekOnAccent
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(step.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SleekTextPrimary)
                                    Text(
                                        step.description,
                                        fontSize = 13.sp,
                                        color = SleekTextSecondary
                                    )
                                    step.tip?.let { tip ->
                                        Text(
                                            text = "💡 Tip: $tip",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = EmergencyAmber,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // DOs and DON'Ts side by side or stacked
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("KEY CHECKLIST", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SleekTextPrimary)

                            guide.doList.forEach { item ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = "Do", tint = EmergencyGreen, modifier = Modifier.size(16.dp))
                                    Text(item, fontSize = 12.sp, color = SleekTextSecondary)
                                }
                            }

                            guide.doNotList.forEach { item ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, contentDescription = "Do Not", tint = SosRedBright, modifier = Modifier.size(16.dp))
                                    Text(item, fontSize = 12.sp, color = SleekTextSecondary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

