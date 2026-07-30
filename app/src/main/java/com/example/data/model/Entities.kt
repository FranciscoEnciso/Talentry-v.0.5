package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacancies")
data class Vacancy(
    @PrimaryKey val id: String,
    val title: String,
    val branch: String,
    val department: String,
    val positionsOpen: Int,
    val positionsFilled: Int,
    val status: String, // "Activa", "En Pausa", "Cerrada", "Borrador"
    val salaryRange: String,
    val description: String,
    val requirements: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "candidates")
data class Candidate(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val city: String,
    val experienceYears: Int,
    val currentStatus: String, // "Postulado", "Llamada Pendiente", "Entrevista", "Documentos", "Contratado", "Descartado"
    val aiMatchScore: Int, // 0 to 100
    val aiSummary: String,
    val notes: String,
    val appliedVacancyId: String,
    val appliedVacancyTitle: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "applications")
data class Application(
    @PrimaryKey val id: String,
    val candidateId: String,
    val candidateName: String,
    val vacancyId: String,
    val vacancyTitle: String,
    val stage: String, // "Postulado", "Llamada / Filtro", "Entrevista", "Documentos", "Oferta / Contratado", "Rechazado"
    val rating: Int, // 1 to 5
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "interviews")
data class Interview(
    @PrimaryKey val id: String,
    val candidateId: String,
    val candidateName: String,
    val vacancyTitle: String,
    val scheduledDateTime: String,
    val type: String, // "Presencial", "Videollamada", "Telefónica"
    val locationOrLink: String,
    val interviewer: String,
    val status: String, // "Programada", "Completada", "No Asistió", "Cancelada", "Reagendada"
    val feedback: String = ""
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Llamada", "Entrevista", "Documentación", "Contratación", "Seguimiento"
    val priority: String, // "Alta", "Media", "Baja"
    val candidateName: String,
    val timeSlot: String,
    val isCompleted: Boolean = false
)

data class Branch(
    val id: String,
    val name: String,
    val city: String,
    val activeVacanciesCount: Int
)

data class Recruiter(
    val id: String,
    val name: String,
    val role: String,
    val activeCandidates: Int,
    val monthlyHires: Int
)

data class ReportMetrics(
    val totalActiveVacancies: Int,
    val totalCandidates: Int,
    val totalHiresThisMonth: Int,
    val avgDaysToHire: Double,
    val interviewConversionRate: Double
)
