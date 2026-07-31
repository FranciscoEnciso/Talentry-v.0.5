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
    val type: QuestionType,
    val isRequired: Boolean = true,
    val options: List<String> = emptyList(),
    val helpText: String = "",
    val placeholder: String = "",
    val validationRule: ValidationRule = ValidationRule(),
    val conditionalRule: ConditionalRule? = null
)

enum class QuestionType(val label: String) {
    TEXT_SHORT("Texto corto"),
    TEXT_LONG("Texto largo"),
    SPLIT_NAME("Nombre Completo (Nombre, Ap. Paterno, Ap. Materno)"),
    MULTIPLE_CHOICE("Opción múltiple (Radio)"),
    DROPDOWN("Lista desplegable (Select)"),
    CHECKBOXES("Casillas de verificación (Multi-select)"),
    FILE_UPLOAD("Carga de archivo (CV / INE / PDF)"),
    YES_NO("Sí / No"),
    DATE("Fecha / Calendario")
}

data class ValidationRule(
    val type: ValidationType = ValidationType.NONE,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val exactLength: Int? = null,
    val dateFormat: String = "DD/MM/YYYY", // "DD/MM/YYYY", "MM/YYYY", "YYYY"
    val forbiddenWords: List<String> = emptyList()
)

enum class ValidationType(val label: String) {
    NONE("Sin validación especial"),
    EMAIL("Correo electrónico válido"),
    PHONE("Teléfono de 10 dígitos"),
    CURP("CURP (18 caracteres)"),
    RFC("RFC (12-13 caracteres)"),
    NSS("NSS IMSS (11 dígitos)")
}

data class ConditionalRule(
    val parentQuestionId: String,
    val expectedAnswer: String
)

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

// 4. MOTOR DE REGLAS IFTTT (If This Then That Recruitment Workflow Engine)
data class WorkflowIftttRule(
    val id: String,
    val title: String,
    val triggerType: IftttTriggerType,
    val triggerDescription: String,
    val actionType: IftttActionType,
    val actionDescription: String,
    val targetStage: String? = null,
    val isEnabled: Boolean = true
)

enum class IftttTriggerType(val label: String) {
    INTERVIEW_CONFIRMED("Cuando el candidato confirma entrevista"),
    DOCUMENTS_UPLOADED("Cuando envía documentos de ingreso"),
    OFFER_REJECTED("Cuando rechaza la oferta o es descartado"),
    FORM_SUBMITTED("Cuando responde formulario interno"),
    INACTIVITY_48H("Cuando pasa 48 hrs sin responder")
}

enum class IftttActionType(val label: String) {
    UPDATE_STAGE_AND_NOTIFY("Actualizar etapa del Pipeline + Enviar instrucciones"),
    UPDATE_DOSSIER_AND_ALERT("Actualizar Expediente 360° + Notificar a Reclutador"),
    CLOSE_PROCESS("Cerrar proceso y enviar encuesta de salida"),
    SCHEDULE_EVENT("Crear evento en Agenda del Reclutador"),
    SEND_AUTO_REMINDER("Enviar recordatorio automático por WhatsApp")
}

