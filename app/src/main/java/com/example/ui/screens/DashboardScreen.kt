package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candidate
import com.example.data.model.Vacancy
import com.example.data.repository.TalentryRepository
import com.example.ui.components.StatsCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueContainer
import com.example.ui.theme.ElectricBlueOnContainer
import com.example.ui.theme.EmeraldSuccess

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*

@Composable
fun DashboardScreen(
    vacancies: List<Vacancy>,
    candidates: List<Candidate>,
    monthlyGoal: Int = 25,
    onUpdateMonthlyGoal: (Int) -> Unit = {},
    onNavigateToTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeVacanciesCount = vacancies.count { it.status == "Activa" }
    val totalPositionsOpen = vacancies.filter { it.status == "Activa" }.sumOf { it.positionsOpen }
    val totalPositionsFilled = vacancies.filter { it.status == "Activa" }.sumOf { it.positionsFilled }
    val pendingContactCount = candidates.count { it.currentStatus == "Postulado" || it.currentStatus == "Llamada Pendiente" }

    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInputText by remember { mutableStateOf(monthlyGoal.toString()) }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Configurar Meta Mensual", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Define el número objetivo de contrataciones para este mes:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = goalInputText,
                        onValueChange = { goalInputText = it.filter { char -> char.isDigit() } },
                        label = { Text("Meta de Contrataciones") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newGoal = goalInputText.toIntOrNull() ?: monthlyGoal
                        if (newGoal > 0) {
                            onUpdateMonthlyGoal(newGoal)
                        }
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Guardar Meta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INDICADORES GLOBALES DE RECLUTAMIENTO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("dashboard_title")
                )
            }
        }

        // Metrics Grid (Clickable cards opening corresponding modules)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Vacantes Activas",
                        value = activeVacanciesCount.toString(),
                        subtitle = "$totalPositionsOpen plazas abiertas • Ver vacantes",
                        icon = Icons.Default.Work,
                        iconColor = ElectricBlue,
                        containerColor = ElectricBlueContainer,
                        contentColor = ElectricBlueOnContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab(2) } // Vacancies tab
                    )
                    StatsCard(
                        title = "Pendientes de Contactar",
                        value = pendingContactCount.toString(),
                        subtitle = "Candidatos por filtrar • Ir a candidatos",
                        icon = Icons.Default.Phone,
                        iconColor = EmeraldSuccess,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab(3) } // Candidates tab
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Contrataciones Mes",
                        value = totalPositionsFilled.toString(),
                        subtitle = "Meta: $monthlyGoal contrataciones",
                        icon = Icons.Default.People,
                        iconColor = ElectricBlue,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab(4) } // Pipeline tab
                    )
                    StatsCard(
                        title = "Tiempo Prom. Cierre",
                        value = "4.2 Días",
                        subtitle = "35% más eficiente • Ver reportes",
                        icon = Icons.Default.Speed,
                        iconColor = Color(0xFFF59E0B),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab(7) } // Reports tab
                    )
                }
            }
        }

        // Operational Progress Banner (Configurable Goal)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cumplimiento de Meta Mensual",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    goalInputText = monthlyGoal.toString()
                                    showGoalDialog = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Meta",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "$totalPositionsFilled de $monthlyGoal Contrataciones",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val progressRatio = (totalPositionsFilled.toFloat() / maxOf(1, monthlyGoal)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = ElectricBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Avance actual: ${(progressRatio * 100).toInt()}% completado de la meta mensual.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section Title: Candidatos Recientes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CANDIDATOS RECIENTES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${candidates.size} Total",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue
                )
            }
        }

        items(candidates.take(4), key = { "cand_dash_${it.id}" }) { cand ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cand.fullName.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cand.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = cand.currentStatus)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = cand.appliedVacancyTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section Title: Equipo de Reclutamiento
        item {
            Text(
                text = "PROGRESO POR RECLUTADOR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(TalentryRepository.staticRecruiters, key = { it.id }) { recruiter ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElectricBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = recruiter.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlueOnContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recruiter.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = recruiter.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${recruiter.monthlyHires} contratados",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                        Text(
                            text = "${recruiter.activeCandidates} activos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
