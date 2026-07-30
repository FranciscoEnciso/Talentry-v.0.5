package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [Vacancy::class, Candidate::class, Application::class, Interview::class, Task::class],
    version = 1,
    exportSchema = false
)
abstract class TalentryDatabase : RoomDatabase() {
    abstract fun vacancyDao(): VacancyDao
    abstract fun candidateDao(): CandidateDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun interviewDao(): InterviewDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TalentryDatabase? = null

        fun getDatabase(context: Context): TalentryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TalentryDatabase::class.java,
                    "talentry_recruitment_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
