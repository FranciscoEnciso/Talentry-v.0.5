package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TalentryRepository(private val database: TalentryDatabase) {

    val vacancies: Flow<List<Vacancy>> = database.vacancyDao().getAllVacancies()
    val candidates: Flow<List<Candidate>> = database.candidateDao().getAllCandidates()
    val applications: Flow<List<Application>> = database.applicationDao().getAllApplications()
    val interviews: Flow<List<Interview>> = database.interviewDao().getAllInterviews()
    val tasks: Flow<List<Task>> = database.taskDao().getAllTasks()

    init {
        // Seed database if empty
        CoroutineScope(Dispatchers.IO).launch {
            if (database.vacancyDao().getAllVacancies().first().isEmpty()) {
                seedInitialData()
            }
        }
    }

    // Vacancy actions
    suspend fun addVacancy(vacancy: Vacancy) = database.vacancyDao().insertVacancy(vacancy)
    suspend fun updateVacancy(vacancy: Vacancy) = database.vacancyDao().updateVacancy(vacancy)
    suspend fun deleteVacancy(id: String) = database.vacancyDao().deleteVacancyById(id)

    // Candidate actions
    suspend fun addCandidate(candidate: Candidate) = database.candidateDao().insertCandidate(candidate)
    suspend fun updateCandidate(candidate: Candidate) = database.candidateDao().updateCandidate(candidate)
    suspend fun deleteCandidate(id: String) = database.candidateDao().deleteCandidateById(id)

    // Application actions
    suspend fun addApplication(application: Application) = database.applicationDao().insertApplication(application)
    suspend fun updateApplicationStage(id: String, stage: String) = database.applicationDao().updateApplicationStage(id, stage)

    // Interview actions
    suspend fun addInterview(interview: Interview) = database.interviewDao().insertInterview(interview)
    suspend fun updateInterviewResult(id: String, status: String, feedback: String) =
        database.interviewDao().updateInterviewResult(id, status, feedback)

    // Task actions
    suspend fun addTask(task: Task) = database.taskDao().insertTask(task)
    suspend fun toggleTaskCompleted(id: String, isCompleted: Boolean) = database.taskDao().setTaskCompleted(id, isCompleted)

    private suspend fun seedInitialData() {
        val seedVacancies = listOf(
            Vacancy(
                id = "VAC-101",
                title = "Operador de Montacargas",
                branch = "Sucursal Norte CDMX",
                department = "Logística & Almacén",
                positionsOpen = 15,
                positionsFilled = 9,
                status = "Activa",
                salaryRange = "$12,500 - $14,000 MXN",
                description = "Operación de montacargas hombre sentado/parado en cedis de alta velocidad. Turnos rotativos.",
                requirements = "DC-3 vigente, experiencia mínima 1 año, certificado secundaria."
            ),
            Vacancy(
                id = "VAC-102",
                title = "Cajero Operativo Nocturno",
                branch = "Guadalajara Sur",
                department = "Operaciones Tienda",
                positionsOpen = 8,
                positionsFilled = 6,
                status = "Activa",
                salaryRange = "$10,800 - $12,000 MXN",
                description = "Manejo de caja registradora, conteo de efectivo, atención a clientes y acomodo de mercancía nocturna.",
                requirements = "Preparatoria trunca/concluida, disponibilidad nocturna, carta antecedente no penales."
            ),
            Vacancy(
                id = "VAC-103",
                title = "Promotor de Campo - Cambaceo",
                branch = "Monterrey NL",
                department = "Ventas de Campo",
                positionsOpen = 20,
                positionsFilled = 14,
                status = "Activa",
                salaryRange = "$11,000 + Comisiones",
                description = "Prospección de clientes en campo, colocación de servicios financieros operativos y cierre en sitio.",
                requirements = "Gusto por las ventas en campo, facilidad de palabra, preparatoria."
            ),
            Vacancy(
                id = "VAC-104",
                title = "Auxiliar de Mantenimiento General",
                branch = "Puebla Centro",
                department = "Servicios Generales",
                positionsOpen = 5,
                positionsFilled = 5,
                status = "Cerrada",
                salaryRange = "$11,500 MXN",
                description = "Reparaciones menores de plomería, pintura y electricidad en instalaciones operativas.",
                requirements = "Experiencia 2 años comprobable, certificado secundaria."
            ),
            Vacancy(
                id = "VAC-105",
                title = "Agente de Soporte Telefónico Bilingüe",
                branch = "Querétaro Park",
                department = "Contact Center",
                positionsOpen = 12,
                positionsFilled = 4,
                status = "Activa",
                salaryRange = "$18,000 - $21,000 MXN",
                description = "Atención a clientes internacionales vía telefónica y chat para soporte operativo técnico básico.",
                requirements = "Inglés conversacional B2/C1, preparatoria terminada."
            )
        )

        val seedCandidates = listOf(
            Candidate(
                id = "CAN-501",
                fullName = "Roberto Carlos Gómez",
                phone = "55 4123 8890",
                email = "roberto.gomez@gmail.com",
                city = "Ciudad de México",
                experienceYears = 3,
                currentStatus = "Entrevista",
                aiMatchScore = 94,
                aiSummary = "Cuenta con constancia DC-3 vigente de montacargas. Disponibilidad inmediata para turnos nocturnos.",
                notes = "Confirmó asistencia a entrevista presencial el día de hoy a las 10:30 AM.",
                appliedVacancyId = "VAC-101",
                appliedVacancyTitle = "Operador de Montacargas"
            ),
            Candidate(
                id = "CAN-502",
                fullName = "María Fernanda López",
                phone = "33 1890 2234",
                email = "mafer.lopez@yahoo.com",
                city = "Guadalajara",
                experienceYears = 2,
                currentStatus = "Llamada Pendiente",
                aiMatchScore = 88,
                aiSummary = "Perfil sólido en cobro de efectivo e inventarios rápidos. Experiencia previa en tiendas de conveniencia.",
                notes = "Llamar entre 2:00 PM y 4:00 PM para filtro inicial.",
                appliedVacancyId = "VAC-102",
                appliedVacancyTitle = "Cajero Operativo Nocturno"
            ),
            Candidate(
                id = "CAN-503",
                fullName = "Alejandro Morales Treviño",
                phone = "81 9023 5511",
                email = "alex.morales@hotmail.com",
                city = "Monterrey",
                experienceYears = 4,
                currentStatus = "Documentos",
                aiMatchScore = 91,
                aiSummary = "Gran empuje comercial. Ex-promotor con excelentes métricas de colocación en zona metropolitana.",
                notes = "Documentación completa entregada. En espera de cotejo de referencias laborales.",
                appliedVacancyId = "VAC-103",
                appliedVacancyTitle = "Promotor de Campo - Cambaceo"
            ),
            Candidate(
                id = "CAN-504",
                fullName = "Valeria Patricia Méndez",
                phone = "44 2309 8812",
                email = "valeria.mendez@outlook.com",
                city = "Querétaro",
                experienceYears = 1,
                currentStatus = "Contratado",
                aiMatchScore = 96,
                aiSummary = "Nivel de inglés C1 verificado en filtro. Experiencia previa en campañas norteamericanas.",
                notes = "Firmó contrato. Inicia capacitación el próximo lunes.",
                appliedVacancyId = "VAC-105",
                appliedVacancyTitle = "Agente de Soporte Telefónico Bilingüe"
            ),
            Candidate(
                id = "CAN-505",
                fullName = "Jesús Alberto Ramírez",
                phone = "55 9912 3456",
                email = "jesus.ramirez@live.com.mx",
                city = "Estado de México",
                experienceYears = 5,
                currentStatus = "Postulado",
                aiMatchScore = 79,
                aiSummary = "Experiencia técnica comprobada en manejo de inventarios, sin embargo requiere actualización de DC-3.",
                notes = "Agendar filtro telefónico inicial para confirmar disponibilidad de renovación DC-3.",
                appliedVacancyId = "VAC-101",
                appliedVacancyTitle = "Operador de Montacargas"
            )
        )

        val seedApplications = listOf(
            Application("APP-1", "CAN-501", "Roberto Carlos Gómez", "VAC-101", "Operador de Montacargas", "Entrevista", 5),
            Application("APP-2", "CAN-502", "María Fernanda López", "VAC-102", "Cajero Operativo Nocturno", "Llamada / Filtro", 4),
            Application("APP-3", "CAN-503", "Alejandro Morales Treviño", "VAC-103", "Promotor de Campo - Cambaceo", "Documentos", 5),
            Application("APP-4", "CAN-504", "Valeria Patricia Méndez", "VAC-105", "Agente de Soporte Telefónico", "Oferta / Contratado", 5),
            Application("APP-5", "CAN-505", "Jesús Alberto Ramírez", "VAC-101", "Operador de Montacargas", "Postulado", 3)
        )

        val seedInterviews = listOf(
            Interview(
                id = "INT-1",
                candidateId = "CAN-501",
                candidateName = "Roberto Carlos Gómez",
                vacancyTitle = "Operador de Montacargas",
                scheduledDateTime = "2026-07-30 10:30 AM",
                type = "Presencial",
                locationOrLink = "Cedis Norte, Sala B",
                interviewer = "Carlos Mendoza (Senior)",
                status = "Programada"
            ),
            Interview(
                id = "INT-2",
                candidateId = "CAN-502",
                candidateName = "María Fernanda López",
                vacancyTitle = "Cajero Operativo Nocturno",
                scheduledDateTime = "2026-07-30 02:00 PM",
                type = "Telefónica",
                locationOrLink = "+52 33 1890 2234",
                interviewer = "Ana Sofía Ruiz",
                status = "Programada"
            ),
            Interview(
                id = "INT-3",
                candidateId = "CAN-503",
                candidateName = "Alejandro Morales Treviño",
                vacancyTitle = "Promotor de Campo - Cambaceo",
                scheduledDateTime = "2026-07-29 11:00 AM",
                type = "Videollamada",
                locationOrLink = "meet.google.com/talentry-mty",
                interviewer = "Jorge Garza",
                status = "Completada",
                feedback = "Aprobado con excelencia. Pasa a recepción de documentos de contratación."
            )
        )

        val seedTasks = listOf(
            Task("TSK-1", "Realizar filtro telefónico inicial a María López", "Llamada", "Alta", "María Fernanda López", "02:00 PM"),
            Task("TSK-2", "Entrevista presencial Operador Montacargas", "Entrevista", "Alta", "Roberto Carlos Gómez", "10:30 AM"),
            Task("TSK-3", "Validar antecedentes laborales y referencias", "Documentación", "Media", "Alejandro Morales", "04:00 PM"),
            Task("TSK-4", "Envío de carta oferta laboral digital", "Contratación", "Alta", "Valeria Patricia Méndez", "05:00 PM"),
            Task("TSK-5", "Seguimiento de candidatos pendientes de filtro", "Seguimiento", "Baja", "Jesús Alberto Ramírez", "06:00 PM")
        )

        database.vacancyDao().insertVacancies(seedVacancies)
        database.candidateDao().insertCandidates(seedCandidates)
        database.applicationDao().insertApplications(seedApplications)
        database.interviewDao().insertInterviews(seedInterviews)
        database.taskDao().insertTasks(seedTasks)
    }

    companion object {
        val staticBranches = listOf(
            Branch("B-1", "Sucursal Norte CDMX", "Ciudad de México", 15),
            Branch("B-2", "Guadalajara Sur", "Guadalajara", 8),
            Branch("B-3", "Monterrey NL", "Monterrey", 20),
            Branch("B-4", "Puebla Centro", "Puebla", 2),
            Branch("B-5", "Querétaro Park", "Querétaro", 12)
        )

        val staticRecruiters = listOf(
            Recruiter("R-1", "Carlos Mendoza", "Líder de Reclutamiento Masivo", 24, 18),
            Recruiter("R-2", "Ana Sofía Ruiz", "Reclutadora Operativa", 19, 14),
            Recruiter("R-3", "Jorge Garza", "Reclutador de Campo", 31, 22),
            Recruiter("R-4", "Lucía Fernández", "Especialista en Onboarding", 15, 12)
        )
    }
}
