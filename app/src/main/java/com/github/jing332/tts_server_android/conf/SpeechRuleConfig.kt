package com.github.jing332.tts_server_android.conf

import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.tts_server_android.app

object SpeechRuleConfig {
    private val pref = DataSaverPreferences(app.getSharedPreferences("speech_rule", 0))

    var textParam = mutableDataSaverStateOf(pref, key = "textParam", "This is an Android system TTS app with a built-in Microsoft demo interface. It supports custom HTTP requests, importing other local TTS engines, and simple narration/dialogue recognition and reading based on Chinese double quotation marks, plus auto retry, backup configurations, text replacement, and more.")
}