package com.example.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.LabDao
import com.example.data.ResearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel cho ResearchDashboardScreen.
 */
class ResearchDashboardViewModel(
    private val repository: ResearchRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResearchRepository.DashboardStatsUiState())
    val uiState: StateFlow<ResearchRepository.DashboardStatsUiState> = _uiState.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportSuccess = MutableStateFlow<android.net.Uri?>(null)
    val exportSuccess: StateFlow<android.net.Uri?> = _exportSuccess.asStateFlow()

    private val _exportError = MutableStateFlow<String?>(null)
    val exportError: StateFlow<String?> = _exportError.asStateFlow()

    init {
        // Collect dashboard stats from repository
        viewModelScope.launch {
            repository.dashboardStats.collect { stats ->
                _uiState.value = stats
            }
        }
    }

    /**
     * Xuất dữ liệu nghiên cứu ra file CSV.
     */
    fun exportToCsv() {
        if (_isExporting.value) return

        viewModelScope.launch {
            _isExporting.value = true
            _exportError.value = null

            val uri = repository.exportResearchDataToCsv(appContext)

            if (uri != null) {
                _exportSuccess.value = uri
            } else {
                _exportError.value = "Không thể xuất file CSV. Vui lòng thử lại."
            }

            _isExporting.value = false
        }
    }

    /**
     * Xóa trạng thái export sau khi đã xử lý.
     */
    fun clearExportState() {
        _exportSuccess.value = null
        _exportError.value = null
    }

    /**
     * Lấy Intent chia sẻ cho file CSV đã xuất.
     */
    fun getShareIntent(uri: android.net.Uri): android.content.Intent {
        return repository.createShareIntent(appContext, uri)
    }
}

/**
 * Factory cho ResearchDashboardViewModel.
 */
class ResearchDashboardViewModelFactory(
    private val repository: ResearchRepository? = null,
    private val context: Context? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResearchDashboardViewModel::class.java)) {
            val repo = repository ?: run {
                // Fallback: tạo repository với LabDatabase (cần context)
                // Trong thực tế nên truyền context qua constructor
                throw IllegalStateException(
                    "ResearchRepository và Context cần được truyền vào ResearchDashboardViewModelFactory"
                )
            }
            val ctx = context ?: repo.hashCode().toString() as Context
            return ResearchDashboardViewModel(repo, ctx) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
