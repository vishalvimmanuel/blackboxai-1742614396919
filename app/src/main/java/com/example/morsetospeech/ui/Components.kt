package com.example.morsetospeech.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.morsetospeech.MorseConverter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MorseInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (MorseConverter.isValidMorseCode(input) || input.isEmpty()) {
                    onValueChange(input)
                }
            },
            label = { Text("Morse Code") },
            supportingText = { Text(MorseConverter.getValidMorseCharacters()) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionHistoryItem(
    record: ConversionRecord,
    onPlayClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = record.morseCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(Date(record.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { onPlayClick(record.text) }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play conversion"
                    )
                }
            }
        }
    }
}

@Composable
fun ConversionHistory(
    history: List<ConversionRecord>,
    onPlayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(history) { record ->
            ConversionHistoryItem(
                record = record,
                onPlayClick = onPlayClick
            )
        }
    }
}

@Composable
fun TTSSettingsDialog(
    currentPitch: Float,
    currentRate: Float,
    onPitchChange: (Float) -> Unit,
    onRateChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TTS Settings") },
        text = {
            Column {
                Text("Pitch: ${String.format("%.1f", currentPitch)}")
                Slider(
                    value = currentPitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.5f..2f,
                    steps = 14
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Speech Rate: ${String.format("%.1f", currentRate)}")
                Slider(
                    value = currentRate,
                    onValueChange = onRateChange,
                    valueRange = 0.5f..2f,
                    steps = 14
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}