package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EmergencyNumber
import com.example.data.GlobalEmergencyData
import com.example.service.EmergencyCommunicationHelper
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
import com.example.ui.theme.SosRedBright
import com.example.ui.theme.SosRedDark

@Composable
fun GlobalNumbersSection(
    selectedRegion: String,
    searchQuery: String,
    onSelectRegion: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filteredNumbers = remember(selectedRegion, searchQuery) {
        GlobalEmergencyData.numbers.filter { item ->
            val matchesRegion = selectedRegion == "ALL" || item.regionCode.equals(selectedRegion, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.countryOrRegion.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    item.number.contains(searchQuery, ignoreCase = true)
            matchesRegion && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_emergency_numbers_input"),
            placeholder = { Text("Search country, service, or number...", color = SleekTextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = SleekAccent)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SleekTextMuted)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SleekAccent,
                unfocusedBorderColor = SleekBorder,
                focusedTextColor = SleekTextPrimary,
                unfocusedTextColor = SleekTextPrimary,
                focusedContainerColor = SleekSurface,
                unfocusedContainerColor = SleekSurface
            ),
            shape = RoundedCornerShape(14.dp)
        )

        // Region Filter Chips Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlobalEmergencyData.regions.forEach { (code, label) ->
                FilterChip(
                    selected = selectedRegion == code,
                    onClick = { onSelectRegion(code) },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (selectedRegion == code) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SleekAccent,
                        selectedLabelColor = SleekOnAccent,
                        containerColor = SleekSurfaceVariant,
                        labelColor = SleekTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedRegion == code,
                        borderColor = if (selectedRegion == code) SleekAccent else SleekBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // List of emergency numbers
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredNumbers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No emergency lines match '$searchQuery'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SleekTextSecondary
                        )
                    }
                }
            } else {
                items(filteredNumbers, key = { it.id }) { item ->
                    EmergencyNumberCard(
                        numberItem = item,
                        onCall = { EmergencyCommunicationHelper.dialNumber(context, item.number) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyNumberCard(
    numberItem: EmergencyNumber,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_number_${numberItem.id}"),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Icon Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (numberItem.iconType) {
                    "POLICE" -> EmergencyBlue.copy(alpha = 0.15f)
                    "MEDICAL" -> EmergencyGreen.copy(alpha = 0.15f)
                    "FIRE" -> EmergencyAmber.copy(alpha = 0.15f)
                    "CRISIS" -> SleekAccent.copy(alpha = 0.15f)
                    else -> SosRed.copy(alpha = 0.15f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (numberItem.iconType) {
                        "POLICE" -> EmergencyBlue.copy(alpha = 0.3f)
                        "MEDICAL" -> EmergencyGreen.copy(alpha = 0.3f)
                        "FIRE" -> EmergencyAmber.copy(alpha = 0.3f)
                        "CRISIS" -> SleekAccent.copy(alpha = 0.3f)
                        else -> SosRedBorder.copy(alpha = 0.3f)
                    }
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (numberItem.iconType) {
                            "POLICE" -> Icons.Default.LocalPolice
                            "MEDICAL" -> Icons.Default.LocalHospital
                            "FIRE" -> Icons.Default.LocalFireDepartment
                            "CRISIS" -> Icons.Default.Favorite
                            else -> Icons.Default.Shield
                        },
                        contentDescription = numberItem.category,
                        tint = when (numberItem.iconType) {
                            "POLICE" -> EmergencyBlue
                            "MEDICAL" -> EmergencyGreen
                            "FIRE" -> EmergencyAmber
                            "CRISIS" -> SleekAccent
                            else -> SosRedBright
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = numberItem.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextPrimary
                    )
                }
                Text(
                    text = "${numberItem.countryOrRegion} • ${numberItem.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekTextSecondary
                )
                Text(
                    text = numberItem.description,
                    fontSize = 11.sp,
                    color = SleekTextMuted,
                    maxLines = 2
                )
            }

            // Quick Call Button
            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dial_btn_${numberItem.number}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(15.dp))
                    Text(
                        text = numberItem.number,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

