package com.example.ui.viewmodel

import android.app.Application as AndroidApp
import com.example.data.model.Application as RecruitmentApp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TalentryDatabase
import com.example.data.model.*
import com.example.data.repository.TalentryRepository
import com.example.data.service.GeminiAiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TalentryViewModel(application: AndroidApp) : AndroidViewModel(application) {

    private val repository = TalentryRepository(TalentryDatabase.getDatabase(application))
    private val aiService = GeminiAiService()

    // Database flows
    val vacancies: StateFlow<List<Vacancy>> = repository.vacancies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val candidates: StateFlow<List<Candidate>> = repository.candidates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applications: StateFlow<List<RecruitmentApp>> = repository.applications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val interviews: StateFlow<List<Interview>> = repository.interviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBranchFilter = MutableStateFlow("Todas")
    val selectedBranchFilter: StateFlow<String> = _selectedBranchFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("Todos")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // AI State
    private val _aiOutputText = MutableStateFlow("")
    val aiOutputText: StateFlow<String> = _aiOutputText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBranchFilter(branch: String) {
        _selectedBranchFilter.value = branch
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // CRUD operations
    fun addVacancy(vacancy: Vacancy) {
        viewModelScope.launch { repository.addVacancy(vacancy) }
    }

    fun updateVacancy(vacancy: Vacancy) {
        viewModelScope.launch { repository.updateVacancy(vacancy) }
    }

    fun deleteVacancy(id: String) {
        viewModelScope.launch { repository.deleteVacancy(id) }
    }

    fun addCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.addCandidate(candidate)
            // Automatically add application entry
            repository.addApplication(
                RecruitmentApp(
                    id = "APP-${System.currentTimeMillis() % 10000}",
                    candidateId = candidate.id,
                    candidateName = candidate.fullName,
                    vacancyId = candidate.appliedVacancyId,
                    vacancyTitle = candidate.appliedVacancyTitle,
                    stage = "Postulado",
                    rating = 4
                )
            )
        }
    }

    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch { repository.updateCandidate(candidate) }
    }

    fun deleteCandidate(id: String) {
        viewModelScope.launch { repository.deleteCandidate(id) }
    }

    fun updateApplicationStage(applicationId: String, candidateId: String, newStage: String) {
        viewModelScope.launch {
            repository.updateApplicationStage(applicationId, newStage)
            // Sync currentStatus in candidate entity
            val currentCandidates = candidates.value
            currentCandidates.find { it.id == candidateId }?.let { candidate ->
                repository.updateCandidate(candidate.copy(currentStatus = newStage))
            }
        }
    }

    fun addInterview(interview: Interview) {
        viewModelScope.launch { repository.addInterview(interview) }
    }

    fun updateInterviewResult(id: String, status: String, feedback: String) {
        viewModelScope.launch { repository.updateInterviewResult(id, status, feedback) }
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repository.addTask(task) }
    }

    fun toggleTaskCompleted(id: String, isCompleted: Boolean) {
        viewModelScope.launch { repository.toggleTaskCompleted(id, isCompleted) }
    }

    // AI Operations
    fun generateJobDescriptionWithAi(title: String, branch: String, requirements: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateJobDescription(title, branch, requirements)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun analyzeCandidateFitWithAi(candidateName: String, experience: String, vacancyTitle: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.analyzeCandidateFit(candidateName, experience, vacancyTitle)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun generateInterviewSummaryWithAi(candidateName: String, feedback: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateInterviewSummary(candidateName, feedback)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun generateAutoResponseWithAi(candidateName: String, stage: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateAutoResponse(candidateName, stage)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun clearAiOutput() {
        _aiOutputText.value = ""
    }

    // =========================================================================
    // DESKTOP FIRST / ADAPTIVE LAYOUT STATE
    // =========================================================================
    private val _isDesktopMode = MutableStateFlow(true)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    fun toggleDesktopMode() {
        _isDesktopMode.value = !_isDesktopMode.value
    }

    // =========================================================================
    // 1. FORMULARIOS INTERNOS (Native Form Builder & Submissions)
    // =========================================================================
    private val _formTemplates = MutableStateFlow(
        listOf(
            FormTemplate(
                id = "FORM-101",
                title = "Formulario Pre-Filtro Operativo",
                description = "Cuestionario rápido de calificación inicial para puestos de almacén y logística.",
                category = "Pre-Filtro",
                questions = listOf(
                    FormQuestion("Q1", "¿Cuentas con disponibilidad de rotar turnos?", QuestionType.YES_NO, true),
                    FormQuestion("Q2", "¿Cuántos años de experiencia tienes operando montacargas?", QuestionType.MULTIPLE_CHOICE, true, listOf("Sin experiencia", "1 a 2 años", "3 a 5 años", "Más de 5 años")),
                    FormQuestion("Q3", "¿Cuál es tu pretensión salarial mensual libre?", QuestionType.TEXT_SHORT, true),
                    FormQuestion("Q4", "Sube tu CV o Solicitud de Empleo firmada", QuestionType.FILE_UPLOAD, true)
                ),
                responseCount = 18,
                isActive = true
            ),
            FormTemplate(
                id = "FORM-102",
                title = "Carga de Documentación de Ingreso",
                description = "Solicitud oficial de documentos para candidatos en etapa pre-contratación.",
                category = "Documentación",
                questions = listOf(
                    FormQuestion("Q10", "Identificación Oficial vigente (INE frente y reverso)", QuestionType.FILE_UPLOAD, true),
                    FormQuestion("Q11", "Constancia de Situación Fiscal (RFC actualizada)", QuestionType.FILE_UPLOAD, true),
                    FormQuestion("Q12", "Número de Seguridad Social (NSS IMSS)", QuestionType.TEXT_SHORT, true),
                    FormQuestion("Q13", "¿Cuentas con certificado médico vigente?", QuestionType.YES_NO, true)
                ),
                responseCount = 12,
                isActive = true
            ),
            FormTemplate(
                id = "FORM-103",
                title = "Evaluación Técnica de Competencias",
                description = "Examen de normas de seguridad, manejo de inventario y KPI logísticos.",
                category = "Competencias",
                questions = listOf(
                    FormQuestion("Q20", "Describe cómo procedes si detectas un pallet con embalaje dañado", QuestionType.TEXT_LONG, true),
                    FormQuestion("Q21", "¿Conoces la normativa básica de seguridad NOM-006 de manejo de materiales?", QuestionType.YES_NO, true),
                    FormQuestion("Q22", "Adjunta tu certificado o licencia DC-3 de montacargas", QuestionType.FILE_UPLOAD, false)
                ),
                responseCount = 7,
                isActive = true
            )
        )
    )
    val formTemplates: StateFlow<List<FormTemplate>> = _formTemplates.asStateFlow()

    private val _formSubmissions = MutableStateFlow(
        listOf(
            FormSubmission(
                id = "SUB-801",
                formTemplateId = "FORM-101",
                formTitle = "Formulario Pre-Filtro Operativo",
                candidateId = "CAND-01",
                candidateName = "Carlos Ramírez",
                submittedAt = "Hace 2 horas",
                answers = listOf(
                    FormAnswerItem("¿Cuentas con disponibilidad de rotar turnos?", "Sí"),
                    FormAnswerItem("¿Cuántos años de experiencia tienes operando montacargas?", "3 a 5 años"),
                    FormAnswerItem("¿Cuál es tu pretensión salarial mensual libre?", "$14,500 MXN"),
                    FormAnswerItem("Sube tu CV o Solicitud de Empleo firmada", "cv_carlos_ramirez_2026.pdf", "https://talentry.app/files/cv_carlos.pdf")
                ),
                aiScore = 96
            ),
            FormSubmission(
                id = "SUB-802",
                formTemplateId = "FORM-102",
                formTitle = "Carga de Documentación de Ingreso",
                candidateId = "CAND-02",
                candidateName = "Ana Rodríguez",
                submittedAt = "Ayer 16:30",
                answers = listOf(
                    FormAnswerItem("Identificación Oficial vigente (INE frente y reverso)", "ine_ana_rodriguez.pdf", "https://talentry.app/files/ine_ana.pdf"),
                    FormAnswerItem("Constancia de Situación Fiscal (RFC actualizada)", "rfc_ana_2026.pdf", "https://talentry.app/files/rfc_ana.pdf"),
                    FormAnswerItem("Número de Seguridad Social (NSS IMSS)", "68149203810"),
                    FormAnswerItem("¿Cuentas con certificado médico vigente?", "Sí")
                ),
                aiScore = 94
            )
        )
    )
    val formSubmissions: StateFlow<List<FormSubmission>> = _formSubmissions.asStateFlow()

    fun addFormTemplate(template: FormTemplate) {
        _formTemplates.value = listOf(template) + _formTemplates.value
    }

    fun toggleFormTemplateStatus(templateId: String) {
        _formTemplates.value = _formTemplates.value.map {
            if (it.id == templateId) it.copy(isActive = !it.isActive) else it
        }
    }

    // =========================================================================
    // 2. WHATSAPP AUTOMATION & MOTOR DE REGLAS (No-Code Rules Engine)
    // =========================================================================
    private val _whatsAppRules = MutableStateFlow(
        listOf(
            WhatsAppRule(
                id = "RULE-01",
                title = "Confirmación de Interés 'SÍ'",
                triggerKeywords = listOf("sí", "si", "interesado", "claro", "me interesa"),
                actionType = RuleActionType.CHANGE_STAGE_AND_RESPOND,
                responseTemplate = "¡Excelente {{candidate_name}}! Hemos registrado tu interés para {{vacancy_title}}. Te comparto las opciones de horario para entrevista presencial.",
                newCandidateStage = "Entrevista",
                isEnabled = true
            ),
            WhatsAppRule(
                id = "RULE-02",
                title = "Solicitud de Documentos 'DOCUMENTOS'",
                triggerKeywords = listOf("documentos", "ine", "cv", "papeles", "adjunto"),
                actionType = RuleActionType.REQUEST_DOCUMENTATION,
                responseTemplate = "Gracias por tu mensaje {{candidate_name}}. Por favor completa tu expediente cargando tu INE y RFC en nuestro portal: https://talentry.app/form/FORM-102",
                newCandidateStage = "Documentos",
                isEnabled = true
            ),
            WhatsAppRule(
                id = "RULE-03",
                title = "Petición de Horarios 'HORARIO'",
                triggerKeywords = listOf("horario", "cita", "entrevista", "cuando", "agendar"),
                actionType = RuleActionType.OFFER_INTERVIEW_SLOTS,
                responseTemplate = "¡Hola {{candidate_name}}! Contamos con cupo mañana de 09:00 a 13:00 en la planta de Apodaca. ¿A qué hora te confirmo?",
                isEnabled = true
            ),
            WhatsAppRule(
                id = "RULE-04",
                title = "Escalamiento a Reclutador 'AYUDA'",
                triggerKeywords = listOf("ayuda", "duda", "problema", "humano", "reclutador", "urgente"),
                actionType = RuleActionType.ESCALATE_TO_RECRUITER,
                responseTemplate = "Hemos escalado tu consulta al reclutador senior Francisco. Te contactaremos personalmente por teléfono en breve.",
                isEnabled = true
            )
        )
    )
    val whatsAppRules: StateFlow<List<WhatsAppRule>> = _whatsAppRules.asStateFlow()

    private val _whatsAppMessages = MutableStateFlow(
        listOf(
            WhatsAppMessage(
                id = "MSG-01",
                candidateId = "CAND-01",
                candidateName = "Carlos Ramírez",
                sender = MessageSender.BOT_AUTOMATION,
                content = "¡Hola Carlos! Te contactamos del equipo de Talentry para la vacante de Operador de Montacargas. ¿Sigues interesado en el proceso?",
                timestamp = "09:15 AM",
                triggeredRuleTitle = "Mensaje Inicial Automático"
            ),
            WhatsAppMessage(
                id = "MSG-02",
                candidateId = "CAND-01",
                candidateName = "Carlos Ramírez",
                sender = MessageSender.CANDIDATE,
                content = "¡Sí, estoy muy interesado! ¿Cuáles son los siguientes pasos?",
                timestamp = "09:18 AM"
            ),
            WhatsAppMessage(
                id = "MSG-03",
                candidateId = "CAND-01",
                candidateName = "Carlos Ramírez",
                sender = MessageSender.BOT_AUTOMATION,
                content = "¡Excelente Carlos! Hemos registrado tu interés para Operador de Montacargas. Te comparto las opciones de horario para entrevista presencial.",
                timestamp = "09:18 AM",
                triggeredRuleTitle = "Confirmación de Interés 'SÍ'"
            ),
            WhatsAppMessage(
                id = "MSG-04",
                candidateId = "CAND-01",
                candidateName = "Carlos Ramírez",
                sender = MessageSender.CANDIDATE,
                content = "¿Tienen horario mañana a las 11:00 AM para entrevista?",
                timestamp = "09:22 AM"
            )
        )
    )
    val whatsAppMessages: StateFlow<List<WhatsAppMessage>> = _whatsAppMessages.asStateFlow()

    fun addWhatsAppRule(rule: WhatsAppRule) {
        _whatsAppRules.value = _whatsAppRules.value + rule
    }

    fun toggleWhatsAppRule(ruleId: String) {
        _whatsAppRules.value = _whatsAppRules.value.map {
            if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun sendSimulatedCandidateMessage(text: String, candidateName: String, vacancyTitle: String = "Operador de Montacargas") {
        val userMsg = WhatsAppMessage(
            id = "MSG-${System.currentTimeMillis() % 10000}",
            candidateId = "CAND-01",
            candidateName = candidateName,
            sender = MessageSender.CANDIDATE,
            content = text,
            timestamp = "Ahora"
        )
        val currentList = _whatsAppMessages.value + userMsg
        _whatsAppMessages.value = currentList

        // Automated Rule Matching
        val lowerText = text.lowercase()
        val matchedRule = _whatsAppRules.value.firstOrNull { rule ->
            rule.isEnabled && rule.triggerKeywords.any { keyword -> lowerText.contains(keyword.lowercase()) }
        }

        if (matchedRule != null) {
            val responseText = matchedRule.responseTemplate
                .replace("{{candidate_name}}", candidateName)
                .replace("{{vacancy_title}}", vacancyTitle)
                .replace("{{recruiter_name}}", "Francisco Enciso")

            val botMsg = WhatsAppMessage(
                id = "MSG-${(System.currentTimeMillis() + 1) % 10000}",
                candidateId = "CAND-01",
                candidateName = candidateName,
                sender = if (matchedRule.actionType == RuleActionType.ESCALATE_TO_RECRUITER) MessageSender.RECRUITER else MessageSender.BOT_AUTOMATION,
                content = responseText,
                timestamp = "Ahora",
                triggeredRuleTitle = "Regla automática: ${matchedRule.title}"
            )
            _whatsAppMessages.value = _whatsAppMessages.value + botMsg
        } else {
            // Default AI Assistant response
            val defaultMsg = WhatsAppMessage(
                id = "MSG-${(System.currentTimeMillis() + 1) % 10000}",
                candidateId = "CAND-01",
                candidateName = candidateName,
                sender = MessageSender.BOT_AUTOMATION,
                content = "Hemos recibido tu mensaje '$text'. Nuestro equipo de reclutamiento revisará tu respuesta para la vacante $vacancyTitle y te responderá en breve.",
                timestamp = "Ahora",
                triggeredRuleTitle = "Asistente Conversacional IA"
            )
            _whatsAppMessages.value = _whatsAppMessages.value + defaultMsg
        }
    }

    // =========================================================================
    // 3. EXPEDIENTE DIGITAL 360° (Complete Candidate Dossier)
    // =========================================================================
    private val _candidateDocuments = MutableStateFlow(
        listOf(
            CandidateDocument("DOC-1", "Curriculum Vitae (CV)", "cv_carlos_ramirez_2026.pdf", "28 Jul 2026", DocumentStatus.VALIDATED),
            CandidateDocument("DOC-2", "Identificación Oficial (INE)", "ine_frente_y_reverso.pdf", "28 Jul 2026", DocumentStatus.VALIDATED),
            CandidateDocument("DOC-3", "RFC / Constancia Fiscal", "csf_sat_2026.pdf", "28 Jul 2026", DocumentStatus.VALIDATED),
            CandidateDocument("DOC-4", "Número de Seguridad Social (NSS)", "imss_alta_nss.pdf", "29 Jul 2026", DocumentStatus.RECEIVED),
            CandidateDocument("DOC-5", "Certificado Médico Laboral", "examen_medico_laboratorio.pdf", "30 Jul 2026", DocumentStatus.PENDING)
        )
    )
    val candidateDocuments: StateFlow<List<CandidateDocument>> = _candidateDocuments.asStateFlow()

    private val _dossierTimeline = MutableStateFlow(
        listOf(
            DossierTimelineEvent(
                id = "EVT-01",
                title = "Postulación recibida vía Portal Talentry",
                description = "El candidato se postuló para 'Operador de Montacargas' en Planta Apodaca.",
                timestamp = "27 Jul 2026, 14:20",
                type = TimelineEventType.APPLICATION
            ),
            DossierTimelineEvent(
                id = "EVT-02",
                title = "Análisis IA Conversacional completado",
                description = "Clasificado como 'Alta Intención' • Match de perfil: 96% compatible con requisitos.",
                timestamp = "27 Jul 2026, 14:21",
                type = TimelineEventType.AI_INSIGHT
            ),
            DossierTimelineEvent(
                id = "EVT-03",
                title = "Contacto Automático por WhatsApp",
                description = "Regla ejecutada: 'Confirmación de Interés SÍ'. El candidato respondió favorablemente.",
                timestamp = "28 Jul 2026, 09:18",
                type = TimelineEventType.WHATSAPP_AUTO
            ),
            DossierTimelineEvent(
                id = "EVT-04",
                title = "Formulario Interno 'Pre-Filtro' Respondido",
                description = "Respuestas verificadas: Experiencia 3-5 años montacargas, disponibilidad rotar turnos.",
                timestamp = "28 Jul 2026, 11:45",
                type = TimelineEventType.FORM_SUBMITTED
            ),
            DossierTimelineEvent(
                id = "EVT-05",
                title = "Entrevista Técnica Programada",
                description = "Cita presencial confirmada en Sala 1 con Reclutador Senior Francisco Enciso.",
                timestamp = "29 Jul 2026, 10:00",
                type = TimelineEventType.INTERVIEW
            )
        )
    )
    val dossierTimeline: StateFlow<List<DossierTimelineEvent>> = _dossierTimeline.asStateFlow()

    fun updateDocumentStatus(docId: String, newStatus: DocumentStatus) {
        _candidateDocuments.value = _candidateDocuments.value.map {
            if (it.id == docId) it.copy(status = newStatus) else it
        }
    }

    fun addTimelineEvent(title: String, description: String, type: TimelineEventType) {
        val newEvt = DossierTimelineEvent(
            id = "EVT-${System.currentTimeMillis() % 10000}",
            title = title,
            description = description,
            timestamp = "Ahora mismo",
            type = type
        )
        _dossierTimeline.value = listOf(newEvt) + _dossierTimeline.value
    }
}
