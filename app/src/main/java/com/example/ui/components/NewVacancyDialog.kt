package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Vacancy
import com.example.data.repository.TalentryRepository

@Composable
fun NewVacancyDialog(
    onDismiss: () -> Unit,
    onSave: (Vacancy) -> Unit,
    onGenerateWithAi: (title: String, branch: String, requirements: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf(TalentryRepository.staticBranches.first().name) }
    var department by remember { mutableStateOf("Operaciones") }
    var positionsOpenText by remember { mutableStateOf("10") }
    var salaryRange by remember { mutableStateOf("$12,000 MXN") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Vacante Operativa", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Puesto") },
                    placeholder = { Text("Ej: Auxiliar de Almacén") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("vacancy_title_input")
                )

                OutlinedTextField(
                    value = selectedBranch,
                    onValueChange = { selectedBranch = it },
                    label = { Text("Sucursal / Sede") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = positionsOpenText,
                        onValueChange = { positionsOpenText = it },
                        label = { Text("Plazas Vacantes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = salaryRange,
                        onValueChange = { salaryRange = it },
                        label = { Text("Sueldo Estimado") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = requirements,
                    onValueChange = { requirements = it },
                    label = { Text("Requisitos Principales") },
                    placeholder = { Text("Ej: Secundaria, experiencia 1 año, DC-3") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción del Puesto") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = {
                        if (title.isNotEmpty()) {
                            onGenerateWithAi(title, selectedBranch, requirements)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("ai_generate_description_button")
                ) {
                    Text("✨ Autogenerar Descripción con IA")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            Vacancy(
                                id = "VAC-${System.currentTimeMillis() % 10000}",
                                title = title,
                                branch = selectedBranch,
                                department = department,
                                positionsOpen = positionsOpenText.toIntOrNull() ?: 5,
                                positionsFilled = 0,
                                status = "Activa",
                                salaryRange = salaryRange,
                                description = description.ifBlank { "Vacante operativa para $title en $selectedBranch." },
                                requirements = requirements.ifBlank { "Experiencia mínima y secundaria requerida." }
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_vacancy_button")
            ) {
                Text("Publicar Vacante")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
