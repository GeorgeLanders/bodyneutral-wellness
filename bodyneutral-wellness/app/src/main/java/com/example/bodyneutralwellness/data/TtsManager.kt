package com.example.bodyneutralwellness.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        }
    }

    fun speak(text: String, speedRate: Float = 1.0f, pitch: Float = 1.0f) {
        if (!isInitialized) {
            pendingText = text
            return
        }

        try {
            tts?.apply {
                setSpeechRate(speedRate)
                setPitch(pitch)
                speak(text, TextToSpeech.QUEUE_FLUSH, null, "coach_reply_speech")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shutdown() {
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tts = null
        isInitialized = false
    }
}
