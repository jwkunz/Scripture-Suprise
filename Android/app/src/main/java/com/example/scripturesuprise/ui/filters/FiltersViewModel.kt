package com.example.scripturesuprise.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scripturesuprise.data.repository.ScriptureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val repository: ScriptureRepository
) : ViewModel() {

    val volumes: StateFlow<List<String>> = repository.getVolumes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val excludedVolumes: StateFlow<Set<String>> = repository.excludedVolumes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleVolume(volume: String, isExcluded: Boolean) {
        viewModelScope.launch {
            repository.toggleVolume(volume, !isExcluded)
        }
    }
}
