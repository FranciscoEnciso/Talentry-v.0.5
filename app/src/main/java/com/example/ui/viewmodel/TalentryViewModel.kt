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

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TalentryViewModel(application: AndroidApp) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("talentry_prefs", android.content.Context.MODE_PRIVATE)

    private val repository = TalentryRepository(TalentryDatabase.getDatabase(application))
    private val aiService = GeminiAiService()
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    init {
        setupFirestoreSync()
    }

    private fun setupFirestoreSync() {
        try {
            firestore.collection("form_responses")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("TalentryViewModel", "Firestore sync error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val incomingSubmissions = snapshot.documents.mapNotNull { doc ->
                            try {
                                val formId = doc.getString("formTemplateId") ?: "FORM-01"
                                val formTitle = doc.getString("formTitle") ?: "Formulario Web"
                                val candName = doc.getString("candidateName") ?: "Candidato Web"
                                val rawAnswers = doc.get("answers") as? List<Map<String, Any?>> ?: emptyList()
                                val answerItems = rawAnswers.map { ans ->
                                    FormAnswerItem(
                                        questionPrompt = ans["question"]?.toString() ?: "",
                                        answerText = ans["answer"]?.toString() ?: "",
                                        fileUrl = ans["fileUrl"]?.toString()
                                    )
                                }
                                FormSubmission(
                                    id = doc.id,
                                    formTemplateId = formId,
                                    formTitle = formTitle,
                                    candidateId = "CAND-${doc.id.takeLast(4)}",
                                    candidateName = candName,
                                    submittedAt = "Sincronizado Firebase",
                                    answers = answerItems,
                                    aiScore = 95
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (incomingSubmissions.isNotEmpty()) {
                            val existingIds = _formSubmissions.value.map { it.id }.toSet()
                            val newOnly = incomingSubmissions.filter { it.id !in existingIds }
                            if (newOnly.isNotEmpty()) {
                                _formSubmissions.value = newOnly + _formSubmissions.value
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("TalentryViewModel", "Failed to init Firestore sync", e)
        }
    }

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

    // UI state & Preferences State Flows
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBranchFilter = MutableStateFlow("Todas")
    val selectedBranchFilter: StateFlow<String> = _selectedBranchFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("Todos")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _monthlyHiringGoal = MutableStateFlow(prefs.getInt("monthly_hiring_goal", 25))
    val monthlyHiringGoal: StateFlow<Int> = _monthlyHiringGoal.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Francisco Enciso") ?: "Francisco Enciso")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow(prefs.getString("user_role", "Reclutador Senior") ?: "Reclutador Senior")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _userCompany = MutableStateFlow(prefs.getString("user_company", "Talentry HR SaaS") ?: "Talentry HR SaaS")
    val userCompany: StateFlow<String> = _userCompany.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("user_email", "francisco.enciso@talentry.app") ?: "francisco.enciso@talentry.app")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(prefs.getBoolean("push_notifications", true))
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    private val _emailAlertsEnabled = MutableStateFlow(prefs.getBoolean("email_alerts", true))
    val emailAlertsEnabled: StateFlow<Boolean> = _emailAlertsEnabled.asStateFlow()

    private val _whatsAppAlertsEnabled = MutableStateFlow(prefs.getBoolean("whatsapp_alerts", true))
    val whatsAppAlertsEnabled: StateFlow<Boolean> = _whatsAppAlertsEnabled.asStateFlow()

    private val _aiModelSelected = MutableStateFlow(prefs.getString("ai_model", "Gemini 2.5 Pro") ?: "Gemini 2.5 Pro")
    val aiModelSelected: StateFlow<String> = _aiModelSelected.asStateFlow()

    private val _recruitmentMaxDays = MutableStateFlow(prefs.getInt("recruitment_max_days", 30))
    val recruitmentMaxDays: StateFlow<Int> = _recruitmentMaxDays.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // AI State
    private val _aiOutputText = MutableStateFlow("")
    val aiOutputText: StateFlow<String> = _aiOutputText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

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
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    fun updateMonthlyHiringGoal(newGoal: Int) {
        _monthlyHiringGoal.value = newGoal
        prefs.edit().putInt("monthly_hiring_goal", newGoal).apply()
        showSnackbar("Meta mensual de contrataciones actualizada a $newGoal plazas.")
    }

    fun updateUserProfile(name: String, role: String, company: String, email: String) {
        _userName.value = name
        _userRole.value = role
        _userCompany.value = company
        _userEmail.value = email
        prefs.edit()
            .putString("user_name", name)
            .putString("user_role", role)
            .putString("user_company", company)
            .putString("user_email", email)
            .apply()
        showSnackbar("Perfil de usuario actualizado con éxito.")
    }

    fun updateNotificationSettings(push: Boolean, email: Boolean, whatsApp: Boolean) {
        _pushNotificationsEnabled.value = push
        _emailAlertsEnabled.value = email
        _whatsAppAlertsEnabled.value = whatsApp
        prefs.edit()
            .putBoolean("push_notifications", push)
            .putBoolean("email_alerts", email)
            .putBoolean("whatsapp_alerts", whatsApp)
            .apply()
        showSnackbar("Preferencias de notificación guardadas.")
    }

    fun updateAiSettings(model: String) {
        _aiModelSelected.value = model
        prefs.edit().putString("ai_model", model).apply()
        showSnackbar("Modelo de IA configurado a $model.")
    }

    fun updateRecruitmentMaxDays(days: Int) {
        _recruitmentMaxDays.value = days
        prefs.edit().putInt("recruitment_max_days", days).apply()
        showSnackbar("Días límite por vacante configurados a $days días.")
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
        viewModelScope.launch {
            repository.addInterview(interview)
            showSnackbar("Entrevista agendada con exito para ${interview.candidateName}.")
            addTimelineEvent(
                title = "Entrevista Agendada (${interview.type})",
                description = "Programada para el ${interview.scheduledDateTime} con ${interview.interviewer}.",
                type = TimelineEventType.INTERVIEW
            )
        }
    }

    fun updateInterviewResult(id: String, status: String, feedback: String) {
        viewModelScope.launch {
            repository.updateInterviewResult(id, status, feedback)
            val currentInterviews = interviews.value
            val targetInterview = currentInterviews.find { it.id == id }
            
            val candName = targetInterview?.candidateName ?: "Candidato"
            val targetCandidateId = targetInterview?.candidateId
            
            showSnackbar("Resultado de entrevista registrado: $status")
            
            addTimelineEvent(
                title = "Resultado de Entrevista: $status",
                description = "Feedback / Comentarios: $feedback",
                type = TimelineEventType.INTERVIEW
            )

            // If "No aprobó" or "Descartado", update candidate stage
            if (status.contains("No aprobó", ignoreCase = true) || status.contains("Rechazado", ignoreCase = true) || status.contains("Descartado", ignoreCase = true)) {
                if (targetCandidateId != null) {
                    candidates.value.find { it.id == targetCandidateId }?.let { candidate ->
                        repository.updateCandidate(candidate.copy(currentStatus = "Descartado", notes = "${candidate.notes}\n[Entrevista - No Aprobó]: $feedback"))
                    }
                }
            } else if (status.contains("Aprobó", ignoreCase = true) || status.contains("Aprobado", ignoreCase = true)) {
                if (targetCandidateId != null) {
                    candidates.value.find { it.id == targetCandidateId }?.let { candidate ->
                        repository.updateCandidate(candidate.copy(currentStatus = "Documentos"))
                    }
                }
            }
        }
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

    fun analyzeConversationIntentWithAi(candidateName: String, chatSnippet: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.analyzeConversationIntent(candidateName, chatSnippet)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun clearAiOutput() {
        _aiOutputText.value = ""
    }

    // =========================================================================
    // MOBILE APP FIRST (OPPO RENO 12 F 5G) / EXCLUSIVE APP LAYOUT STATE
    // =========================================================================
    private val _isDesktopMode = MutableStateFlow(false)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    fun toggleDesktopMode() {
        _isDesktopMode.value = false // Mantener estrictamente como versión de app móvil
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

    fun updateFormTemplate(template: FormTemplate) {
        _formTemplates.value = _formTemplates.value.map {
            if (it.id == template.id) template else it
        }
    }

    fun deleteFormTemplate(templateId: String) {
        _formTemplates.value = _formTemplates.value.filter { it.id != templateId }
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

    fun addCandidateDocument(docName: String, fileName: String) {
        val newDoc = CandidateDocument(
            id = "DOC-${System.currentTimeMillis() % 1000}",
            docName = docName,
            fileName = fileName,
            uploadDate = "Hoy",
            status = DocumentStatus.RECEIVED
        )
        _candidateDocuments.value = _candidateDocuments.value + newDoc
        addTimelineEvent(
            title = "Documento cargado al Expediente Digital",
            description = "Archivo '$fileName' ($docName) recibido y listo para validación de RH.",
            type = TimelineEventType.FORM_SUBMITTED
        )
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

    // =========================================================================
    // 4. MOTOR DE REGLAS IFTTT (If This Then That Recruitment Workflow Engine)
    // =========================================================================
    private val _workflowIftttRules = MutableStateFlow(
        listOf(
            WorkflowIftttRule(
                id = "WFR-01",
                title = "Si confirma entrevista -> Actualizar etapa + Instrucciones",
                triggerType = IftttTriggerType.INTERVIEW_CONFIRMED,
                triggerDescription = "Candidato responde aceptando horario de entrevista",
                actionType = IftttActionType.UPDATE_STAGE_AND_NOTIFY,
                actionDescription = "Cambiar etapa en Pipeline a 'Entrevista', crear evento y enviar PDF de llegada",
                targetStage = "Entrevista",
                isEnabled = true
            ),
            WorkflowIftttRule(
                id = "WFR-02",
                title = "Si envía documentos de ingreso -> Actualizar Expediente 360°",
                triggerType = IftttTriggerType.DOCUMENTS_UPLOADED,
                triggerDescription = "Candidato sube archivos en portal web o WhatsApp",
                actionType = IftttActionType.UPDATE_DOSSIER_AND_ALERT,
                actionDescription = "Marcar expediente en revisión y enviar alerta push al Reclutador Senior",
                targetStage = "Documentos",
                isEnabled = true
            ),
            WorkflowIftttRule(
                id = "WFR-03",
                title = "Si rechaza oferta -> Cerrar proceso + Cuestionario salida",
                triggerType = IftttTriggerType.OFFER_REJECTED,
                triggerDescription = "Candidato indica que ya aceptó otra propuesta laboral",
                actionType = IftttActionType.CLOSE_PROCESS,
                actionDescription = "Actualizar estado a 'Descartado' y enviar link de encuesta de motivos",
                targetStage = "Descartado",
                isEnabled = true
            ),
            WorkflowIftttRule(
                id = "WFR-04",
                title = "Si pasa 48 hrs sin respuesta -> Recordatorio automático",
                triggerType = IftttTriggerType.INACTIVITY_48H,
                triggerDescription = "Candidato convocado que no ha contestado llamada ni mensaje",
                actionType = IftttActionType.SEND_AUTO_REMINDER,
                actionDescription = "Enviar mensaje de seguimiento amable con botón para reagendar",
                targetStage = null,
                isEnabled = true
            )
        )
    )
    val workflowIftttRules: StateFlow<List<WorkflowIftttRule>> = _workflowIftttRules.asStateFlow()

    fun addWorkflowIftttRule(rule: WorkflowIftttRule) {
        _workflowIftttRules.value = _workflowIftttRules.value + rule
    }

    fun toggleWorkflowIftttRule(ruleId: String) {
        _workflowIftttRules.value = _workflowIftttRules.value.map {
            if (it.id == ruleId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun executeWorkflowIftttRule(rule: WorkflowIftttRule, candidateName: String = "Carlos Ramírez") {
        // 1. Log timeline event
        addTimelineEvent(
            title = "Regla IFTTT Ejecutada: ${rule.title}",
            description = "Disparador: ${rule.triggerType.label} • Acción: ${rule.actionDescription}",
            type = TimelineEventType.WHATSAPP_AUTO
        )

        // 2. Add automated WhatsApp message
        val autoMsgText = when (rule.actionType) {
            IftttActionType.UPDATE_STAGE_AND_NOTIFY -> "¡Hola $candidateName! Tu entrevista ha sido agendada con éxito. Te hemos adjuntado las instrucciones y mapa para llegar a la planta."
            IftttActionType.UPDATE_DOSSIER_AND_ALERT -> "Hemos recibido correctamente tus documentos oficiales. El reclutador Francisco está validando tu expediente en este momento."
            IftttActionType.CLOSE_PROCESS -> "Agradecemos mucho tu interés en Talentry, $candidateName. Hemos cerrado tu proceso y te agradecemos contestar esta breve encuesta de 1 minuto."
            IftttActionType.SCHEDULE_EVENT -> "Se ha programado una sesión especial en tu calendario para el seguimiento técnico con el área usuaria."
            IftttActionType.SEND_AUTO_REMINDER -> "¡Hola $candidateName! Vimos que aún está pendiente tu confirmación para Operador de Montacargas. ¿Te gustaría reagendar para otro día?"
        }

        val botMsg = WhatsAppMessage(
            id = "MSG-${System.currentTimeMillis() % 10000}",
            candidateId = "CAND-01",
            candidateName = candidateName,
            sender = MessageSender.BOT_AUTOMATION,
            content = autoMsgText,
            timestamp = "Ahora",
            triggeredRuleTitle = "Motor IFTTT: ${rule.title}"
        )
        _whatsAppMessages.value = _whatsAppMessages.value + botMsg

        // 3. Update candidate stage if applicable
        if (rule.targetStage != null) {
            val currentList = candidates.value
            currentList.find { it.fullName.contains(candidateName, ignoreCase = true) || it.id == "CAND-01" }?.let { cand ->
                updateCandidate(cand.copy(currentStatus = rule.targetStage))
            }
        }
    }

    fun addFormSubmissionFromCandidate(formTemplateId: String, formTitle: String, candidateName: String, answers: List<FormAnswerItem>) {
        val newSub = FormSubmission(
            id = "SUB-${System.currentTimeMillis() % 10000}",
            formTemplateId = formTemplateId,
            formTitle = formTitle,
            candidateId = "CAND-01",
            candidateName = candidateName,
            submittedAt = "Ahora mismo",
            answers = answers,
            aiScore = 95
        )
        _formSubmissions.value = listOf(newSub) + _formSubmissions.value

        // Sync submission to Firebase Firestore
        try {
            val firestoreData = mapOf(
                "formTemplateId" to formTemplateId,
                "formTitle" to formTitle,
                "candidateName" to candidateName,
                "answers" to answers.map { mapOf("question" to it.questionPrompt, "answer" to it.answerText, "fileUrl" to it.fileUrl) },
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("form_responses").add(firestoreData)
        } catch (e: Exception) {
            Log.e("TalentryViewModel", "Error pushing submission to Firestore", e)
        }

        // Check if there is a file upload among answers
        answers.filter { it.fileUrl != null || it.answerText.endsWith(".pdf", ignoreCase = true) }.forEach { ans ->
            addCandidateDocument(
                docName = ans.questionPrompt.take(28),
                fileName = ans.answerText
            )
        }

        addTimelineEvent(
            title = "Formulario Respondido por Candidato: $formTitle",
            description = "Respuestas recibidas de $candidateName • Score IA automático: 95% compatible.",
            type = TimelineEventType.FORM_SUBMITTED
        )
    }
}

