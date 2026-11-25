package com.example.scripturesuprise.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scripturesuprise.data.repository.ScriptureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ScriptureRepository
) : ViewModel() {

    private val _refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Idle)
    val refreshStatus: StateFlow<RefreshStatus> = _refreshStatus

    fun refreshScriptures() {
        viewModelScope.launch {
            _refreshStatus.value = RefreshStatus.Loading("Starting import...")
            val result = repository.refreshScriptures { progressMessage ->
                _refreshStatus.value = RefreshStatus.Loading(progressMessage)
            }
            if (result.isSuccess) {
                _refreshStatus.value = RefreshStatus.Success
            } else {
                _refreshStatus.value = RefreshStatus.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}

sealed interface RefreshStatus {
    object Idle : RefreshStatus
    data class Loading(val message: String) : RefreshStatus
    object Success : RefreshStatus
    data class Error(val message: String) : RefreshStatus
}
