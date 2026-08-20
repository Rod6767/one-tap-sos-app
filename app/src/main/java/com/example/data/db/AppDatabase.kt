package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EmergencyContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [EmergencyContact::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sos_emergency_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialContacts(database.emergencyContactDao())
                    }
                }
            }

            suspend fun populateInitialContacts(dao: EmergencyContactDao) {
                if (dao.getContactCount() == 0) {
                    val defaultContacts = listOf(
                        EmergencyContact(
                            name = "Mom (Primary Contact)",
                            phone = "18005550199",
                            relationship = "Parent / Family",
                            isPrimary = true,
                            notes = "Lives 10 mins away, knows medical history"
                        ),
                        EmergencyContact(
                            name = "Alex Morgan",
                            phone = "18005550142",
                            relationship = "Spouse / Partner",
                            isPrimary = false,
                            notes = "Work number available in emergencies"
                        ),
                        EmergencyContact(
                            name = "Dr. Sarah Adams",
                            phone = "18005550188",
                            relationship = "Primary Physician",
                            isPrimary = false,
                            notes = "Clinic Line: St. Jude Health Center"
                        )
                    )
                    dao.insertContacts(defaultContacts)
                }
            }
        }
    }
}
