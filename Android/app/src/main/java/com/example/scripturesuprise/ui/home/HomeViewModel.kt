package com.example.scripturesuprise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scripturesuprise.data.local.entity.ScriptureVerse
import com.example.scripturesuprise.data.repository.ScriptureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ScriptureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadRandomVerse()
    }

    fun loadRandomVerse() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val verse = repository.getRandomVerse()
            if (verse != null) {
                _uiState.value = HomeUiState.Success(verse)
            } else {
                _uiState.value = HomeUiState.Empty
            }
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val verse: ScriptureVerse) : HomeUiState
    object Empty : HomeUiState
}
