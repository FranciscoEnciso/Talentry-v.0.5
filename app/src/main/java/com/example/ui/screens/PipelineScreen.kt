package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.Application
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError

@Composable
fun PipelineScreen(
    applications: List<Application>,
    onUpdateStage: (applicationId: String, candidateId: String, newStage: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stages = listOf(
        "Postulado",
        "Llamada / Filtro",
        "Entrevista",
        "Documentos",
        "Oferta / Contratado",
        "Rechazado"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Embudo de Seleccion Kanban",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("pipeline_header_title")
        )
        Text(
            text = "Arrastra o avanza candidatos a lo largo de las etapas del proceso.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Kanban Column Container
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            stages.forEach { stageName ->
                val stageApps = applications.filter { it.stage.equals(stageName, ignoreCase = true) }

                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    // Column Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stageName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stageApps.size.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(stageApps, key = { it.id }) { app ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("kanban_card_${app.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = app.candidateName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = app.vacancyTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Stage Navigation Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val currentIndex = stages.indexOf(stageName)

                                        if (currentIndex > 0) {
                                            IconButton(
                                                onClick = {
                                                    val prevStage = stages[currentIndex - 1]
                                                    onUpdateStage(app.id, app.candidateId, prevStage)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowBack,
                                                    contentDescription = "Retroceder etapa",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.size(28.dp))
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            if (stageName != "Rechazado") {
                                                IconButton(
                                                    onClick = { onUpdateStage(app.id, app.candidateId, "Rechazado") },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Descartar",
                                                        tint = RoseError,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            if (stageName != "Oferta / Contratado" && stageName != "Rechazado") {
                                                IconButton(
                                                    onClick = { onUpdateStage(app.id, app.candidateId, "Oferta / Contratado") },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Contratar",
                                                        tint = EmeraldSuccess,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (currentIndex < stages.size - 2 && stageName != "Rechazado") {
                                            IconButton(
                                                onClick = {
                                                    val nextStage = stages[currentIndex + 1]
                                                    onUpdateStage(app.id, app.candidateId, nextStage)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = "Avanzar etapa",
                                                    tint = ElectricBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.size(28.dp))
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
