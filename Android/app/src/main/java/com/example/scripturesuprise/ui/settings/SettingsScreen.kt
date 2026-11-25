package com.example.scripturesuprise.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val status by viewModel.refreshStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Scripture Database",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.refreshScriptures() },
                enabled = status !is RefreshStatus.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (status is RefreshStatus.Loading) "Refreshing..." else "Refresh Scriptures")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val currentStatus = status) {
                is RefreshStatus.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentStatus.message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is RefreshStatus.Success -> Text("Database updated successfully!", color = MaterialTheme.colorScheme.primary)
                is RefreshStatus.Error -> Text("Error: ${currentStatus.message}", color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}
