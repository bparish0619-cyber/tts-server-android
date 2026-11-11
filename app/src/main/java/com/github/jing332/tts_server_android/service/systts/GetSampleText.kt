package com.github.jing332.tts_server_android.service.systts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech

/**
 * Answers Android's GET_SAMPLE_TEXT request for this TTS engine.
 *
 * Why this activity exists: the system TTS settings screen (Samsung's
 * included) plays its sample by first asking the engine for sample text
 * through this intent, and only then sending that text to the speech
 * service. If no activity handles the intent, the settings screen never
 * dispatches a synthesis request at all and its Play button stays
 * silent, even though the engine itself works. Upstream never
 * implemented this, so Play did nothing there too.
 *
 * Contract (android.speech.tts): the request intent carries the locale
 * as the string extras "language", "country" and "variant" (ISO codes,
 * usually the 3-letter forms). On success we return RESULT_OK with the
 * sample sentence in [TextToSpeech.Engine.EXTRA_SAMPLE_TEXT]; for a
 * locale this engine does not support we return RESULT_CANCELED.
 *
 * Supported languages must stay in sync with CheckVoiceData (the voice
 * data this engine advertises) and SystemTtsService.onIsLanguageAvailable.
 */
class GetSampleText : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CHANGE-ME: the sample sentence spoken by the system settings
        // Play button, one per supported language.
        val sampleEnglish = "This is an example of speech synthesis."
        val sampleChinese = "这是语音合成的一个示例。"

        // Accept both the 3-letter (eng / zho) and 2-letter (en / zh)
        // language codes; callers are not consistent about which they send.
        val language = intent.getStringExtra("language")?.lowercase().orEmpty()
        val sample = when (language) {
            "eng", "en" -> sampleEnglish
            "zho", "zh", "chi" -> sampleChinese
            // Unsupported locale: tell the caller instead of guessing.
            else -> null
        }

        if (sample == null) {
            setResult(RESULT_CANCELED)
        } else {
            val returnData = Intent()
            returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, sample)
            setResult(RESULT_OK, returnData)
        }
        finish()
    }
}
