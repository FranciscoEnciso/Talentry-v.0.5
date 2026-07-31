package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TalentryRepository
import com.example.ui.theme.ElectricBlue

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("Lic. Ana Martínez") }
    var userRole by remember { mutableStateOf("Líder de Reclutamiento & Selección") }
    var userCompany by remember { mutableStateOf("Talentry Enterprise Operations") }
    var monthlyGoalStr by remember { mutableStateOf("25") }
    var slaDaysStr by remember { mutableStateOf("12") }
    var enablePushNotifications by remember { mutableStateOf(true) }
    var enableWhatsappAlerts by remember { mutableStateOf(true) }
    var enableEmailAlerts by remember { mutableStateOf(true) }
    var selectedAiModel by remember { mutableStateOf("Gemini 2.5 Pro (IA Generativa RAG)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Configuración del Sistema SaaS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("settings_header_title")
                )
                Text(
                    text = "Perfil del reclutador, metas de contratación, notificaciones e IA.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section: Perfil del Reclutador
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ElectricBlue)
                        Text("Perfil de Usuario & Empresa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Nombre del Reclutador") },
                        modifier = Modifier.fillMaxWidth().testTag("settings_user_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = userRole,
                        onValueChange = { userRole = it },
                        label = { Text("Puesto / Rol") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = userCompany,
                        onValueChange = { userCompany = it },
                        label = { Text("Empresa u Organización") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Section: Metas de Reclutamiento & SLA
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = ElectricBlue)
                        Text("Objetivos Operativos & SLA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = monthlyGoalStr,
                            onValueChange = { monthlyGoalStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Meta Contrataciones / Mes") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = slaDaysStr,
                            onValueChange = { slaDaysStr = it.filter { char -> char.isDigit() } },
                            label = { Text("SLA Máximo (Días)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section: Configuración de Motor IA Gemini
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricBlue)
                        Text("Motor de Inteligencia Artificial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Text("Selecciona el modelo para parsing de CVs y ranking automático:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    listOf(
                        "Gemini 2.5 Pro (IA Generativa RAG)",
                        "Gemini 2.5 Flash (Ultra Rápido)",
                        "Llama 3 Local / Webhook Custom"
                    ).forEach { model ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedAiModel == model,
                                onClick = { selectedAiModel = model }
                            )
                            Text(model, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Section: Apariencia & Notificaciones
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = ElectricBlue)
                        Text("Apariencia & Notificaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Modo Oscuro (Tema Dark SaaS)", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            modifier = Modifier.testTag("settings_dark_mode_switch")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notificaciones Push Móvil", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = enablePushNotifications, onCheckedChange = { enablePushNotifications = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Alertas por WhatsApp Bot", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = enableWhatsappAlerts, onCheckedChange = { enableWhatsappAlerts = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Resúmenes Diarios por Email", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = enableEmailAlerts, onCheckedChange = { enableEmailAlerts = it })
                    }
                }
            }
        }

        // Section: Sucursales
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = ElectricBlue)
                        Text("Sucursales de Operación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    TalentryRepository.staticBranches.forEach { branch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(branch.name, fontWeight = FontWeight.SemiBold)
                                Text(branch.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${branch.activeVacanciesCount} Vacantes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    Toast.makeText(context, "✅ Configuración de Talentry guardada correctamente", Toast.LENGTH_LONG).show()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Configuración", fontWeight = FontWeight.Bold)
            }
        }
    }
}

