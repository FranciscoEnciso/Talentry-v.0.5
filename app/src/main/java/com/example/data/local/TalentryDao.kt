package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VacancyDao {
    @Query("SELECT * FROM vacancies ORDER BY createdAt DESC")
    fun getAllVacancies(): Flow<List<Vacancy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacancy(vacancy: Vacancy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVacancies(vacancies: List<Vacancy>)

    @Update
    suspend fun updateVacancy(vacancy: Vacancy)

    @Query("DELETE FROM vacancies WHERE id = :id")
    suspend fun deleteVacancyById(id: String)
}

@Dao
interface CandidateDao {
    @Query("SELECT * FROM candidates ORDER BY createdAt DESC")
    fun getAllCandidates(): Flow<List<Candidate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: Candidate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidates(candidates: List<Candidate>)

    @Update
    suspend fun updateCandidate(candidate: Candidate)

    @Query("DELETE FROM candidates WHERE id = :id")
    suspend fun deleteCandidateById(id: String)
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY lastUpdated DESC")
    fun getAllApplications(): Flow<List<Application>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: Application)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(applications: List<Application>)

    @Update
    suspend fun updateApplication(application: Application)

    @Query("UPDATE applications SET stage = :newStage, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateApplicationStage(id: String, newStage: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface InterviewDao {
    @Query("SELECT * FROM interviews ORDER BY scheduledDateTime ASC")
    fun getAllInterviews(): Flow<List<Interview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterview(interview: Interview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviews(interviews: List<Interview>)

    @Update
    suspend fun updateInterview(interview: Interview)

    @Query("UPDATE interviews SET status = :status, feedback = :feedback WHERE id = :id")
    suspend fun updateInterviewResult(id: String, status: String, feedback: String)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setTaskCompleted(id: String, isCompleted: Boolean)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
}
