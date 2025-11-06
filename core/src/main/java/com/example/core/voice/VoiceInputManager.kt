package com.example.core.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceInputManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _recognitionResult = MutableStateFlow<VoiceResult?>(null)
    val recognitionResult: StateFlow<VoiceResult?> = _recognitionResult.asStateFlow()
    
    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    fun startListening(activity: FragmentActivity) {
        if (!isSpeechRecognitionAvailable()) {
            _recognitionResult.value = VoiceResult.Error("Speech recognition not available")
            return
        }
        
        _isListening.value = true
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your task...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        activity.startActivityForResult(intent, VOICE_REQUEST_CODE)
    }
    
    fun processVoiceResult(requestCode: Int, resultCode: Int, data: Intent?) {
        _isListening.value = false
        
        if (requestCode == VOICE_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val spokenText = results[0]
                _recognitionResult.value = VoiceResult.Success(spokenText)
            } else {
                _recognitionResult.value = VoiceResult.Error("No speech detected")
            }
        } else {
            _recognitionResult.value = VoiceResult.Error("Speech recognition failed")
        }
    }
    
    fun clearResult() {
        _recognitionResult.value = null
    }
    
    companion object {
        const val VOICE_REQUEST_CODE = 1001
    }
}

sealed class VoiceResult {
    data class Success(val text: String) : VoiceResult()
    data class Error(val message: String) : VoiceResult()
}
