package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.Task
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueContainer
import com.example.ui.theme.ElectricBlueOnContainer
import com.example.ui.viewmodel.TalentryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentryNavigation(
    viewModel: TalentryViewModel,
    modifier: Modifier = Modifier
) {
    val vacancies by viewModel.vacancies.collectAsState()
    val candidates by viewModel.candidates.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val interviews by viewModel.interviews.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedBranchFilter by viewModel.selectedBranchFilter.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val aiOutputText by viewModel.aiOutputText.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    // Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Dialog & Quick Action States
    var showQuickActionsSheet by remember { mutableStateOf(false) }
    var showNewVacancyDialog by remember { mutableStateOf(false) }
    var showNewCandidateDialog by remember { mutableStateOf(false) }
    var showNewInterviewDialog by remember { mutableStateOf(false) }
    var showNewTaskDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier
                    .width(310.dp)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header Logo & Brand
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "T",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Talentry",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Talento que impulsa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { coroutineScope.launch { drawerState.close() } }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar menú")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Items List
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val drawerMenuItems = listOf(
                            NavigationMenuItem(0, "Mi Día", Icons.Default.WbSunny),
                            NavigationMenuItem(1, "Dashboard", Icons.Default.Dashboard),
                            NavigationMenuItem(3, "Candidatos", Icons.Default.People),
                            NavigationMenuItem(4, "Postulaciones", Icons.Default.ViewColumn),
                            NavigationMenuItem(2, "Vacantes", Icons.Default.Work),
                            NavigationMenuItem(6, "Entrevistas", Icons.Default.AssignmentInd),
                            NavigationMenuItem(5, "Agenda", Icons.Default.CalendarMonth),
                            NavigationMenuItem(7, "Reportes", Icons.Default.BarChart),
                            NavigationMenuItem(9, "IA Assistant", Icons.Default.AutoAwesome),
                            NavigationMenuItem(8, "Configuración", Icons.Default.Settings)
                        )

                        drawerMenuItems.forEach { item ->
                            val isSelected = selectedTab == item.tabIndex
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    selectedTab = item.tabIndex
                                    coroutineScope.launch { drawerState.close() }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = ElectricBlueContainer,
                                    selectedTextColor = ElectricBlueOnContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("drawer_item_${item.tabIndex}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // User Profile Card at bottom
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("FE", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Francisco Enciso",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reclutador Senior",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { coroutineScope.launch { drawerState.close() } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopHeaderBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedBranch = selectedBranchFilter,
                    onBranchSelected = { viewModel.setBranchFilter(it) },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onMenuClick = { coroutineScope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Mi Día
                        BottomNavItem(
                            icon = Icons.Default.Home,
                            label = "Mi Día",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.testTag("bottom_nav_my_day")
                        )

                        // 2. Candidatos
                        BottomNavItem(
                            icon = Icons.Default.People,
                            label = "Candidatos",
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            modifier = Modifier.testTag("bottom_nav_candidates")
                        )

                        // 3. Central FAB (+)
                        FloatingActionButton(
                            onClick = { showQuickActionsSheet = true },
                            containerColor = ElectricBlue,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(52.dp)
                                .testTag("quick_action_fab")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Acciones rápidas")
                        }

                        // 4. Agenda / Tareas
                        BottomNavItem(
                            icon = Icons.Default.CalendarMonth,
                            label = "Agenda",
                            isSelected = selectedTab == 5,
                            onClick = { selectedTab = 5 },
                            modifier = Modifier.testTag("bottom_nav_agenda")
                        )

                        // 5. Más / Drawer Menu
                        BottomNavItem(
                            icon = Icons.Default.Menu,
                            label = "Más",
                            isSelected = drawerState.isOpen,
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("bottom_nav_more")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> MyDayScreen(
                        tasks = tasks,
                        interviewsToday = interviews,
                        onToggleTask = { id, isDone -> viewModel.toggleTaskCompleted(id, isDone) },
                        onAddNewTaskClick = { showNewTaskDialog = true },
                        onScheduleInterviewClick = { showNewInterviewDialog = true }
                    )

                    1 -> DashboardScreen(
                        vacancies = vacancies,
                        candidates = candidates
                    )

                    2 -> VacanciesScreen(
                        vacancies = vacancies,
                        selectedBranchFilter = selectedBranchFilter,
                        onAddNewVacancyClick = { showNewVacancyDialog = true },
                        onDeleteVacancy = { viewModel.deleteVacancy(it) },
                        onToggleStatus = { vac ->
                            val newStatus = if (vac.status == "Activa") "En Pausa" else "Activa"
                            viewModel.updateVacancy(vac.copy(status = newStatus))
                        }
                    )

                    3 -> CandidatesScreen(
                        candidates = candidates,
                        searchQuery = searchQuery,
                        onAddNewCandidateClick = { showNewCandidateDialog = true },
                        onDeleteCandidate = { viewModel.deleteCandidate(it) },
                        onRunAiFitAnalysis = { cand ->
                            viewModel.analyzeCandidateFitWithAi(cand.fullName, "${cand.experienceYears} años exp", cand.appliedVacancyTitle)
                            selectedTab = 9
                        }
                    )

                    4 -> PipelineScreen(
                        applications = applications,
                        onUpdateStage = { appId, candidateId, newStage ->
                            viewModel.updateApplicationStage(appId, candidateId, newStage)
                        }
                    )

                    5 -> AgendaScreen(
                        interviews = interviews,
                        onScheduleInterviewClick = { showNewInterviewDialog = true }
                    )

                    6 -> InterviewsScreen(
                        interviews = interviews,
                        onScheduleInterviewClick = { showNewInterviewDialog = true },
                        onUpdateResult = { id, status, feedback ->
                            viewModel.updateInterviewResult(id, status, feedback)
                        },
                        onGenerateAiSummary = { candName, feedback ->
                            viewModel.generateInterviewSummaryWithAi(candName, feedback)
                            selectedTab = 9
                        }
                    )

                    7 -> ReportsScreen(
                        vacancies = vacancies,
                        candidates = candidates
                    )

                    8 -> SettingsScreen(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() }
                    )

                    9 -> AiScreen(
                        aiOutputText = aiOutputText,
                        isAiLoading = isAiLoading,
                        onClearAiOutput = { viewModel.clearAiOutput() },
                        onGenerateJobDescription = { title, branch, reqs ->
                            viewModel.generateJobDescriptionWithAi(title, branch, reqs)
                        },
                        onAnalyzeFit = { name, exp, vac ->
                            viewModel.analyzeCandidateFitWithAi(name, exp, vac)
                        },
                        onGenerateAutoMessage = { name, stage ->
                            viewModel.generateAutoResponseWithAi(name, stage)
                        }
                    )
                }
            }
        }
    }

    // Quick Action Dialog Sheet
    if (showQuickActionsSheet) {
        AlertDialog(
            onDismissRequest = { showQuickActionsSheet = false },
            title = { Text("Acciones Rápidas", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "Nueva Vacante Operativa",
                        subtitle = "Publicar vacante por sucursal",
                        icon = Icons.Default.Work,
                        onClick = {
                            showQuickActionsSheet = false
                            showNewVacancyDialog = true
                        }
                    )

                    QuickActionButton(
                        title = "Registrar Candidato",
                        subtitle = "Postular nuevo aspirante",
                        icon = Icons.Default.PersonAdd,
                        onClick = {
                            showQuickActionsSheet = false
                            showNewCandidateDialog = true
                        }
                    )

                    QuickActionButton(
                        title = "Agendar Entrevista",
                        subtitle = "Programar cita presencial o virtual",
                        icon = Icons.Default.Event,
                        onClick = {
                            showQuickActionsSheet = false
                            showNewInterviewDialog = true
                        }
                    )

                    QuickActionButton(
                        title = "Crear Tarea de Seguimiento",
                        subtitle = "Recordatorio o llamada pendiente",
                        icon = Icons.Default.CheckCircle,
                        onClick = {
                            showQuickActionsSheet = false
                            showNewTaskDialog = true
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showQuickActionsSheet = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Dialog Renderers
    if (showNewVacancyDialog) {
        NewVacancyDialog(
            onDismiss = { showNewVacancyDialog = false },
            onSave = { newVac -> viewModel.addVacancy(newVac) },
            onGenerateWithAi = { title, branch, reqs ->
                viewModel.generateJobDescriptionWithAi(title, branch, reqs)
                showNewVacancyDialog = false
                selectedTab = 9
            }
        )
    }

    if (showNewCandidateDialog) {
        NewCandidateDialog(
            vacancies = vacancies,
            onDismiss = { showNewCandidateDialog = false },
            onSave = { newCand -> viewModel.addCandidate(newCand) }
        )
    }

    if (showNewInterviewDialog) {
        NewInterviewDialog(
            candidates = candidates,
            onDismiss = { showNewInterviewDialog = false },
            onSave = { newIntv -> viewModel.addInterview(newIntv) }
        )
    }

    if (showNewTaskDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var candidateName by remember { mutableStateOf("Roberto Gómez") }
        var timeSlot by remember { mutableStateOf("04:00 PM") }

        AlertDialog(
            onDismissRequest = { showNewTaskDialog = false },
            title = { Text("Nueva Tarea de Reclutamiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Título de la Tarea") },
                        modifier = Modifier.fillMaxWidth().testTag("new_task_title_input")
                    )
                    OutlinedTextField(
                        value = candidateName,
                        onValueChange = { candidateName = it },
                        label = { Text("Candidato Asociado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Horario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.addTask(
                                Task(
                                    id = "TSK-${System.currentTimeMillis() % 10000}",
                                    title = taskTitle,
                                    category = "Seguimiento",
                                    priority = "Alta",
                                    candidateName = candidateName,
                                    timeSlot = timeSlot,
                                    isCompleted = false
                                )
                            )
                            showNewTaskDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_task_button")
                ) {
                    Text("Guardar Tarea")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTaskDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

data class NavigationMenuItem(
    val tabIndex: Int,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ElectricBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ElectricBlueOnContainer, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
