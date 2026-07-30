package com.example.data.model

// 1. FORMULARIOS INTERNOS (Internal Forms Builder & Submissions)
data class FormTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // e.g. "Pre-Filtro", "Documentación", "Competencias", "Onboarding"
    val questions: List<FormQuestion>,
    val responseCount: Int = 0,
    val isActive: Boolean = true,
    val shareCode: String = "TAL-FORM-${System.currentTimeMillis() % 1000}"
)

data class FormQuestion(
    val id: String,
    val promptText: String,
    val type: QuestionType, // TEXT_SHORT, TEXT_LONG, MULTIPLE_CHOICE, FILE_UPLOAD, YES_NO, DATE
    val isRequired: Boolean = true,
    val options: List<String> = emptyList() // For multiple choice
)

enum class QuestionType(val label: String) {
    TEXT_SHORT("Texto corto"),
    TEXT_LONG("Texto largo"),
    MULTIPLE_CHOICE("Opción múltiple"),
    FILE_UPLOAD("Carga de archivo (CV / INE / PDF)"),
    YES_NO("Sí / No"),
    DATE("Fecha / Calendario")
}

data class FormSubmission(
    val id: String,
    val formTemplateId: String,
    val formTitle: String,
    val candidateId: String,
    val candidateName: String,
    val submittedAt: String,
    val answers: List<FormAnswerItem>,
    val aiScore: Int = 92 // Automatically calculated by AI
)

data class FormAnswerItem(
    val questionPrompt: String,
    val answerText: String,
    val fileUrl: String? = null
)

// 2. WHATSAPP & MOTOR DE REGLAS (No-Code Rules Engine & WhatsApp Automation)
data class WhatsAppRule(
    val id: String,
    val title: String,
    val triggerKeywords: List<String>,
    val actionType: RuleActionType,
    val responseTemplate: String,
    val newCandidateStage: String? = null,
    val isEnabled: Boolean = true
)

enum class RuleActionType(val description: String) {
    SEND_TEMPLATE("Enviar plantilla automática"),
    CHANGE_STAGE_AND_RESPOND("Cambiar etapa del candidato + Enviar mensaje"),
    REQUEST_DOCUMENTATION("Solicitar documentos (CV, INE, RFC)"),
    ESCALATE_TO_RECRUITER("Escalar a Reclutador Humano + Alerta"),
    OFFER_INTERVIEW_SLOTS("Ofrecer horarios disponibles de entrevista")
}

data class WhatsAppMessage(
    val id: String,
    val candidateId: String,
    val candidateName: String,
    val sender: MessageSender, // CANDIDATE, BOT_AUTOMATION, RECRUITER
    val content: String,
    val timestamp: String,
    val triggeredRuleTitle: String? = null,
    val isRead: Boolean = true
)

enum class MessageSender {
    CANDIDATE,
    BOT_AUTOMATION,
    RECRUITER
}

data class WhatsAppConversation(
    val candidateId: String,
    val candidateName: String,
    val candidatePhone: String,
    val vacancyTitle: String,
    val currentStage: String,
    val unreadCount: Int,
    val lastMessage: String,
    val lastTimestamp: String,
    val aiIntentLabel: String // "Alta Intención", "Riesgo Abandono", "Escalado Humano", "Interesado"
)

// 3. EXPEDIENTE DIGITAL 360° (Complete Candidate Dossier)
data class CandidateDocument(
    val id: String,
    val docName: String, // e.g. "CV Actualizado", "INE / Identificación", "RFC / Constancia", "Examen Médico"
    val fileName: String,
    val uploadDate: String,
    val status: DocumentStatus // VALIDATED, PENDING, REJECTED
)

enum class DocumentStatus(val label: String) {
    VALIDATED("Validado"),
    PENDING("Pendiente de carga"),
    RECEIVED("Recibido / En revisión"),
    REJECTED("Rechazado")
}

data class DossierTimelineEvent(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val type: TimelineEventType // APPLICATION, WHATSAPP_AUTO, FORM_SUBMITTED, INTERVIEW, STAGE_CHANGE
)

enum class TimelineEventType {
    APPLICATION,
    WHATSAPP_AUTO,
    FORM_SUBMITTED,
    INTERVIEW,
    STAGE_CHANGE,
    AI_INSIGHT
}
