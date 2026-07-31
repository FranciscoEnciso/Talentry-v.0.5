package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Candidate
import com.example.data.model.Vacancy

@Composable
fun NewCandidateDialog(
    vacancies: List<Vacancy>,
    onDismiss: () -> Unit,
    onSave: (Candidate) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Ciudad de México") }
    var expYearsText by remember { mutableStateOf("2") }
    var notes by remember { mutableStateOf("") }

    var selectedVacancy by remember {
        mutableStateOf(vacancies.firstOrNull() ?: Vacancy("VAC-101", "Operador de Montacargas", "CDMX", "Logística", 10, 0, "Activa", "$12,000", "", ""))
    }
    var vacancyDropdownExpanded by remember { mutableStateOf(false) }

    var isManualDirectEntry by remember { mutableStateOf(false) }
    var selectedInitialStage by remember { mutableStateOf("Postulado") }
    var stageDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Alta Manual de Candidato", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Alta Manual Directa Notice
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isManualDirectEntry,
                            onCheckedChange = {
                                isManualDirectEntry = it
                                if (it) selectedInitialStage = "Contratado" else selectedInitialStage = "Postulado"
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Alta Manual / Directa", style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Registra candidatos directos omitiendo el flujo habitual de filtros.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("candidate_name_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono / WhatsApp") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expYearsText,
                        onValueChange = { expYearsText = it },
                        label = { Text("Años Exp.") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Stage selector for Alta Manual
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { stageDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Etapa Inicial: $selectedInitialStage")
                    }
                    DropdownMenu(
                        expanded = stageDropdownExpanded,
                        onDismissRequest = { stageDropdownExpanded = false }
                    ) {
                        listOf("Postulado", "Llamada Pendiente", "Entrevista", "Documentos", "Contratado").forEach { stage ->
                            DropdownMenuItem(
                                text = { Text(stage) },
                                onClick = {
                                    selectedInitialStage = stage
                                    stageDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { vacancyDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vacante: ${selectedVacancy.title}")
                    }
                    DropdownMenu(
                        expanded = vacancyDropdownExpanded,
                        onDismissRequest = { vacancyDropdownExpanded = false }
                    ) {
                        vacancies.forEach { vac ->
                            DropdownMenuItem(
                                text = { Text("${vac.title} (${vac.branch})") },
                                onClick = {
                                    selectedVacancy = vac
                                    vacancyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas de Alta / Origen Candidato") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onSave(
                            Candidate(
                                id = "CAN-${System.currentTimeMillis() % 10000}",
                                fullName = fullName,
                                phone = phone.ifBlank { "55 1234 5678" },
                                email = email.ifBlank { "candidato@talentry.com" },
                                city = city,
                                experienceYears = expYearsText.toIntOrNull() ?: 1,
                                currentStatus = selectedInitialStage,
                                aiMatchScore = if (isManualDirectEntry) 98 else (75..98).random(),
                                aiSummary = if (isManualDirectEntry) "Alta manual directa autorizada por Reclutador Senior." else "Perfil registrado correctamente para ${selectedVacancy.title}.",
                                notes = if (isManualDirectEntry) "[Alta Manual Directa]: ${notes.ifBlank { "Ingreso directo autorizado sin proceso habitual." }}" else notes.ifBlank { "Registrado manualmente en plataforma." },
                                appliedVacancyId = selectedVacancy.id,
                                appliedVacancyTitle = selectedVacancy.title
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_candidate_button")
            ) {
                Text(if (isManualDirectEntry) "Alta Manual Directa" else "Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
