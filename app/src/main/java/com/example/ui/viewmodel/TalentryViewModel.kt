package com.example.ui.viewmodel

import android.app.Application as AndroidApp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TalentryDatabase
import com.example.data.model.Candidate
import com.example.data.model.Interview
import com.example.data.model.Task
import com.example.data.model.Vacancy
import com.example.data.model.Application as RecruitmentApp
import com.example.data.repository.TalentryRepository
import com.example.data.service.GeminiAiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TalentryViewModel(application: AndroidApp) : AndroidViewModel(application) {

    private val repository = TalentryRepository(TalentryDatabase.getDatabase(application))
    private val aiService = GeminiAiService()

    // Database flows
    val vacancies: StateFlow<List<Vacancy>> = repository.vacancies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val candidates: StateFlow<List<Candidate>> = repository.candidates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applications: StateFlow<List<RecruitmentApp>> = repository.applications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val interviews: StateFlow<List<Interview>> = repository.interviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBranchFilter = MutableStateFlow("Todas")
    val selectedBranchFilter: StateFlow<String> = _selectedBranchFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("Todos")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // AI State
    private val _aiOutputText = MutableStateFlow("")
    val aiOutputText: StateFlow<String> = _aiOutputText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBranchFilter(branch: String) {
        _selectedBranchFilter.value = branch
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // CRUD operations
    fun addVacancy(vacancy: Vacancy) {
        viewModelScope.launch { repository.addVacancy(vacancy) }
    }

    fun updateVacancy(vacancy: Vacancy) {
        viewModelScope.launch { repository.updateVacancy(vacancy) }
    }

    fun deleteVacancy(id: String) {
        viewModelScope.launch { repository.deleteVacancy(id) }
    }

    fun addCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.addCandidate(candidate)
            // Automatically add application entry
            repository.addApplication(
                RecruitmentApp(
                    id = "APP-${System.currentTimeMillis() % 10000}",
                    candidateId = candidate.id,
                    candidateName = candidate.fullName,
                    vacancyId = candidate.appliedVacancyId,
                    vacancyTitle = candidate.appliedVacancyTitle,
                    stage = "Postulado",
                    rating = 4
                )
            )
        }
    }

    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch { repository.updateCandidate(candidate) }
    }

    fun deleteCandidate(id: String) {
        viewModelScope.launch { repository.deleteCandidate(id) }
    }

    fun updateApplicationStage(applicationId: String, candidateId: String, newStage: String) {
        viewModelScope.launch {
            repository.updateApplicationStage(applicationId, newStage)
            // Sync currentStatus in candidate entity
            val currentCandidates = candidates.value
            currentCandidates.find { it.id == candidateId }?.let { candidate ->
                repository.updateCandidate(candidate.copy(currentStatus = newStage))
            }
        }
    }

    fun addInterview(interview: Interview) {
        viewModelScope.launch { repository.addInterview(interview) }
    }

    fun updateInterviewResult(id: String, status: String, feedback: String) {
        viewModelScope.launch { repository.updateInterviewResult(id, status, feedback) }
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repository.addTask(task) }
    }

    fun toggleTaskCompleted(id: String, isCompleted: Boolean) {
        viewModelScope.launch { repository.toggleTaskCompleted(id, isCompleted) }
    }

    // AI Operations
    fun generateJobDescriptionWithAi(title: String, branch: String, requirements: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateJobDescription(title, branch, requirements)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun analyzeCandidateFitWithAi(candidateName: String, experience: String, vacancyTitle: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.analyzeCandidateFit(candidateName, experience, vacancyTitle)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun generateInterviewSummaryWithAi(candidateName: String, feedback: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateInterviewSummary(candidateName, feedback)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun generateAutoResponseWithAi(candidateName: String, stage: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.generateAutoResponse(candidateName, stage)
            _aiOutputText.value = result
            _isAiLoading.value = false
        }
    }

    fun clearAiOutput() {
        _aiOutputText.value = ""
    }
}
