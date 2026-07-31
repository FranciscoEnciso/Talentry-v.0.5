package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun FormsScreen(
    formTemplates: List<FormTemplate>,
    formSubmissions: List<FormSubmission>,
    onAddTemplate: (FormTemplate) -> Unit,
    onToggleTemplateStatus: (String) -> Unit,
    onSimulateCandidateSubmission: (String, String, String, List<FormAnswerItem>) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTemplateId by remember { mutableStateOf(formTemplates.firstOrNull()?.id ?: "") }
    val selectedTemplate = formTemplates.firstOrNull { it.id == selectedTemplateId } ?: formTemplates.firstOrNull()
    var activeTab by remember { mutableStateOf(0) } // 0 = Constructor, 1 = Respuestas
    var showNewFormDialog by remember { mutableStateOf(false) }
    var showPublicPreviewDialog by remember { mutableStateOf(false) }
    var candidateNameInput by remember { mutableStateOf("Ana Rodríguez") }
    var answerTextQ1 by remember { mutableStateOf("Sí tengo disponibilidad inmediata") }
    var answerTextQ2 by remember { mutableStateOf("4 años operando montacargas hombre sentado") }
    var answerTextQ3 by remember { mutableStateOf("cv_ana_rodriguez_2026.pdf") }
    var submissionFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElectricBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = ElectricBlueOnContainer
                        )
                    }
                    Column {
                        Text(
                            text = "Formularios Internos Talentry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Motor propio sin dependencias de Google Forms • Sincronización automática con Expediente Digital 360°",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showNewFormDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.testTag("create_form_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nuevo Formulario", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Desktop-First Two Column Layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: List of Form Templates (35% width)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(0.38f)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PLANTILLAS DE FORMULARIO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(formTemplates, key = { it.id }) { template ->
                            val isSelected = template.id == selectedTemplate?.id
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ElectricBlueContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = if (isSelected) BorderStroke(1.5.dp, ElectricBlue) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTemplateId = template.id }
                                    .testTag("form_template_${template.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = template.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ElectricBlueOnContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        StatusBadge(status = template.category)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = template.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${template.questions.size} preguntas • ${template.responseCount} respuestas",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Column: Active Template Builder & Submissions (65% width)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(0.62f)
                    .fillMaxHeight()
            ) {
                if (selectedTemplate != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header & Share Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedTemplate.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Enlace permanente: https://talentry.app/form/${selectedTemplate.id}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricBlue
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showPublicPreviewDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("form_share_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🔗 Enlace Público", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { showPublicPreviewDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    modifier = Modifier.testTag("test_candidate_form_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("👤 Responder como Candidato", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { onToggleTemplateStatus(selectedTemplate.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedTemplate.isActive) EmeraldSuccess else RoseError
                                    )
                                ) {
                                    Text(if (selectedTemplate.isActive) "Activo" else "Pausado", fontSize = 12.sp)
                                }
                            }
                        }

                        if (submissionFeedback != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { submissionFeedback = null }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                        Text(submissionFeedback!!, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    }
                                    Text("Cerrar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab selector
                        TabRow(
                            selectedTabIndex = activeTab,
                            containerColor = Color.Transparent,
                            contentColor = ElectricBlue
                        ) {
                            Tab(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                text = { Text("Constructor de Preguntas (${selectedTemplate.questions.size})", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                text = { Text("Respuestas Recibidas (${selectedTemplate.responseCount})", fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab 0: Constructor
                        if (activeTab == 0) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(selectedTemplate.questions, key = { it.id }) { q ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.DragIndicator, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(
                                                        text = q.promptText,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                StatusBadge(status = q.type.label)
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (q.isRequired) "• Campo Obligatorio (*)" else "• Opcional",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (q.isRequired) RoseError else MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                if (q.options.isNotEmpty()) {
                                                    Text(
                                                        text = "Opciones: ${q.options.joinToString(", ")}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Tab 1: Submissions
                            val filteredSubs = formSubmissions.filter { it.formTemplateId == selectedTemplate.id }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(filteredSubs, key = { it.id }) { sub ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(38.dp)
                                                            .clip(CircleShape)
                                                            .background(ElectricBlueContainer),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = sub.candidateName.take(2).uppercase(),
                                                            fontWeight = FontWeight.Bold,
                                                            color = ElectricBlueOnContainer
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            text = sub.candidateName,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Enviado: ${sub.submittedAt} • Vinculado al Expediente 360°",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(EmeraldLight)
                                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "AI Score: ${sub.aiScore}%",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = EmeraldOnContainer
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                            Spacer(modifier = Modifier.height(10.dp))

                                            sub.answers.forEach { ans ->
                                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = ans.questionPrompt,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = ans.answerText,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    if (ans.fileUrl != null) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            modifier = Modifier.padding(top = 4.dp)
                                                        ) {
                                                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                                            Text(text = "Archivo cargado: ${ans.answerText}", style = MaterialTheme.typography.bodySmall, color = ElectricBlue)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Form Dialog
    if (showNewFormDialog) {
        var formTitle by remember { mutableStateOf("") }
        var formDesc by remember { mutableStateOf("") }
        var formCat by remember { mutableStateOf("Pre-Filtro") }

        AlertDialog(
            onDismissRequest = { showNewFormDialog = false },
            title = { Text("Crear Formulario Propio Talentry", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { formTitle = it },
                        label = { Text("Título del Formulario") },
                        modifier = Modifier.fillMaxWidth().testTag("new_form_title_input")
                    )
                    OutlinedTextField(
                        value = formDesc,
                        onValueChange = { formDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formCat,
                        onValueChange = { formCat = it },
                        label = { Text("Categoría (Pre-Filtro, Documentación, Competencias)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formTitle.isNotBlank()) {
                            val newForm = FormTemplate(
                                id = "FORM-${System.currentTimeMillis() % 1000}",
                                title = formTitle,
                                description = formDesc.ifBlank { "Formulario generado por Talentry Forms Engine" },
                                category = formCat,
                                questions = listOf(
                                    FormQuestion("Q1", "¿Nombre completo y teléfono de contacto?", QuestionType.TEXT_SHORT, true),
                                    FormQuestion("Q2", "¿Disponibilidad de inicio inmediato?", QuestionType.YES_NO, true),
                                    FormQuestion("Q3", "Adjunta tu CV actualizado", QuestionType.FILE_UPLOAD, true)
                                ),
                                responseCount = 0,
                                isActive = true
                            )
                            onAddTemplate(newForm)
                            showNewFormDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_form_button")
                ) {
                    Text("Guardar Formulario")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFormDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showPublicPreviewDialog && selectedTemplate != null) {
        AlertDialog(
            onDismissRequest = { showPublicPreviewDialog = false },
            title = {
                Column {
                    Text("Portal Web Público del Candidato", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("URL Compartible: https://talentry.app/form/${selectedTemplate.id}", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = selectedTemplate.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricBlue
                    )
                    Text(selectedTemplate.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    OutlinedTextField(
                        value = candidateNameInput,
                        onValueChange = { candidateNameInput = it },
                        label = { Text("Nombre Completo del Candidato") },
                        modifier = Modifier.fillMaxWidth().testTag("preview_candidate_name")
                    )

                    OutlinedTextField(
                        value = answerTextQ1,
                        onValueChange = { answerTextQ1 = it },
                        label = { Text("P1: ¿Disponibilidad inmediata?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = answerTextQ2,
                        onValueChange = { answerTextQ2 = it },
                        label = { Text("P2: ¿Años de experiencia en el puesto?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = answerTextQ3,
                        onValueChange = { answerTextQ3 = it },
                        label = { Text("P3: Adjunta archivo o CV (.pdf)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val answers = listOf(
                            FormAnswerItem("¿Disponibilidad inmediata?", answerTextQ1),
                            FormAnswerItem("¿Años de experiencia en el puesto?", answerTextQ2),
                            FormAnswerItem("Adjunta archivo o CV (.pdf)", answerTextQ3, fileUrl = "https://storage.talentry.app/docs/$answerTextQ3")
                        )
                        onSimulateCandidateSubmission(
                            selectedTemplate.id,
                            selectedTemplate.title,
                            candidateNameInput,
                            answers
                        )
                        submissionFeedback = "✅ Respuesta de '$candidateNameInput' enviada al portal. CRM, Expediente 360° y Timeline sincronizados."
                        showPublicPreviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                    modifier = Modifier.testTag("submit_preview_button")
                ) {
                    Text("🚀 Enviar Respuesta al Reclutador")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublicPreviewDialog = false }) {
                    Text("Cerrar Vista Previa")
                }
            }
        )
    }
}
