package com.github.jing332.tts_server_android.constant

import androidx.annotation.IntDef
import com.github.jing332.tts_server_android.R
import com.github.jing332.tts_server_android.app

@IntDef(
    SpeechTarget.ALL,
//    SpeechTarget.ASIDE,
//    SpeechTarget.DIALOGUE,
    SpeechTarget.BGM,
    SpeechTarget.TAG
)
@Retention(AnnotationRetention.SOURCE)
annotation class SpeechTarget {
    companion object {
        const val ALL = 0

        @Deprecated("A custom TAG already exists")
        const val ASIDE = 1 // Narration

        @Deprecated("A custom TAG already exists")
        const val DIALOGUE = 2 // Dialogue

        const val BGM = 3 // Background music
        const val TAG = 4 // Custom tag

        fun toText(@SpeechTarget target: Int): String {
            return when (target) {
                BGM -> {
                    app.getString(R.string.bgm)
                }

                else -> ""
            }
        }
    }
}