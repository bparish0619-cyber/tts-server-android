package com.github.jing332.tts_server_android.service.systts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech

class CheckVoiceData : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = TextToSpeech.Engine.CHECK_VOICE_DATA_PASS
        val returnData = Intent()

        // Voice data this engine reports to Android, which is what the
        // system TTS settings language picker lists (besides "use system
        // language"). Format: ISO 639-3 language + "-" + ISO 3166 country.
        // English (US) comes first because it is this fork's default
        // language; Chinese remains available. Upstream reported only
        // "zho-CHN", which is why the picker offered no English entry.
        // CHANGE-ME: to offer another language, add its "lll-CCC" entry
        // here and keep SystemTtsService.onIsLanguageAvailable in sync.
        val available: ArrayList<String> = arrayListOf("eng-USA", "zho-CHN")
        val unavailable: ArrayList<String> = arrayListOf()

        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available)
        returnData.putStringArrayListExtra(
            TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES,
            unavailable
        )
        setResult(result, returnData)
        finish()
    }
}