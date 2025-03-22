package com.example.morsetospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.morsetospeech.ui.*
import com.example.morsetospeech.ui.theme.MorseToSpeechTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(this, "Text to Speech initialization failed!", Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            MorseToSpeechTheme {
                MorseToSpeechApp(textToSpeech)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorseToSpeechApp(textToSpeech: TextToSpeech?) {
    val viewModel: HistoryViewModel = viewModel()
    val context = LocalContext.current
    var morseInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    val ttsState by viewModel.ttsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Morse to Speech") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                    IconButton(onClick = { morseInput = "" }) {
                        Icon(Icons.Default.Clear, "Clear input")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MorseInputField(
                value = morseInput,
                onValueChange = { 
                    morseInput = it
                    errorMessage = ""
                }
            )

            Button(
                onClick = {
                    MorseConverter.convertMorseToText(morseInput)
                        .onSuccess { text ->
                            viewModel.addConversion(morseInput, text)
                            textToSpeech?.let { tts ->
                                tts.setPitch(ttsState.pitch)
                                tts.setSpeechRate(ttsState.speechRate)
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                            errorMessage = ""
                        }
                        .onFailure { error ->
                            errorMessage = error.message ?: "Conversion failed"
                        }
                },
                enabled = morseInput.isNotEmpty()
            ) {
                Text("Convert and Speak")
            }

            ErrorMessage(errorMessage)

            ConversionHistory(
                history = viewModel.history,
                onPlayClick = { text ->
                    textToSpeech?.let { tts ->
                        tts.setPitch(ttsState.pitch)
                        tts.setSpeechRate(ttsState.speechRate)
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        if (showSettings) {
            TTSSettingsDialog(
                currentPitch = ttsState.pitch,
                currentRate = ttsState.speechRate,
                onPitchChange = { pitch ->
                    viewModel.updateTTSSettings(pitch, ttsState.speechRate)
                },
                onRateChange = { rate ->
                    viewModel.updateTTSSettings(ttsState.pitch, rate)
                },
                onDismiss = { showSettings = false }
            )
        }
    }
}