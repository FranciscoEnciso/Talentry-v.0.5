package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Candidate
import com.example.data.model.Interview

@Composable
fun NewInterviewDialog(
    candidates: List<Candidate>,
    onDismiss: () -> Unit,
    onSave: (Interview) -> Unit
) {
    var selectedCandidate by remember {
        mutableStateOf(candidates.firstOrNull() ?: Candidate("CAN-501", "Roberto Gómez", "5512345678", "", "CDMX", 2, "Postulado", 90, "", "", "VAC-101", "Operador de Montacargas"))
    }
    var candidateMenuExpanded by remember { mutableStateOf(false) }

    var dateTime by remember { mutableStateOf("2026-07-30 03:30 PM") }
    var type by remember { mutableStateOf("Presencial") }
    var locationOrLink by remember { mutableStateOf("Oficina Central - Sala 1") }
    var interviewer by remember { mutableStateOf("Carlos Mendoza") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agendar Entrevista", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { candidateMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth().testTag("select_interview_candidate_button")
                    ) {
                        Text("Candidato: ${selectedCandidate.fullName}")
                    }
                    DropdownMenu(
                        expanded = candidateMenuExpanded,
                        onDismissRequest = { candidateMenuExpanded = false }
                    ) {
                        candidates.forEach { cand ->
                            DropdownMenuItem(
                                text = { Text("${cand.fullName} (${cand.appliedVacancyTitle})") },
                                onClick = {
                                    selectedCandidate = cand
                                    candidateMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateTime,
                    onValueChange = { dateTime = it },
                    label = { Text("Fecha y Hora") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Modalidad (Presencial / Telefónica / Virtual)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = locationOrLink,
                    onValueChange = { locationOrLink = it },
                    label = { Text("Ubicación o Enlace") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = interviewer,
                    onValueChange = { interviewer = it },
                    label = { Text("Reclutador / Entrevistador") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Interview(
                            id = "INT-${System.currentTimeMillis() % 10000}",
                            candidateId = selectedCandidate.id,
                            candidateName = selectedCandidate.fullName,
                            vacancyTitle = selectedCandidate.appliedVacancyTitle,
                            scheduledDateTime = dateTime,
                            type = type,
                            locationOrLink = locationOrLink,
                            interviewer = interviewer,
                            status = "Programada"
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.testTag("save_interview_button")
            ) {
                Text("Agendar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
