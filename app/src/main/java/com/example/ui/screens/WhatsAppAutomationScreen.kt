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
fun WhatsAppAutomationScreen(
    rules: List<WhatsAppRule>,
    messages: List<WhatsAppMessage>,
    onAddRule: (WhatsAppRule) -> Unit,
    onToggleRule: (String) -> Unit,
    onSendMessage: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Reglas No-Code, 1 = Simulador en Vivo, 2 = Horarios & Plantillas
    var showNewRuleDialog by remember { mutableStateOf(false) }
    var candidateInputText by remember { mutableStateOf("") }
    var selectedCandidateName by remember { mutableStateOf("Carlos Ramírez") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
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
                            .background(Color(0xFF25D366).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkChatUnread,
                            contentDescription = null,
                            tint = Color(0xFF1E8E3E)
                        )
                    }
                    Column {
                        Text(
                            text = "WhatsApp Automation & Motor de Reglas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Motor no-code integrado • Detección de palabras clave • Escalamiento inteligente a Reclutador Humano",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showNewRuleDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E3E)),
                    modifier = Modifier.testTag("new_rule_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nueva Regla No-Code", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = ElectricBlue
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Motor de Reglas No-Code (${rules.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Simulador & Bandeja en Vivo (${messages.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Horarios & Variables Dinámicas", fontWeight = FontWeight.Bold) }
            )
        }

        // Tab Content
        when (activeTab) {
            0 -> {
                // TAB 0: NO-CODE RULES ENGINE
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                    items(rules, key = { it.id }) { rule ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_rule_${rule.id}")
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
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (rule.isEnabled) Color(0xFF25D366) else RoseError)
                                        )
                                        Text(
                                            text = rule.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        StatusBadge(status = if (rule.isEnabled) "Activa" else "Pausada")
                                        Switch(
                                            checked = rule.isEnabled,
                                            onCheckedChange = { onToggleRule(rule.id) },
                                            modifier = Modifier.testTag("switch_rule_${rule.id}")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "SI el mensaje contiene:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    rule.triggerKeywords.forEach { kw ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "\"$kw\"", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "ENTONCES acción:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                    Text(
                                        text = rule.actionType.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (rule.newCandidateStage != null) {
                                        StatusBadge(status = "Etapa -> ${rule.newCandidateStage}")
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
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
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: LIVE SIMULATOR & WHATSAPP TRAY
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Candidate Chat Info & Suggestion Chips (40% width)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "SIMULAR MENSAJE DEL CANDIDATO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Selecciona o escribe una frase para ver al Bot de Talentry actuar en vivo:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val quickSimulatedInputs = listOf(
                                "¡Sí, estoy muy interesado!",
                                "¿Cuáles son mis documentos para ingreso?",
                                "¿Qué horario de entrevista tienen disponible?",
                                "Tengo una duda con el puesto, AYUDA urgente"
                            )

                            quickSimulatedInputs.forEach { simText ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onSendMessage(simText, selectedCandidateName)
                                        }
                                        .testTag("quick_sim_${simText.take(10)}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "\"$simText\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Escribir mensaje personalizado:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

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

                    // Right Column: Live Chat View (60% width)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
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
                                            .size(38.dp)
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

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

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
            }

            2 -> {
                // TAB 2: SCHEDULE & VARIABLES
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
}
