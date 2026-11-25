package com.example.scripturesuprise.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scripturesuprise.R
import com.example.scripturesuprise.data.local.entity.ScriptureVerse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFilters: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scripture Surprise") },
                actions = {
                    Button(onClick = onNavigateToFilters) { Text("Filters") }
                    Button(onClick = onNavigateToSettings) { Text("Settings") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Black)
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator()
                is HomeUiState.Empty -> {
                    Text(
                        "No scriptures match your filters. Adjust your selections.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is HomeUiState.Success -> {
                    ScriptureDisplay(state.verse)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.loadRandomVerse() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Scripture")
            }

            if (uiState is HomeUiState.Success) {
                val verse = (uiState as HomeUiState.Success).verse
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(verse.churchUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Read in Context")
                }
            }
        }
    }
}

@Composable
fun ScriptureDisplay(verse: ScriptureVerse) {
    Text(
        text = "${verse.book} ${verse.chapter}:${verse.verse}",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "\"${verse.text}\"",
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 28.sp,
            fontStyle = FontStyle.Italic
        ),
        textAlign = TextAlign.Center
    )
}
