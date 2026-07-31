package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
fun ExpedienteDigitalScreen(
    candidates: List<Candidate>,
    documents: List<CandidateDocument>,
    timeline: List<DossierTimelineEvent>,
    messages: List<WhatsAppMessage>,
    formSubmissions: List<FormSubmission>,
    onUpdateDocumentStatus: (String, DocumentStatus) -> Unit,
    onAddDocument: (String, String) -> Unit = { _, _ -> },
    onAddTimelineEvent: (String, String, TimelineEventType) -> Unit,
    onNavigateToWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCandidateId by remember { mutableStateOf(candidates.firstOrNull()?.id ?: "CAND-01") }
    val selectedCandidate = candidates.firstOrNull { it.id == selectedCandidateId } ?: candidates.firstOrNull()
    var activeTab by remember { mutableStateOf(0) } // 0 = Checklist, 1 = Timeline, 2 = WhatsApp, 3 = Formularios
    var showAddDocDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Expediente Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FolderShared, contentDescription = null, tint = ElectricBlueOnContainer)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Expediente Digital 360°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ATS • WhatsApp CRM • Formularios • Auditoría",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Scrollable Candidate Switcher
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Seleccionar expediente:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(candidates) { c ->
                            val isSel = c.id == selectedCandidate?.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedCandidateId = c.id }
                            ) {
                                Text(
                                    text = c.fullName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Responsive Workspace: Single Column on Mobile, 2-Column on Wide Screens
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val isWide = maxWidth >= 600.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Candidate Info Card (32% width)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(0.32f)
                            .fillMaxHeight()
                    ) {
                        if (selectedCandidate != null) {
                            CandidateSidebarContent(
                                selectedCandidate = selectedCandidate,
                                onNavigateToWhatsApp = onNavigateToWhatsApp,
                                onAddTimelineEvent = onAddTimelineEvent
                            )
                        }
                    }

                    // Right Column: Tabbed Canvas (68% width)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(0.68f)
                            .fillMaxHeight()
                    ) {
                        TabCanvasContent(
                            activeTab = activeTab,
                            onTabSelected = { activeTab = it },
                            documents = documents,
                            timeline = timeline,
                            messages = messages,
                            formSubmissions = formSubmissions,
                            selectedCandidate = selectedCandidate,
                            onUpdateDocumentStatus = onUpdateDocumentStatus,
                            onShowAddDocDialog = { showAddDocDialog = true },
                            onAddTimelineEvent = onAddTimelineEvent
                        )
                    }
                }
            } else {
                // Mobile Layout: Column Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedCandidate != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedCandidate.fullName.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedCandidate.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${selectedCandidate.appliedVacancyTitle} • ${selectedCandidate.city}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElectricBlue,
                                        maxLines = 1
                                    )
                                }
                                StatusBadge(status = selectedCandidate.currentStatus)
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        TabCanvasContent(
                            activeTab = activeTab,
                            onTabSelected = { activeTab = it },
                            documents = documents,
                            timeline = timeline,
                            messages = messages,
                            formSubmissions = formSubmissions,
                            selectedCandidate = selectedCandidate,
                            onUpdateDocumentStatus = onUpdateDocumentStatus,
                            onShowAddDocDialog = { showAddDocDialog = true },
                            onAddTimelineEvent = onAddTimelineEvent
                        )
                    }
                }
            }
        }
    }

    if (showAddDocDialog) {
        var docNameInput by remember { mutableStateOf("") }
        var fileNameInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDocDialog = false },
            title = { Text("Adjuntar Documento al Expediente 360°", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Los archivos adjuntados se vinculan de inmediato al candidato seleccionado y quedan disponibles para el equipo de RH.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = docNameInput,
                        onValueChange = { docNameInput = it },
                        label = { Text("Tipo de Documento (Ej: CURP / Comprobante)") },
                        placeholder = { Text("Ej: Comprobante de Domicilio 2026") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fileNameInput,
                        onValueChange = { fileNameInput = it },
                        label = { Text("Nombre del Archivo") },
                        placeholder = { Text("Ej: recibo_luz_candidato.pdf") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docNameInput.isNotBlank()) {
                            onAddDocument(
                                docNameInput,
                                fileNameInput.ifBlank { "adjunto_rh_2026.pdf" }
                            )
                            showAddDocDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Subir al Expediente")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDocDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CandidateSidebarContent(
    selectedCandidate: Candidate,
    onNavigateToWhatsApp: () -> Unit,
    onAddTimelineEvent: (String, String, TimelineEventType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar & Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedCandidate.fullName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Column {
                Text(
                    text = selectedCandidate.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = selectedCandidate.appliedVacancyTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricBlue
                )
                StatusBadge(status = selectedCandidate.currentStatus)
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // AI Fit Score Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer),
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlueOnContainer, modifier = Modifier.size(16.dp))
                        Text("Score de Encaje IA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = ElectricBlueOnContainer)
                    }
                    Text(
                        text = "${selectedCandidate.aiMatchScore}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricBlueOnContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { selectedCandidate.aiMatchScore / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = ElectricBlueOnContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedCandidate.aiSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricBlueOnContainer
                )
            }
        }

        // Conversational AI Intent Label
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.MarkChatRead, contentDescription = null, tint = Color(0xFF1E8E3E))
                Column {
                    Text("Intención Conversacional IA:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1E8E3E))
                    Text("Alta Intención • Bajo riesgo de abandono", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E8E3E))
                }
            }
        }

        // Contact info
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("INFORMACIÓN DE CONTACTO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("📱 Tel: ${selectedCandidate.phone}", style = MaterialTheme.typography.bodySmall)
            Text("📧 Email: ${selectedCandidate.email}", style = MaterialTheme.typography.bodySmall)
            Text("📍 Ciudad: ${selectedCandidate.city} (${selectedCandidate.experienceYears} años exp)", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToWhatsApp,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.MarkChatUnread, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("WhatsApp", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    onAddTimelineEvent(
                        "Llamada telefónica de seguimiento",
                        "Reclutador se comunicó para confirmar documentación pendiente.",
                        TimelineEventType.STAGE_CHANGE
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Llamar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TabCanvasContent(
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    documents: List<CandidateDocument>,
    timeline: List<DossierTimelineEvent>,
    messages: List<WhatsAppMessage>,
    formSubmissions: List<FormSubmission>,
    selectedCandidate: Candidate?,
    onUpdateDocumentStatus: (String, DocumentStatus) -> Unit,
    onShowAddDocDialog: () -> Unit,
    onAddTimelineEvent: (String, String, TimelineEventType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = ElectricBlue,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Checklist (${documents.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Timeline (${timeline.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { onTabSelected(2) },
                text = { Text("WhatsApp (${messages.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { onTabSelected(3) },
                text = { Text("Formularios (${formSubmissions.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab contents
        when (activeTab) {
            0 -> {
                // TAB 0: DOCUMENT CHECKLIST
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Gestión Documental Integrada 360°",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = onShowAddDocDialog,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.testTag("add_doc_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Subir / Solicitar", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(documents, key = { it.id }) { doc ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth().testTag("doc_item_${doc.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (doc.status) {
                                                    DocumentStatus.VALIDATED -> EmeraldLight
                                                    DocumentStatus.RECEIVED -> ElectricBlueContainer
                                                    DocumentStatus.PENDING -> AmberLight
                                                    DocumentStatus.REJECTED -> RoseLight
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (doc.status) {
                                                DocumentStatus.VALIDATED -> Icons.Default.CheckCircle
                                                DocumentStatus.RECEIVED -> Icons.Default.Description
                                                DocumentStatus.PENDING -> Icons.Default.Pending
                                                DocumentStatus.REJECTED -> Icons.Default.Error
                                            },
                                            contentDescription = null,
                                            tint = when (doc.status) {
                                                DocumentStatus.VALIDATED -> EmeraldOnContainer
                                                DocumentStatus.RECEIVED -> ElectricBlueOnContainer
                                                DocumentStatus.PENDING -> AmberOnContainer
                                                DocumentStatus.REJECTED -> RoseOnContainer
                                            }
                                        )
                                    }

                                    Column {
                                        Text(text = doc.docName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(text = "Archivo: ${doc.fileName} • Fecha: ${doc.uploadDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onUpdateDocumentStatus(doc.id, DocumentStatus.VALIDATED) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Validar", tint = Color(0xFF1E8E3E))
                                    }

                                    IconButton(
                                        onClick = { onUpdateDocumentStatus(doc.id, DocumentStatus.REJECTED) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Rechazar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: DOSSIER TIMELINE
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(timeline, key = { it.id }) { event ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (event.type) {
                                                TimelineEventType.APPLICATION -> ElectricBlueContainer
                                                TimelineEventType.WHATSAPP_AUTO -> ElectricBlueContainer
                                                TimelineEventType.FORM_SUBMITTED -> EmeraldLight
                                                TimelineEventType.INTERVIEW -> AmberLight
                                                TimelineEventType.STAGE_CHANGE -> RoseLight
                                                TimelineEventType.AI_INSIGHT -> ElectricBlueContainer
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (event.type) {
                                            TimelineEventType.APPLICATION -> Icons.Default.Description
                                            TimelineEventType.WHATSAPP_AUTO -> Icons.AutoMirrored.Filled.Chat
                                            TimelineEventType.FORM_SUBMITTED -> Icons.Default.AssignmentTurnedIn
                                            TimelineEventType.INTERVIEW -> Icons.Default.Event
                                            TimelineEventType.STAGE_CHANGE -> Icons.Default.SwapHoriz
                                            TimelineEventType.AI_INSIGHT -> Icons.Default.AutoAwesome
                                        },
                                        contentDescription = null,
                                        tint = ElectricBlue
                                    )
                                }

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(text = event.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = event.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: WHATSAPP MESSAGES LOG
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isCandidate = msg.sender == MessageSender.CANDIDATE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isCandidate) Arrangement.Start else Arrangement.End
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCandidate) MaterialTheme.colorScheme.surfaceVariant else ElectricBlueContainer
                                ),
                                modifier = Modifier.widthIn(max = 340.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.sender.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = msg.content, style = MaterialTheme.typography.bodySmall)
                                    Text(text = msg.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: FORM SUBMISSIONS
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(formSubmissions, key = { it.id }) { sub ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = sub.formTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    StatusBadge(status = "Enviado: ${sub.submittedAt}")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                sub.answers.forEach { ans ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(text = ans.questionPrompt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = ans.answerText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
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
