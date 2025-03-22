package com.example.morsetospeech.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConversionRecord(
    val morseCode: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TTSState(
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f
)

class HistoryViewModel : ViewModel() {
    private val _history = mutableStateListOf<ConversionRecord>()
    val history: List<ConversionRecord> = _history

    private val _ttsState = MutableStateFlow(TTSState())
    val ttsState: StateFlow<TTSState> = _ttsState.asStateFlow()

    fun addConversion(morseCode: String, text: String) {
        viewModelScope.launch {
            _history.add(0, ConversionRecord(morseCode, text))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _history.clear()
        }
    }

    fun updateTTSSettings(pitch: Float, speechRate: Float) {
        viewModelScope.launch {
            _ttsState.value = TTSState(pitch, speechRate)
        }
    }
}