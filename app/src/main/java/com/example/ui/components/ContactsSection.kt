package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.service.EmergencyCommunicationHelper
import com.example.service.LocationInfo
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
fun ContactsSection(
    contacts: List<EmergencyContact>,
    locationInfo: LocationInfo,
    onAddContact: (name: String, phone: String, relationship: String, isPrimary: Boolean, notes: String) -> Unit,
    onUpdateContact: (EmergencyContact) -> Unit,
    onDeleteContact: (EmergencyContact) -> Unit,
    onSetPrimary: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }
    var deletingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (contacts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = SleekSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "No contacts",
                            tint = SleekAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Emergency Contacts Saved", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Add family members, doctors, or trusted neighbors who should receive your instant distress alerts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SleekTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add First Emergency Contact", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EMERGENCY CONTACTS (${contacts.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = SleekTextMuted,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Persisted locally. Direct one-tap SOS and calling enabled.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SleekTextSecondary
                            )
                        }
                    }
                }

                items(contacts, key = { it.id }) { contact ->
                    ContactCardItem(
                        contact = contact,
                        locationInfo = locationInfo,
                        onCall = { EmergencyCommunicationHelper.dialNumber(context, contact.phone) },
                        onSendSms = {
                            EmergencyCommunicationHelper.sendSms(
                                context = context,
                                phoneNumber = contact.phone,
                                messageBody = locationInfo.sosMessageBody
                            )
                        },
                        onEdit = { editingContact = contact },
                        onDelete = { deletingContact = contact },
                        onTogglePrimary = { onSetPrimary(contact.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
                }
            }
        }

        // Floating Action Button to Add Contact
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_contact_fab"),
            containerColor = SosRedBright,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Contact")
        }

        // Add Contact Dialog
        if (showAddDialog) {
            ContactFormDialog(
                title = "Add Emergency Contact",
                initialContact = null,
                onDismiss = { showAddDialog = false },
                onSave = { name, phone, rel, isPrimary, notes ->
                    onAddContact(name, phone, rel, isPrimary, notes)
                    showAddDialog = false
                }
            )
        }

        // Edit Contact Dialog
        editingContact?.let { contactToEdit ->
            ContactFormDialog(
                title = "Edit Emergency Contact",
                initialContact = contactToEdit,
                onDismiss = { editingContact = null },
                onSave = { name, phone, rel, isPrimary, notes ->
                    onUpdateContact(
                        contactToEdit.copy(
                            name = name,
                            phone = phone,
                            relationship = rel,
                            isPrimary = isPrimary,
                            notes = notes
                        )
                    )
                    editingContact = null
                }
            )
        }

        // Delete Confirmation Dialog
        deletingContact?.let { contactToDelete ->
            AlertDialog(
                onDismissRequest = { deletingContact = null },
                containerColor = SleekSurface,
                title = { Text("Delete Contact?", color = SleekTextPrimary) },
                text = { Text("Are you sure you want to remove ${contactToDelete.name} from your emergency contacts list?", color = SleekTextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteContact(contactToDelete)
                            deletingContact = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { deletingContact = null },
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = SleekTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ContactCardItem(
    contact: EmergencyContact,
    locationInfo: LocationInfo,
    onCall: () -> Unit,
    onSendSms: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePrimary: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("contact_item_${contact.id}"),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (contact.isPrimary) 1.5.dp else 1.dp,
            if (contact.isPrimary) EmergencyAmber else SleekBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Name, Relationship badge, Primary star, Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        color = if (contact.isPrimary) EmergencyAmber.copy(alpha = 0.2f) else SleekSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (contact.isPrimary) EmergencyAmber.copy(alpha = 0.4f) else SleekBorder),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = contact.name.firstOrNull()?.uppercase() ?: "C",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (contact.isPrimary) EmergencyAmber else SleekAccent
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextPrimary
                            )
                            if (contact.isPrimary) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmergencyAmber,
                                    modifier = Modifier.padding(start = 2.dp)
                                ) {
                                    Text(
                                        text = "PRIMARY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${contact.relationship} • ${contact.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SleekTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onTogglePrimary, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = if (contact.isPrimary) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Set Primary",
                            tint = if (contact.isPrimary) EmergencyAmber else SleekTextMuted
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SleekTextSecondary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SosRedBright)
                    }
                }
            }

            if (contact.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SleekSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${contact.notes}",
                        fontSize = 12.sp,
                        color = SleekTextSecondary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Direct Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Text("Call Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = onSendSms,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SosRedBorder.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = "SMS", tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Direct SOS SMS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactFormDialog(
    title: String,
    initialContact: EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relationship: String, isPrimary: Boolean, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var phone by remember { mutableStateOf(initialContact?.phone ?: "") }
    var relationship by remember { mutableStateOf(initialContact?.relationship ?: "Family") }
    var isPrimary by remember { mutableStateOf(initialContact?.isPrimary ?: false) }
    var notes by remember { mutableStateOf(initialContact?.notes ?: "") }
    var isError by remember { mutableStateOf(false) }

    val presetRelationships = listOf("Family", "Spouse", "Parent", "Doctor", "Friend", "Neighbor")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SleekSurface,
        title = { Text(title, fontWeight = FontWeight.Bold, color = SleekTextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; isError = false },
                    label = { Text("Contact Full Name *") },
                    singleLine = true,
                    isError = isError && name.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekAccent,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = SleekTextPrimary,
                        unfocusedTextColor = SleekTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; isError = false },
                    label = { Text("Phone Number *") },
                    placeholder = { Text("e.g. +1 555-0199") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = isError && phone.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekAccent,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = SleekTextPrimary,
                        unfocusedTextColor = SleekTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_phone_input")
                )

                Column {
                    Text("Relationship", fontSize = 12.sp, color = SleekTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetRelationships.take(3).forEach { rel ->
                            OutlinedButton(
                                onClick = { relationship = rel },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (relationship == rel) SleekAccent.copy(alpha = 0.2f) else Color.Transparent,
                                    contentColor = if (relationship == rel) SleekAccent else SleekTextSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (relationship == rel) SleekAccent else SleekBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(rel, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetRelationships.drop(3).forEach { rel ->
                            OutlinedButton(
                                onClick = { relationship = rel },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (relationship == rel) SleekAccent.copy(alpha = 0.2f) else Color.Transparent,
                                    contentColor = if (relationship == rel) SleekAccent else SleekTextSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (relationship == rel) SleekAccent else SleekBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(rel, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Medical / Access Notes (Optional)") },
                    placeholder = { Text("e.g., Has house key, knows blood type") },
                    singleLine = false,
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekAccent,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = SleekTextPrimary,
                        unfocusedTextColor = SleekTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Designate as Primary Contact", fontSize = 14.sp, color = SleekTextPrimary)
                    Switch(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmergencyAmber,
                            checkedTrackColor = EmergencyAmber.copy(alpha = 0.3f),
                            uncheckedThumbColor = SleekTextMuted,
                            uncheckedTrackColor = SleekSurfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone, relationship, isPrimary, notes)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_contact_btn")
            ) {
                Text("Save Contact", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel", color = SleekTextSecondary)
            }
        }
    )
}

