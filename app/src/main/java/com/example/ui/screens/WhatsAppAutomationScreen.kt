package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun WhatsAppAutomationScreen(
    rules: List<WhatsAppRule>,
    iftttRules: List<WorkflowIftttRule> = emptyList(),
    messages: List<WhatsAppMessage>,
    onAddRule: (WhatsAppRule) -> Unit,
    onToggleRule: (String) -> Unit,
    onAddIftttRule: (WorkflowIftttRule) -> Unit = {},
    onToggleIftttRule: (String) -> Unit = {},
    onExecuteIftttRule: (WorkflowIftttRule, String) -> Unit = { _, _ -> },
    onSendMessage: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Reglas WhatsApp, 1 = Motor IFTTT, 2 = Simulador
    var showNewRuleDialog by remember { mutableStateOf(false) }
    var showNewIftttDialog by remember { mutableStateOf(false) }
    var showGasInfoDialog by remember { mutableStateOf(false) }
    var executionFeedback by remember { mutableStateOf<String?>(null) }
    var candidateInputText by remember { mutableStateOf("") }
    var selectedCandidateName by remember { mutableStateOf("Carlos Ramírez") }

    if (showGasInfoDialog) {
        AlertDialog(
            onDismissRequest = { showGasInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = ElectricBlue)
                    Text("Integración Google Apps Script", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Sincronización Serverless 24/7",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    Text(
                        text = "Talentry se conecta de forma nativa con Google Apps Script mediante Webhooks REST sin requerir servidores externos ni costos de infraestructura.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Google Sheets: Registro en tiempo real de candidatos.", style = MaterialTheme.typography.labelSmall)
                            Text("• Google Calendar: Agendamiento automático de citas.", style = MaterialTheme.typography.labelSmall)
                            Text("• Gmail & Drive: Notificaciones e integración de expedientes.", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGasInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // COMPACT HERO HEADER
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MarkChatUnread,
                                contentDescription = null,
                                tint = Color(0xFF1E8E3E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "WhatsApp Automation & Reglas",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                IconButton(
                                    onClick = { showGasInfoDialog = true },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info Integración GAS",
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Motor no-code estilo n8n • Operación 24/7",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showNewRuleDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_rule_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Regla WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showNewIftttDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_ifttt_button")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Workflow IFTTT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (executionFeedback != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { executionFeedback = null }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Text(
                            text = executionFeedback!!,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    Text("OK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        // REDESIGNED TABS (Single line, scrolling pills, no text breaking)
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = ElectricBlue,
            edgePadding = 0.dp,
            divider = {}
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = "Reglas WhatsApp (${rules.size})",
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = "Workflows n8n (${iftttRules.size})",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Text(
                        text = "Simulador Chat (${messages.size})",
                        fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            )
        }

        // Tab Content
        when (activeTab) {
            0 -> {
                // TAB 0: NO-CODE RULES ENGINE
                var showTab0Info by remember { mutableStateOf(false) }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reglas de Respuesta Automática",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = { showTab0Info = !showTab0Info },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showTab0Info) "Ocultar ayuda" else "Ver ayuda", fontSize = 12.sp)
                            }
                        }
                    }

                    if (showTab0Info) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlueOnContainer)
                                    Text(
                                        text = "Las reglas no-code se evalúan en tiempo real. Utiliza variables {{candidate_name}} y {{vacancy_title}} para personalizar respuestas automáticas.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElectricBlueOnContainer
                                    )
                                }
                            }
                        }
                    }

                    items(rules, key = { it.id }) { rule ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_rule_${rule.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
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
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (rule.isEnabled) Color(0xFF25D366) else RoseError)
                                        )
                                        Text(
                                            text = rule.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        StatusBadge(status = if (rule.isEnabled) "Activa" else "Pausada")
                                        Switch(
                                            checked = rule.isEnabled,
                                            onCheckedChange = { onToggleRule(rule.id) },
                                            modifier = Modifier
                                                .scale(0.85f)
                                                .testTag("switch_rule_${rule.id}")
                                        )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "SI el mensaje contiene:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(rule.triggerKeywords) { kw ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "\"$kw\"",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "ENTONCES:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue
                                        )
                                        Text(
                                            text = rule.actionType.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (rule.newCandidateStage != null) {
                                        StatusBadge(status = "Etapa: ${rule.newCandidateStage}")
                                    }
                                }

                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Plantilla de Respuesta Automática:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = rule.responseTemplate,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            executionFeedback = "🤖 Regla evaluada: '${rule.title}'. Respuesta enviada a $selectedCandidateName."
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Probar Regla", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: IFTTT RECRUITMENT WORKFLOW ENGINE ("Si sucede esto -> haz esto")
                var showTab1Info by remember { mutableStateOf(false) }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Workflows de Reclutamiento IFTTT",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = { showTab1Info = !showTab1Info },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showTab1Info) "Ocultar ayuda" else "Ver ayuda", fontSize = 12.sp)
                            }
                        }
                    }

                    if (showTab1Info) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlueOnContainer)
                                    Column {
                                        Text(
                                            text = "Motor de Automatización de Flujos (IFTTT)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlueOnContainer
                                        )
                                        Text(
                                            text = "Configura reglas globales para todo el ciclo de vida del candidato: confirmaciones de entrevista, carga documental, pausas de vacantes y encuestas de salida.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ElectricBlueOnContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(iftttRules, key = { it.id }) { iftttRule ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ifttt_rule_${iftttRule.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (iftttRule.isEnabled) Color(0xFF1E8E3E) else RoseError)
                                        )
                                        Text(
                                            text = iftttRule.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        StatusBadge(status = if (iftttRule.isEnabled) "Activa" else "Pausada")
                                        Switch(
                                            checked = iftttRule.isEnabled,
                                            onCheckedChange = { onToggleIftttRule(iftttRule.id) }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Trigger Box
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                                Text("SI SUCEDE:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(iftttRule.triggerType.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(iftttRule.triggerDescription, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    // Action Box
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer.copy(alpha = 0.4f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E8E3E), modifier = Modifier.size(16.dp))
                                                Text("REALIZA ESTO:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E8E3E))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(iftttRule.actionType.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(iftttRule.actionDescription, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (iftttRule.targetStage != null) {
                                        StatusBadge(status = "Cambio automático → ${iftttRule.targetStage}")
                                    } else {
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    Button(
                                        onClick = {
                                            onExecuteIftttRule(iftttRule, selectedCandidateName)
                                            executionFeedback = "⚡ Regla ejecutada: '${iftttRule.title}'. Mensaje enviado, expediente y timeline actualizados."
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        modifier = Modifier.testTag("run_ifttt_${iftttRule.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("⚡ Ejecutar y Probar Ahora", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: LIVE SIMULATOR & WHATSAPP TRAY
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val isWide = maxWidth >= 600.dp
                    val quickSimulatedInputs = remember {
                        listOf(
                            "¡Sí, estoy muy interesado!",
                            "¿Cuáles son mis documentos para ingreso?",
                            "¿Qué horario de entrevista tienen disponible?",
                            "Tengo una duda con el puesto, AYUDA urgente"
                        )
                    }

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column: Candidate Chat Info & Suggestion Chips (35% width)
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(0.35f)
                                    .fillMaxHeight()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "SIMULAR MENSAJE DEL CANDIDATO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Selecciona o escribe una frase para ver al Bot de Talentry actuar en vivo:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    quickSimulatedInputs.forEach { simText ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clickable {
                                                    onSendMessage(simText, selectedCandidateName)
                                                }
                                                .testTag("quick_sim_${simText.take(10)}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "\"$simText\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Escribir mensaje personalizado:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = candidateInputText,
                                            onValueChange = { candidateInputText = it },
                                            placeholder = { Text("Ej: SÍ me interesa") },
                                            modifier = Modifier.weight(1f).testTag("sim_custom_input"),
                                            singleLine = true
                                        )
                                        Button(
                                            onClick = {
                                                if (candidateInputText.isNotBlank()) {
                                                    onSendMessage(candidateInputText, selectedCandidateName)
                                                    candidateInputText = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                                            modifier = Modifier.testTag("send_sim_button")
                                        ) {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }

                            // Right Column: Live Chat View (65% width)
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(0.65f)
                                    .fillMaxHeight()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp)
                                ) {
                                    // Chat Header
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
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricBlueContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("CR", fontWeight = FontWeight.Bold, color = ElectricBlueOnContainer)
                                            }
                                            Column {
                                                Text("Carlos Ramírez", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                                Text("Vacante: Operador de Montacargas • Apodaca", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        StatusBadge(status = "WhatsApp En Línea")
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Chat Messages
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        items(messages, key = { it.id }) { msg ->
                                            val isCandidate = msg.sender == MessageSender.CANDIDATE
                                            val isRecruiter = msg.sender == MessageSender.RECRUITER

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = if (isCandidate) Arrangement.Start else Arrangement.End
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(
                                                        topStart = 16.dp,
                                                        topEnd = 16.dp,
                                                        bottomStart = if (isCandidate) 4.dp else 16.dp,
                                                        bottomEnd = if (isCandidate) 16.dp else 4.dp
                                                    ),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = when {
                                                            isCandidate -> MaterialTheme.colorScheme.surface
                                                            isRecruiter -> Color(0xFFE8F5E9)
                                                            else -> ElectricBlueContainer
                                                        }
                                                    ),
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                                    modifier = Modifier.widthIn(max = 420.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = when (msg.sender) {
                                                                    MessageSender.CANDIDATE -> msg.candidateName
                                                                    MessageSender.RECRUITER -> "Reclutador Francisco (Escalado Humano)"
                                                                    MessageSender.BOT_AUTOMATION -> "Bot Talentry Automation"
                                                                },
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isCandidate) MaterialTheme.colorScheme.onSurface else ElectricBlue
                                                            )
                                                            Text(
                                                                text = msg.timestamp,
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = msg.content,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )

                                                        if (msg.triggeredRuleTitle != null) {
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(6.dp))
                                                                    .background(Color.White.copy(alpha = 0.7f))
                                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(12.dp))
                                                                    Text(
                                                                        text = msg.triggeredRuleTitle,
                                                                        style = MaterialTheme.typography.labelSmall,
                                                                        fontSize = 10.sp,
                                                                        color = ElectricBlue
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
                    } else {
                        // Mobile Layout: Integrated Chat with Bottom Simulation Bar & Message Input
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                // Chat Header
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
                                            Text("CR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElectricBlueOnContainer)
                                        }
                                        Column {
                                            Text("Carlos Ramírez", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            Text("Operador de Montacargas • Apodaca", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    StatusBadge(status = "En Línea")
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Chat Messages List
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(messages, key = { it.id }) { msg ->
                                        val isCandidate = msg.sender == MessageSender.CANDIDATE
                                        val isRecruiter = msg.sender == MessageSender.RECRUITER

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = if (isCandidate) Arrangement.Start else Arrangement.End
                                        ) {
                                            Card(
                                                shape = RoundedCornerShape(
                                                    topStart = 14.dp,
                                                    topEnd = 14.dp,
                                                    bottomStart = if (isCandidate) 2.dp else 14.dp,
                                                    bottomEnd = if (isCandidate) 14.dp else 2.dp
                                                ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = when {
                                                        isCandidate -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                                        isRecruiter -> Color(0xFFE8F5E9)
                                                        else -> ElectricBlueContainer
                                                    }
                                                ),
                                                modifier = Modifier.widthIn(max = 280.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = when (msg.sender) {
                                                                MessageSender.CANDIDATE -> msg.candidateName
                                                                MessageSender.RECRUITER -> "Reclutador"
                                                                MessageSender.BOT_AUTOMATION -> "Bot Talentry"
                                                            },
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCandidate) MaterialTheme.colorScheme.onSurface else ElectricBlue
                                                        )
                                                        Text(
                                                            text = msg.timestamp,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = msg.content,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )

                                                    if (msg.triggeredRuleTitle != null) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "⚡ ${msg.triggeredRuleTitle}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 10.sp,
                                                            color = ElectricBlue
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Simulation Phrases Carousel
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Simular mensaje rápido:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(quickSimulatedInputs) { simText ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.clickable {
                                                    onSendMessage(simText, selectedCandidateName)
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(text = simText, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(12.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Custom Message Input Field & Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = candidateInputText,
                                        onValueChange = { candidateInputText = it },
                                        placeholder = { Text("Escribir mensaje personalizado...", fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f).testTag("sim_custom_input"),
                                        singleLine = true
                                    )
                                    IconButton(
                                        onClick = {
                                            if (candidateInputText.isNotBlank()) {
                                                onSendMessage(candidateInputText, selectedCandidateName)
                                                candidateInputText = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1E8E3E))
                                            .testTag("send_sim_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: SCHEDULE & VARIABLES
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Horarios de Atención Automática", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Durante horario laboral (Lunes a Viernes 08:00 a 20:00), el motor responde en menos de 3 segundos y escala a tu reclutador asignado. Fuera de horario, envía confirmación de recepción y programa notificación matutina.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Variables Dinámicas Disponibles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("• {{candidate_name}} : Nombre del candidato (Ej: Carlos Ramírez)", style = MaterialTheme.typography.bodyMedium)
                            Text("• {{vacancy_title}} : Puesto al que se postula (Ej: Operador de Montacargas)", style = MaterialTheme.typography.bodyMedium)
                            Text("• {{recruiter_name}} : Nombre del reclutador responsable (Ej: Francisco Enciso)", style = MaterialTheme.typography.bodyMedium)
                            Text("• {{interview_link}} : Enlace directo o sala física programada", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    // New Rule Dialog
    if (showNewRuleDialog) {
        var ruleTitle by remember { mutableStateOf("") }
        var ruleKeywords by remember { mutableStateOf("") }
        var ruleTemplate by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewRuleDialog = false },
            title = { Text("Nueva Regla No-Code", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ruleTitle,
                        onValueChange = { ruleTitle = it },
                        label = { Text("Título de la regla") },
                        modifier = Modifier.fillMaxWidth().testTag("new_rule_title_input")
                    )
                    OutlinedTextField(
                        value = ruleKeywords,
                        onValueChange = { ruleKeywords = it },
                        label = { Text("Palabras clave separadas por coma (ej: info, sueldo, turno)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ruleTemplate,
                        onValueChange = { ruleTemplate = it },
                        label = { Text("Respuesta automática (puedes usar {{candidate_name}})") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleTitle.isNotBlank()) {
                            val newRule = WhatsAppRule(
                                id = "RULE-${System.currentTimeMillis() % 1000}",
                                title = ruleTitle,
                                triggerKeywords = ruleKeywords.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                actionType = RuleActionType.SEND_TEMPLATE,
                                responseTemplate = ruleTemplate.ifBlank { "¡Hola {{candidate_name}}! Hemos recibido tu mensaje." },
                                isEnabled = true
                            )
                            onAddRule(newRule)
                            showNewRuleDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_rule_button")
                ) {
                    Text("Guardar Regla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewRuleDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showNewIftttDialog) {
        var iftttTitle by remember { mutableStateOf("") }
        var selectedTrigger by remember { mutableStateOf(IftttTriggerType.INTERVIEW_CONFIRMED) }
        var selectedAction by remember { mutableStateOf(IftttActionType.UPDATE_STAGE_AND_NOTIFY) }
        var targetStageText by remember { mutableStateOf("Entrevista") }

        AlertDialog(
            onDismissRequest = { showNewIftttDialog = false },
            title = { Text("Nueva Regla de Reclutamiento IFTTT", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = iftttTitle,
                        onValueChange = { iftttTitle = it },
                        label = { Text("Título descriptivo de la regla") },
                        placeholder = { Text("Ej: Si el candidato envía INE -> marcar en revisión") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Selecciona el Disparador (SI SUCEDE ESTO):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    IftttTriggerType.values().forEach { tType ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTrigger = tType }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedTrigger == tType, onClick = { selectedTrigger = tType })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(tType.label, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Text("Selecciona la Acción Automática (REALIZA ESTO):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    IftttActionType.values().forEach { aType ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAction = aType }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedAction == aType, onClick = { selectedAction = aType })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(aType.label, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    OutlinedTextField(
                        value = targetStageText,
                        onValueChange = { targetStageText = it },
                        label = { Text("Nueva Etapa del Candidato (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (iftttTitle.isNotBlank()) {
                            onAddIftttRule(
                                WorkflowIftttRule(
                                    id = "WFR-${System.currentTimeMillis() % 1000}",
                                    title = iftttTitle,
                                    triggerType = selectedTrigger,
                                    triggerDescription = "Configurado por usuario en portal",
                                    actionType = selectedAction,
                                    actionDescription = "Acción automatizada activada por ${selectedTrigger.label}",
                                    targetStage = targetStageText.ifBlank { null },
                                    isEnabled = true
                                )
                            )
                            showNewIftttDialog = false
                        }
                    }
                ) {
                    Text("Guardar Regla IFTTT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewIftttDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
