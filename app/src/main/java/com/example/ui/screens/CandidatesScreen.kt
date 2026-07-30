package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.Candidate
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PurpleAI

@Composable
fun CandidatesScreen(
    candidates: List<Candidate>,
    searchQuery: String,
    onAddNewCandidateClick: () -> Unit,
    onDeleteCandidate: (String) -> Unit,
    onRunAiFitAnalysis: (Candidate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCandidateForDetail by remember { mutableStateOf<Candidate?>(null) }

    val filteredCandidates = candidates.filter { cand ->
        cand.fullName.contains(searchQuery, ignoreCase = true) ||
                cand.appliedVacancyTitle.contains(searchQuery, ignoreCase = true) ||
                cand.city.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Directorio de Candidatos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("candidates_header_title")
                        )
                        Text(
                            text = "${filteredCandidates.size} postulantes activos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onAddNewCandidateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.testTag("add_candidate_button")
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar")
                    }
                }
            }

            if (filteredCandidates.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sin candidatos registrados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Comienza registrando un nuevo postulante operativo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredCandidates, key = { it.id }) { cand ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("candidate_card_${cand.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cand.fullName.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cand.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "🎯 ${cand.appliedVacancyTitle} • 📍 ${cand.city}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(status = cand.currentStatus)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Match ${cand.aiMatchScore}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleAI
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "📞 ${cand.phone} • ✉️ ${cand.email} • Exp: ${cand.experienceYears} años",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (cand.aiSummary.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "💡 ${cand.aiSummary}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onRunAiFitAnalysis(cand) },
                                    modifier = Modifier.testTag("ai_fit_analysis_${cand.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Evaluar Fit IA")
                                }

                                Row {
                                    TextButton(onClick = { selectedCandidateForDetail = cand }) {
                                        Text("Ver Detalle")
                                    }
                                    IconButton(onClick = { onDeleteCandidate(cand.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Detail Modal
        selectedCandidateForDetail?.let { candidate ->
            AlertDialog(
                onDismissRequest = { selectedCandidateForDetail = null },
                title = { Text(candidate.fullName, style = MaterialTheme.typography.titleLarge) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Vacante Postulada: ${candidate.appliedVacancyTitle}")
                        Text("Etapa Actual: ${candidate.currentStatus}")
                        Text("Teléfono: ${candidate.phone}")
                        Text("Correo: ${candidate.email}")
                        Text("Experiencia: ${candidate.experienceYears} años")
                        Text("Score de Selección: ${candidate.aiMatchScore}%")
                        Text("Resumen IA: ${candidate.aiSummary}")
                        Text("Notas Reclutador: ${candidate.notes}")
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedCandidateForDetail = null }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}
