package com.example.scripturesuprise.ui.filters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    onNavigateBack: () -> Unit,
    viewModel: FiltersViewModel = hiltViewModel()
) {
    val volumes by viewModel.volumes.collectAsState()
    val excludedVolumes by viewModel.excludedVolumes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scripture Filters") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    "Scriptures",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(volumes) { volume ->
                val isExcluded = excludedVolumes.contains(volume)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = !isExcluded,
                        onCheckedChange = { viewModel.toggleVolume(volume, isExcluded) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(volume)
                }
            }
        }
    }
}
