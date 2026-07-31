package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Interview
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RoseError

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun InterviewsScreen(
    interviews: List<Interview>,
    onScheduleInterviewClick: () -> Unit,
    onUpdateResult: (id: String, status: String, feedback: String) -> Unit,
    onGenerateAiSummary: (candidateName: String, feedback: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedInterviewForFeedback by remember { mutableStateOf<Interview?>(null) }
    var selectedInterviewForReject by remember { mutableStateOf<Interview?>(null) }
    var feedbackText by remember { mutableStateOf("") }
    var rejectionReasonText by remember { mutableStateOf("") }
    var selectedDateFilterIndex by remember { mutableStateOf(0) }

    val daysOfWeek = listOf(
        "Hoy (31 Jul)" to "31 Jul",
        "Mañana (1 Ago)" to "01 Ago",
        "Sáb (2 Ago)" to "02 Ago",
        "Lun (4 Ago)" to "04 Ago",
        "Mar (5 Ago)" to "05 Ago"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Agenda de Entrevistas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("interviews_header_title")
                        )
                        Text(
                            text = "Gestiona evaluaciones, asistencia y notas de entrevista.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onScheduleInterviewClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("new_interview_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agendar", maxLines = 1, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Compact Weekly Calendar Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "VISTA SEMANAL DE CITAS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(daysOfWeek) { idx, (label, _) ->
                            val isSelected = selectedDateFilterIndex == idx
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedDateFilterIndex = idx }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (interviews.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay entrevistas para esta fecha",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(interviews, key = { it.id }) { interview ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("interview_card_${interview.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = interview.candidateName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusBadge(status = interview.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "💼 ${interview.vacancyTitle} • 🕒 ${interview.scheduledDateTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "📍 Modalidad: ${interview.type} (${interview.locationOrLink})",
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (interview.feedback.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📝 Retroalimentación: ${interview.feedback}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons: Aprobó | No Aprobó | No Asistió
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        onUpdateResult(interview.id, "Aprobó", "Candidato cumplió con los requerimientos técnicos del puesto.")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).testTag("mark_completed_${interview.id}")
                                ) {
                                    Text("Aprobó", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        selectedInterviewForReject = interview
                                        rejectionReasonText = ""
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("No Aprobó", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onUpdateResult(interview.id, "No Asistió", "Candidato no se presentó a la entrevista citada.")
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("No Asistió", fontSize = 11.sp)
                                }

                                IconButton(
                                    onClick = {
                                        selectedInterviewForFeedback = interview
                                        feedbackText = interview.feedback
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mandatory Rejection / "No Aprobó" Dialog
        selectedInterviewForReject?.let { intv ->
            AlertDialog(
                onDismissRequest = { selectedInterviewForReject = null },
                title = { Text("Motivo de No Aprobación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Por favor indica el motivo obligatorio por el cual ${intv.candidateName} no aprobó la entrevista:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = rejectionReasonText,
                            onValueChange = { rejectionReasonText = it },
                            label = { Text("Comentarios / Motivo de Rechazo *") },
                            placeholder = { Text("Ej: No cuenta con la experiencia en montacargas requerida.") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (rejectionReasonText.isNotBlank()) {
                                onUpdateResult(intv.id, "No aprobó", rejectionReasonText)
                                selectedInterviewForReject = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                    ) {
                        Text("Registrar No Aprobó")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedInterviewForReject = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // General Feedback Modal Dialog outside LazyColumn
        selectedInterviewForFeedback?.let { intv ->
            AlertDialog(
                onDismissRequest = { selectedInterviewForFeedback = null },
                title = { Text("Evaluación Completa: ${intv.candidateName}") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            label = { Text("Comentarios y Resultado") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedButton(
                            onClick = {
                                if (feedbackText.isNotEmpty()) {
                                    onGenerateAiSummary(intv.candidateName, feedbackText)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generar Resumen IA")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateResult(intv.id, "Completada", feedbackText)
                            selectedInterviewForFeedback = null
                        }
                    ) {
                        Text("Guardar Evaluacion")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedInterviewForFeedback = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
