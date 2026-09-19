package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExperimentRecord::class,
        BktSkillRecord::class,
        ResearchStudentSample::class,
        ExperimentSession::class,
        StudentMistakeLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LabDatabase : RoomDatabase() {
    abstract fun labDao(): LabDao

    companion object DatabaseProvider {
        @Volatile
        private var INSTANCE: LabDatabase? = null

        fun getInstance(context: Context): LabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabDatabase::class.java,
                    "chemlab_adaptive_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(): LabDatabase {
            return INSTANCE ?: throw IllegalStateException(
                "Database chưa được khởi tạo. Gọi DatabaseProvider.getInstance(context) trước."
            )
        }

        /**
         * Tạo in-memory database cho unit testing.
         */
        fun inMemory(context: Context): LabDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                LabDatabase::class.java
            ).build()
        }

        fun close() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
