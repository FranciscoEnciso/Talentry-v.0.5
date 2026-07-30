package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AiOutputCard
import com.example.ui.theme.PurpleAI

@Composable
fun AiScreen(
    aiOutputText: String,
    isAiLoading: Boolean,
    onClearAiOutput: () -> Unit,
    onGenerateJobDescription: (title: String, branch: String, reqs: String) -> Unit,
    onAnalyzeFit: (name: String, exp: String, vacancy: String) -> Unit,
    onGenerateAutoMessage: (name: String, stage: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedToolIndex by remember { mutableStateOf(0) }

    // Form inputs
    var titleInput by remember { mutableStateOf("Operador de Montacargas") }
    var branchInput by remember { mutableStateOf("Sucursal CDMX Norte") }
    var reqsInput by remember { mutableStateOf("DC-3 vigente, experiencia 1 año") }

    var candidateNameInput by remember { mutableStateOf("Roberto Gómez") }
    var expInput by remember { mutableStateOf("3 años operando montacargas hombre sentado") }
    var vacancyInput by remember { mutableStateOf("Operador de Montacargas") }

    var candidateStageName by remember { mutableStateOf("María López") }
    var stageInput by remember { mutableStateOf("Entrevista Agendada") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleAI)
                    Text(
                        text = "Inteligencia Artificial Talentry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAI,
                        modifier = Modifier.testTag("ai_header_title")
                    )
                }
                Text(
                    text = "Herramientas con Gemini IA para agilizar tu flujo de reclutamiento masivo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tool Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedToolIndex,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = selectedToolIndex == 0,
                    onClick = { selectedToolIndex = 0 },
                    text = { Text("Descripciones") },
                    modifier = Modifier.testTag("ai_tab_descriptions")
                )
                Tab(
                    selected = selectedToolIndex == 1,
                    onClick = { selectedToolIndex = 1 },
                    text = { Text("Match Candidato") },
                    modifier = Modifier.testTag("ai_tab_match")
                )
                Tab(
                    selected = selectedToolIndex == 2,
                    onClick = { selectedToolIndex = 2 },
                    text = { Text("Mensajes Auto") },
                    modifier = Modifier.testTag("ai_tab_messages")
                )
            }
        }

        // Active Tool Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedToolIndex) {
                        0 -> {
                            Text("Generador de Descripción de Vacantes", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text("Puesto Operativo") },
                                modifier = Modifier.fillMaxWidth().testTag("ai_tool_title_input")
                            )
                            OutlinedTextField(
                                value = branchInput,
                                onValueChange = { branchInput = it },
                                label = { Text("Sucursal") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = reqsInput,
                                onValueChange = { reqsInput = it },
                                label = { Text("Requisitos Relevantes") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    onGenerateJobDescription(titleInput, branchInput, reqsInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAI),
                                modifier = Modifier.fillMaxWidth().testTag("ai_run_description_button")
                            ) {
                                Text("✨ Generar Descripción con Gemini")
                            }
                        }

                        1 -> {
                            Text("Evaluador de Match y Fit de Candidato", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = candidateNameInput,
                                onValueChange = { candidateNameInput = it },
                                label = { Text("Nombre del Candidato") },
                                modifier = Modifier.fillMaxWidth().testTag("ai_tool_cand_name")
                            )
                            OutlinedTextField(
                                value = expInput,
                                onValueChange = { expInput = it },
                                label = { Text("Experiencia y Competencias") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = vacancyInput,
                                onValueChange = { vacancyInput = it },
                                label = { Text("Puesto al que Postula") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    onAnalyzeFit(candidateNameInput, expInput, vacancyInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAI),
                                modifier = Modifier.fillMaxWidth().testTag("ai_run_match_button")
                            ) {
                                Text("✨ Evaluar Compatibilidad con IA")
                            }
                        }

                        2 -> {
                            Text("Generador de Respuestas para WhatsApp", fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = candidateStageName,
                                onValueChange = { candidateStageName = it },
                                label = { Text("Nombre del Candidato") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = stageInput,
                                onValueChange = { stageInput = it },
                                label = { Text("Etapa Actual del Proceso") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    onGenerateAutoMessage(candidateStageName, stageInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAI),
                                modifier = Modifier.fillMaxWidth().testTag("ai_run_auto_msg_button")
                            ) {
                                Text("✨ Crear Plantilla de Mensaje")
                            }
                        }
                    }
                }
            }
        }

        // Output Display Card
        item {
            AiOutputCard(
                text = aiOutputText,
                isLoading = isAiLoading,
                onClear = onClearAiOutput
            )
        }
    }
}
