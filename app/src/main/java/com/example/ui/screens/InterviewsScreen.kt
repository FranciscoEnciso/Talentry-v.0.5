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
import com.example.data.model.Interview
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RoseError

@Composable
fun InterviewsScreen(
    interviews: List<Interview>,
    onScheduleInterviewClick: () -> Unit,
    onUpdateResult: (id: String, status: String, feedback: String) -> Unit,
    onGenerateAiSummary: (candidateName: String, feedback: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedInterviewForFeedback by remember { mutableStateOf<Interview?>(null) }
    var feedbackText by remember { mutableStateOf("") }

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
                    Column {
                        Text(
                            text = "Gestión de Entrevistas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("interviews_header_title")
                        )
                        Text(
                            text = "Registra asistencia, resultados y notas de entrevistas operativas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onScheduleInterviewClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.testTag("new_interview_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nueva")
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
                                text = "No hay entrevistas registradas",
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
                                text = "${interview.vacancyTitle} • ${interview.scheduledDateTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Modalidad: ${interview.type} (${interview.locationOrLink})",
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onUpdateResult(interview.id, "Completada", "Candidato asistió puntualmente y cumplió el perfil.")
                                    },
                                    modifier = Modifier.weight(1f).testTag("mark_completed_${interview.id}")
                                ) {
                                    Text("Aprobó")
                                }

                                OutlinedButton(
                                    onClick = {
                                        onUpdateResult(interview.id, "No Asistió", "Candidato no se presentó a la cita.")
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("No Asistió")
                                }

                                IconButton(
                                    onClick = {
                                        selectedInterviewForFeedback = interview
                                        feedbackText = interview.feedback
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar retroalimentación")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Feedback Modal Dialog outside LazyColumn
        selectedInterviewForFeedback?.let { intv ->
            AlertDialog(
                onDismissRequest = { selectedInterviewForFeedback = null },
                title = { Text("Registrar Evaluación: ${intv.candidateName}") },
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
                        Text("Guardar")
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
