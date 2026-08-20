package com.example.data.repository

import com.example.data.db.EmergencyContactDao
import com.example.data.model.EmergencyContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EmergencyContactRepository(
    private val contactDao: EmergencyContactDao
) {
    val allContacts: Flow<List<EmergencyContact>> = contactDao.getAllContacts()

    suspend fun getPrimaryContact(): EmergencyContact? = withContext(Dispatchers.IO) {
        contactDao.getPrimaryContact()
    }

    suspend fun addContact(contact: EmergencyContact): Long = withContext(Dispatchers.IO) {
        val id = contactDao.insertContact(contact)
        if (contact.isPrimary) {
            contactDao.setPrimaryContact(id)
        }
        id
    }

    suspend fun updateContact(contact: EmergencyContact) = withContext(Dispatchers.IO) {
        contactDao.updateContact(contact)
        if (contact.isPrimary) {
            contactDao.setPrimaryContact(contact.id)
        }
    }

    suspend fun deleteContact(contact: EmergencyContact) = withContext(Dispatchers.IO) {
        contactDao.deleteContact(contact)
    }

    suspend fun deleteContactById(id: Long) = withContext(Dispatchers.IO) {
        contactDao.deleteContactById(id)
    }

    suspend fun setPrimaryContact(id: Long) = withContext(Dispatchers.IO) {
        contactDao.setPrimaryContact(id)
    }
}
