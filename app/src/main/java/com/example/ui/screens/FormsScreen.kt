package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.widget.Toast
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun FormsScreen(
    formTemplates: List<FormTemplate>,
    formSubmissions: List<FormSubmission>,
    onAddTemplate: (FormTemplate) -> Unit,
    onUpdateTemplate: (FormTemplate) -> Unit = {},
    onDeleteTemplate: (String) -> Unit = {},
    onToggleTemplateStatus: (String) -> Unit,
    onSimulateCandidateSubmission: (String, String, String, List<FormAnswerItem>) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTemplateId by remember(formTemplates) {
        mutableStateOf(formTemplates.firstOrNull()?.id ?: "")
    }
    val selectedTemplate = formTemplates.firstOrNull { it.id == selectedTemplateId } ?: formTemplates.firstOrNull()
    var activeTab by remember { mutableStateOf(0) } // 0 = Preguntas, 1 = Respuestas
    var showNewFormDialog by remember { mutableStateOf(false) }
    var showEditFormDialog by remember { mutableStateOf(false) }
    var formToEdit by remember { mutableStateOf<FormTemplate?>(null) }
    var showPublicPreviewDialog by remember { mutableStateOf(false) }
    var showShareFormDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }

    var candidateNameInput by remember { mutableStateOf("Ana Rodríguez") }
    var answerTextQ1 by remember { mutableStateOf("Sí tengo disponibilidad inmediata") }
    var answerTextQ2 by remember { mutableStateOf("4 años operando montacargas hombre sentado") }
    var answerTextQ3 by remember { mutableStateOf("cv_ana_rodriguez_2026.pdf") }
    var submissionFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // COMPACT MOBILE BANNER (No ocupa toda la pantalla)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("forms_header_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = ElectricBlueOnContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Formularios Internos Talentry",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Motor propio • Sincronizado con Expediente 360°",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = { showNewFormDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("create_form_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // feedback banner
        if (submissionFeedback != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { submissionFeedback = null }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Text(submissionFeedback!!, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                    Text("Cerrar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        // CAROUSEL DE PLANTILLAS EN MÓVIL (Sleek Mobile Horizontal Strip)
        Column {
            Text(
                text = "PLANTILLAS DISPONIBLES (${formTemplates.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(formTemplates, key = { it.id }) { template ->
                    val isSelected = template.id == selectedTemplate?.id
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ElectricBlueContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) ElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .width(240.dp)
                            .clickable { selectedTemplateId = template.id }
                            .testTag("form_template_${template.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(status = template.category)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            formToEdit = template
                                            showEditFormDialog = true
                                        },
                                        modifier = Modifier.size(24.dp).testTag("edit_form_btn_${template.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            onDeleteTemplate(template.id)
                                            submissionFeedback = "🗑️ Plantilla '${template.title}' eliminada."
                                        },
                                        modifier = Modifier.size(24.dp).testTag("delete_form_btn_${template.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = RoseError, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Text(
                                text = template.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElectricBlueOnContainer else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${template.questions.size} preguntas • ${template.responseCount} resps",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (template.isActive) "Activo" else "Pausado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (template.isActive) EmeraldSuccess else RoseError,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // DETALLE DE PLANTILLA SELECCIONADA (Mobile Optimized Card)
        if (selectedTemplate != null) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Header & Acciones Rápidas del Formulario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val formUrl = "https://talentry-app.web.app/form/${selectedTemplate.id}"
                                    clipboardManager.setText(AnnotatedString(formUrl))
                                    Toast.makeText(context, "📋 Link copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                    submissionFeedback = "📋 Link '$formUrl' copiado al portapapeles."
                                }
                        ) {
                            Text(
                                text = selectedTemplate.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar link", tint = ElectricBlue, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "https://talentry-app.web.app/form/${selectedTemplate.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricBlue,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val formUrl = "https://talentry-app.web.app/form/${selectedTemplate.id}"
                                    clipboardManager.setText(AnnotatedString(formUrl))
                                    Toast.makeText(context, "📋 Link copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                    submissionFeedback = "📋 Link '$formUrl' copiado al portapapeles."
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("copy_form_link_btn")
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Copiar Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showShareFormDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("share_candidate_form_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Compartir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = {
                                    formToEdit = selectedTemplate
                                    showEditFormDialog = true
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("form_edit_main_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                            }

                            Button(
                                onClick = { showPublicPreviewDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("test_candidate_form_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Probar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab selector MÓVIL
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = Color.Transparent,
                        contentColor = ElectricBlue
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("Preguntas (${selectedTemplate.questions.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("Respuestas (${selectedTemplate.responseCount})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab 0: Preguntas
                    if (activeTab == 0) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Preguntas del Formulario",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = { showAddQuestionDialog = true },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.testTag("add_question_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Agregar Pregunta", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                itemsIndexed(selectedTemplate.questions, key = { _, q -> q.id }) { index, q ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                                    Text(
                                                        text = "P${index + 1}: ${q.promptText}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                StatusBadge(status = q.type.label)
                                            }

                                            if (q.helpText.isNotBlank()) {
                                                Text(
                                                    text = "💡 ${q.helpText}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    if (q.isRequired) {
                                                        Text("• Obligatorio (*)", style = MaterialTheme.typography.labelSmall, color = RoseError, fontWeight = FontWeight.Bold)
                                                    } else {
                                                        Text("• Opcional", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    if (q.validationRule.type != ValidationType.NONE) {
                                                        Text("• Validación: ${q.validationRule.type.label}", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                                                    }
                                                    if (q.conditionalRule != null) {
                                                        Text("• Condicional", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                // Reorder & Delete Actions
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    IconButton(
                                                        onClick = {
                                                            if (index > 0) {
                                                                val mutableQs = selectedTemplate.questions.toMutableList()
                                                                val temp = mutableQs[index]
                                                                mutableQs[index] = mutableQs[index - 1]
                                                                mutableQs[index - 1] = temp
                                                                onUpdateTemplate(selectedTemplate.copy(questions = mutableQs))
                                                            }
                                                        },
                                                        enabled = index > 0,
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Mover Arriba", modifier = Modifier.size(14.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            if (index < selectedTemplate.questions.size - 1) {
                                                                val mutableQs = selectedTemplate.questions.toMutableList()
                                                                val temp = mutableQs[index]
                                                                mutableQs[index] = mutableQs[index + 1]
                                                                mutableQs[index + 1] = temp
                                                                onUpdateTemplate(selectedTemplate.copy(questions = mutableQs))
                                                            }
                                                        },
                                                        enabled = index < selectedTemplate.questions.size - 1,
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Mover Abajo", modifier = Modifier.size(14.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            val updatedQs = selectedTemplate.questions.filterIndexed { i, _ -> i != index }
                                                            onUpdateTemplate(selectedTemplate.copy(questions = updatedQs))
                                                        },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Pregunta", tint = RoseError, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                            }

                                            if (q.options.isNotEmpty()) {
                                                Text(
                                                    text = "Opciones: ${q.options.joinToString(" | ")}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Tab 1: Respuestas
                        val filteredSubs = formSubmissions.filter { it.formTemplateId == selectedTemplate.id }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredSubs, key = { it.id }) { sub ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
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
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
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
                                                        text = "Enviado: ${sub.submittedAt}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            StatusBadge(status = "AI Score: ${sub.aiScore}%")
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        sub.answers.forEach { ans ->
                                            Column(modifier = Modifier.padding(vertical = 3.dp)) {
                                                Text(
                                                    text = ans.questionPrompt,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = ans.answerText,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold
                                                )
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

    // DIÁLOGO: CREAR NUEVO FORMULARIO
    if (showNewFormDialog) {
        var formTitle by remember { mutableStateOf("") }
        var formDesc by remember { mutableStateOf("") }
        var formCat by remember { mutableStateOf("Pre-Filtro") }

        AlertDialog(
            onDismissRequest = { showNewFormDialog = false },
            title = { Text("Crear Nuevo Formulario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { formTitle = it },
                        label = { Text("Título del Formulario") },
                        modifier = Modifier.fillMaxWidth().testTag("new_form_title_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = formDesc,
                        onValueChange = { formDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = formCat,
                        onValueChange = { formCat = it },
                        label = { Text("Categoría (Pre-Filtro, Documentos, Competencias)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                                description = formDesc.ifBlank { "Formulario creado en Talentry App" },
                                category = formCat,
                                questions = listOf(
                                    FormQuestion("Q1", "¿Nombre completo y teléfono de contacto?", QuestionType.TEXT_SHORT, true),
                                    FormQuestion("Q2", "¿Disponibilidad de inicio inmediato?", QuestionType.YES_NO, true),
                                    FormQuestion("Q3", "Adjunta tu CV actualizado (.pdf)", QuestionType.FILE_UPLOAD, true)
                                ),
                                responseCount = 0,
                                isActive = true
                            )
                            onAddTemplate(newForm)
                            selectedTemplateId = newForm.id
                            val newUrl = "https://talentry-app.web.app/form/${newForm.id}"
                            clipboardManager.setText(AnnotatedString(newUrl))
                            Toast.makeText(context, "📋 Formulario creado y enlace copiado al portapapeles", Toast.LENGTH_LONG).show()
                            submissionFeedback = "✅ Formulario '$formTitle' creado. Link copiado: $newUrl"
                            showNewFormDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_form_button")
                ) {
                    Text("Crear Formulario")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFormDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // DIÁLOGO: EDITAR FORMULARIO EXISTENTE
    if (showEditFormDialog && formToEdit != null) {
        var editTitle by remember(formToEdit) { mutableStateOf(formToEdit!!.title) }
        var editDesc by remember(formToEdit) { mutableStateOf(formToEdit!!.description) }
        var editCat by remember(formToEdit) { mutableStateOf(formToEdit!!.category) }

        AlertDialog(
            onDismissRequest = { showEditFormDialog = false },
            title = { Text("Editar Formulario", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_form_title_input")
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCat,
                        onValueChange = { editCat = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank() && formToEdit != null) {
                            val updatedForm = formToEdit!!.copy(
                                title = editTitle,
                                description = editDesc,
                                category = editCat
                            )
                            onUpdateTemplate(updatedForm)
                            submissionFeedback = "✏️ Formulario '$editTitle' actualizado."
                            showEditFormDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_edit_form_button")
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditFormDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // DIÁLOGO: AGREGAR PREGUNTA AL FORMULARIO SELECCIONADO
    if (showAddQuestionDialog && selectedTemplate != null) {
        var questionPrompt by remember { mutableStateOf("") }
        var questionHelpText by remember { mutableStateOf("") }
        var placeholderText by remember { mutableStateOf("") }
        var isRequired by remember { mutableStateOf(true) }
        var questionType by remember { mutableStateOf(QuestionType.TEXT_SHORT) }
        var optionsText by remember { mutableStateOf("") } // Comma separated for Choice/Dropdown
        var dateFormat by remember { mutableStateOf("DD/MM/YYYY") }
        var validationType by remember { mutableStateOf(ValidationType.NONE) }
        var minLengthStr by remember { mutableStateOf("") }
        var maxLengthStr by remember { mutableStateOf("") }
        var exactLengthStr by remember { mutableStateOf("") }
        var forbiddenWordsStr by remember { mutableStateOf("") }
        var isConditional by remember { mutableStateOf(false) }
        var parentQuestionId by remember { mutableStateOf("") }
        var expectedAnswerVal by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = { Text("Configurar Nueva Pregunta", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = questionPrompt,
                            onValueChange = { questionPrompt = it },
                            label = { Text("Pregunta o indicación (*)") },
                            modifier = Modifier.fillMaxWidth().testTag("new_question_prompt_input"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = questionHelpText,
                            onValueChange = { questionHelpText = it },
                            label = { Text("Descripción / Texto de ayuda (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Text("Tipo de Campo:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = questionType == QuestionType.TEXT_SHORT,
                                    onClick = { questionType = QuestionType.TEXT_SHORT },
                                    label = { Text("Texto Corto", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = questionType == QuestionType.SPLIT_NAME,
                                    onClick = { questionType = QuestionType.SPLIT_NAME },
                                    label = { Text("Nombre Dividido", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = questionType == QuestionType.YES_NO,
                                    onClick = { questionType = QuestionType.YES_NO },
                                    label = { Text("Sí / No", fontSize = 11.sp) }
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = questionType == QuestionType.MULTIPLE_CHOICE,
                                    onClick = { questionType = QuestionType.MULTIPLE_CHOICE },
                                    label = { Text("Radio Choices", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = questionType == QuestionType.DROPDOWN,
                                    onClick = { questionType = QuestionType.DROPDOWN },
                                    label = { Text("Desplegable", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = questionType == QuestionType.CHECKBOXES,
                                    onClick = { questionType = QuestionType.CHECKBOXES },
                                    label = { Text("Casillas", fontSize = 11.sp) }
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = questionType == QuestionType.FILE_UPLOAD,
                                    onClick = { questionType = QuestionType.FILE_UPLOAD },
                                    label = { Text("Archivo / CV", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = questionType == QuestionType.DATE,
                                    onClick = { questionType = QuestionType.DATE },
                                    label = { Text("Fecha", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    if (questionType == QuestionType.MULTIPLE_CHOICE || questionType == QuestionType.DROPDOWN || questionType == QuestionType.CHECKBOXES) {
                        item {
                            OutlinedTextField(
                                value = optionsText,
                                onValueChange = { optionsText = it },
                                label = { Text("Opciones separadas por coma") },
                                placeholder = { Text("Opción 1, Opción 2, Opción 3") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (questionType == QuestionType.DATE) {
                        item {
                            Text("Formato de Fecha:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(selected = dateFormat == "DD/MM/YYYY", onClick = { dateFormat = "DD/MM/YYYY" }, label = { Text("DD/MM/YYYY") })
                                FilterChip(selected = dateFormat == "MM/YYYY", onClick = { dateFormat = "MM/YYYY" }, label = { Text("MM/YYYY") })
                                FilterChip(selected = dateFormat == "YYYY", onClick = { dateFormat = "YYYY" }, label = { Text("YYYY") })
                            }
                        }
                    }

                    item {
                        Text("Regla de Validación Especial:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = validationType == ValidationType.NONE, onClick = { validationType = ValidationType.NONE }, label = { Text("Ninguna", fontSize = 10.sp) })
                            FilterChip(selected = validationType == ValidationType.EMAIL, onClick = { validationType = ValidationType.EMAIL }, label = { Text("Email", fontSize = 10.sp) })
                            FilterChip(selected = validationType == ValidationType.PHONE, onClick = { validationType = ValidationType.PHONE }, label = { Text("Teléfono", fontSize = 10.sp) })
                            FilterChip(selected = validationType == ValidationType.CURP, onClick = { validationType = ValidationType.CURP }, label = { Text("CURP", fontSize = 10.sp) })
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(selected = validationType == ValidationType.RFC, onClick = { validationType = ValidationType.RFC }, label = { Text("RFC", fontSize = 10.sp) })
                            FilterChip(selected = validationType == ValidationType.NSS, onClick = { validationType = ValidationType.NSS }, label = { Text("NSS", fontSize = 10.sp) })
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minLengthStr,
                                onValueChange = { minLengthStr = it.filter { char -> char.isDigit() } },
                                label = { Text("Mín. Caracteres") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = maxLengthStr,
                                onValueChange = { maxLengthStr = it.filter { char -> char.isDigit() } },
                                label = { Text("Máx. Caracteres") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = forbiddenWordsStr,
                            onValueChange = { forbiddenWordsStr = it },
                            label = { Text("Palabras Prohibidas (separadas por coma)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("¿Activar Lógica Condicional?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Switch(checked = isConditional, onCheckedChange = { isConditional = it })
                        }
                    }

                    if (isConditional && selectedTemplate.questions.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Mostrar solo si la pregunta previa:", style = MaterialTheme.typography.labelSmall)
                                selectedTemplate.questions.forEach { prevQ ->
                                    FilterChip(
                                        selected = parentQuestionId == prevQ.id,
                                        onClick = { parentQuestionId = prevQ.id },
                                        label = { Text("P: ${prevQ.promptText}", maxLines = 1) }
                                    )
                                }
                                OutlinedTextField(
                                    value = expectedAnswerVal,
                                    onValueChange = { expectedAnswerVal = it },
                                    label = { Text("Es igual a la respuesta:") },
                                    placeholder = { Text("ej. Sí") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("¿Pregunta obligatoria?", style = MaterialTheme.typography.bodyMedium)
                            Switch(checked = isRequired, onCheckedChange = { isRequired = it })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionPrompt.isNotBlank() && selectedTemplate != null) {
                            val parsedOptions = optionsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val rule = ValidationRule(
                                type = validationType,
                                minLength = minLengthStr.toIntOrNull(),
                                maxLength = maxLengthStr.toIntOrNull(),
                                exactLength = exactLengthStr.toIntOrNull(),
                                dateFormat = dateFormat,
                                forbiddenWords = forbiddenWordsStr.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
                            )
                            val condRule = if (isConditional && parentQuestionId.isNotBlank() && expectedAnswerVal.isNotBlank()) {
                                ConditionalRule(parentQuestionId = parentQuestionId, expectedAnswer = expectedAnswerVal)
                            } else null

                            val newQuestion = FormQuestion(
                                id = "Q-${System.currentTimeMillis() % 1000}",
                                promptText = questionPrompt,
                                helpText = questionHelpText,
                                placeholder = placeholderText,
                                type = questionType,
                                isRequired = isRequired,
                                options = parsedOptions,
                                validationRule = rule,
                                conditionalRule = condRule
                            )
                            val updatedTemplate = selectedTemplate.copy(
                                questions = selectedTemplate.questions + newQuestion
                            )
                            onUpdateTemplate(updatedTemplate)
                            submissionFeedback = "➕ Pregunta '${questionPrompt}' agregada al formulario."
                            showAddQuestionDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_question_button")
                ) {
                    Text("Agregar Pregunta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // DIÁLOGO: VISTA PREVIA PORTAL PÚBLICO DEL CANDIDATO CON INTERACCIÓN Y VALIDACIONES
    if (showPublicPreviewDialog && selectedTemplate != null) {
        val userAnswers = remember { mutableStateMapOf<String, String>() }
        var previewCandidateName by remember { mutableStateOf("Carlos Ramírez") }
        var previewCandidatePhone by remember { mutableStateOf("5512345678") }
        var previewValidationFeedback by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPublicPreviewDialog = false },
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = ElectricBlue)
                        Text("Previsualización Portal Candidato (Firebase Web)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Text("URL: https://talentry-app.web.app/form/${selectedTemplate.id}", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = selectedTemplate.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricBlueOnContainer
                                )
                                Text(selectedTemplate.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (previewValidationFeedback != null) {
                        item {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = RoseError.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, RoseError)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RoseError)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(previewValidationFeedback!!, style = MaterialTheme.typography.labelSmall, color = RoseError, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = previewCandidateName,
                            onValueChange = { previewCandidateName = it },
                            label = { Text("Nombre Completo (*)") },
                            modifier = Modifier.fillMaxWidth().testTag("preview_candidate_name"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = previewCandidatePhone,
                            onValueChange = { previewCandidatePhone = it },
                            label = { Text("Teléfono WhatsApp (*)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Render questions dynamically with conditional visibility logic
                    items(selectedTemplate.questions, key = { it.id }) { q ->
                        // Check conditional dependency
                        val isVisible = q.conditionalRule == null || run {
                            val parentAns = userAnswers[q.conditionalRule.parentQuestionId]
                            parentAns.equals(q.conditionalRule.expectedAnswer, ignoreCase = true)
                        }

                        if (isVisible) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(q.promptText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        if (q.isRequired) {
                                            Text(" *", color = RoseError, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (q.helpText.isNotBlank()) {
                                        Text(q.helpText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    when (q.type) {
                                        QuestionType.TEXT_SHORT, QuestionType.TEXT_LONG -> {
                                            OutlinedTextField(
                                                value = userAnswers[q.id] ?: "",
                                                onValueChange = { userAnswers[q.id] = it },
                                                placeholder = { Text(q.placeholder.ifBlank { "Escribe tu respuesta aquí..." }) },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        QuestionType.SPLIT_NAME -> {
                                            val currentVal = userAnswers[q.id] ?: ""
                                            val parts = currentVal.split("|").let { if (it.size == 3) it else listOf("", "", "") }
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedTextField(
                                                    value = parts[0],
                                                    onValueChange = { userAnswers[q.id] = "$it|${parts[1]}|${parts[2]}" },
                                                    label = { Text("Nombre(s)") },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    OutlinedTextField(
                                                        value = parts[1],
                                                        onValueChange = { userAnswers[q.id] = "${parts[0]}|$it|${parts[2]}" },
                                                        label = { Text("Ap. Paterno") },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    OutlinedTextField(
                                                        value = parts[2],
                                                        onValueChange = { userAnswers[q.id] = "${parts[0]}|${parts[1]}|$it" },
                                                        label = { Text("Ap. Materno") },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                        QuestionType.YES_NO -> {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FilterChip(
                                                    selected = userAnswers[q.id] == "Sí",
                                                    onClick = { userAnswers[q.id] = "Sí" },
                                                    label = { Text("Sí") }
                                                )
                                                FilterChip(
                                                    selected = userAnswers[q.id] == "No",
                                                    onClick = { userAnswers[q.id] = "No" },
                                                    label = { Text("No") }
                                                )
                                            }
                                        }
                                        QuestionType.MULTIPLE_CHOICE, QuestionType.DROPDOWN -> {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                q.options.forEach { opt ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable { userAnswers[q.id] = opt }
                                                    ) {
                                                        RadioButton(
                                                            selected = userAnswers[q.id] == opt,
                                                            onClick = { userAnswers[q.id] = opt }
                                                        )
                                                        Text(opt, style = MaterialTheme.typography.bodyMedium)
                                                    }
                                                }
                                            }
                                        }
                                        QuestionType.CHECKBOXES -> {
                                            val currentSet = (userAnswers[q.id] ?: "").split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                q.options.forEach { opt ->
                                                    val isChecked = currentSet.contains(opt)
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                val newSet = if (isChecked) currentSet - opt else currentSet + opt
                                                                userAnswers[q.id] = newSet.joinToString(", ")
                                                            }
                                                    ) {
                                                        Checkbox(
                                                            checked = isChecked,
                                                            onCheckedChange = { checked ->
                                                                val newSet = if (checked) currentSet + opt else currentSet - opt
                                                                userAnswers[q.id] = newSet.joinToString(", ")
                                                            }
                                                        )
                                                        Text(opt, style = MaterialTheme.typography.bodyMedium)
                                                    }
                                                }
                                            }
                                        }
                                        QuestionType.FILE_UPLOAD -> {
                                            OutlinedButton(
                                                onClick = { userAnswers[q.id] = "documento_candidato_${System.currentTimeMillis() % 1000}.pdf" },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(userAnswers[q.id] ?: "Adjuntar Expediente / PDF", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        QuestionType.DATE -> {
                                            OutlinedTextField(
                                                value = userAnswers[q.id] ?: "",
                                                onValueChange = { userAnswers[q.id] = it },
                                                label = { Text("Fecha (${q.validationRule.dateFormat})") },
                                                placeholder = { Text(q.validationRule.dateFormat) },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Perform client-side validation check
                        var validationError: String? = null
                        for (q in selectedTemplate.questions) {
                            val isVisible = q.conditionalRule == null || run {
                                val parentAns = userAnswers[q.conditionalRule.parentQuestionId]
                                parentAns.equals(q.conditionalRule.expectedAnswer, ignoreCase = true)
                            }

                            if (isVisible && q.isRequired) {
                                val ans = userAnswers[q.id] ?: ""
                                if (ans.isBlank()) {
                                    validationError = "Por favor responde la pregunta requerida: '${q.promptText}'"
                                    break
                                }
                                if (q.validationRule.type == ValidationType.EMAIL && (!ans.contains("@") || !ans.contains("."))) {
                                    validationError = "Respuesta en '${q.promptText}' debe ser un Email válido."
                                    break
                                }
                                if (q.validationRule.type == ValidationType.PHONE && ans.filter { it.isDigit() }.length < 10) {
                                    validationError = "Teléfono en '${q.promptText}' debe contener al menos 10 dígitos."
                                    break
                                }
                            }
                        }

                        if (validationError != null) {
                            previewValidationFeedback = validationError
                        } else {
                            val answersList = selectedTemplate.questions.map { q ->
                                val ansText = userAnswers[q.id] ?: "N/A"
                                FormAnswerItem(questionPrompt = q.promptText, answerText = ansText)
                            }
                            onSimulateCandidateSubmission(
                                selectedTemplate.id,
                                selectedTemplate.title,
                                previewCandidateName,
                                answersList
                            )
                            submissionFeedback = "🚀 Formulario enviado por '$previewCandidateName'. Candidato registrado en CRM Talentry."
                            showPublicPreviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                    modifier = Modifier.testTag("submit_preview_button")
                ) {
                    Text("🚀 Publicar y Enviar Respuesta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublicPreviewDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showShareFormDialog && selectedTemplate != null) {
        AlertDialog(
            onDismissRequest = { showShareFormDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366))
                    Text("Compartir con Candidatos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Formulario Activo: ${selectedTemplate.title}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Enlace Card (Clickable to Copy)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val formUrl = "https://talentry-app.web.app/form/${selectedTemplate.id}"
                                clipboardManager.setText(AnnotatedString(formUrl))
                                Toast.makeText(context, "📋 Link copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                submissionFeedback = "📋 Link '$formUrl' copiado al portapapeles."
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Enlace Público Web Firebase (Cloud Hosted):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = ElectricBlue, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                "https://talentry-app.web.app/form/${selectedTemplate.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }

                    // Acciones Rápidas
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val formUrl = "https://talentry-app.web.app/form/${selectedTemplate.id}"
                                clipboardManager.setText(AnnotatedString(formUrl))
                                Toast.makeText(context, "📋 Link copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                submissionFeedback = "📋 Link '$formUrl' copiado al portapapeles."
                                showShareFormDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copiar Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val formUrl = "https://talentry-app.web.app/form/${selectedTemplate.id}"
                                val messageText = "¡Hola! Te compartimos el enlace para responder el formulario '${selectedTemplate.title}' de Talentry:\n$formUrl"
                                clipboardManager.setText(AnnotatedString(formUrl))
                                Toast.makeText(context, "📋 Link copiado y listo para compartir", Toast.LENGTH_SHORT).show()

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, messageText)
                                    type = "text/plain"
                                }
                                try {
                                    val shareIntent = Intent.createChooser(sendIntent, "Compartir enlace del formulario")
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    // Fallback handled
                                }

                                submissionFeedback = "🚀 Enlace enviado por WhatsApp/Compartir: $formUrl"
                                showShareFormDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Mensaje de plantilla WhatsApp para el candidato
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mensaje para Candidatos:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            Text(
                                "¡Hola! Te compartimos el enlace para responder el formulario '${selectedTemplate.title}' de Talentry:\nhttps://talentry-app.web.app/form/${selectedTemplate.id}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showShareFormDialog = false }) {
                    Text("Listo")
                }
            }
        )
    }
}
