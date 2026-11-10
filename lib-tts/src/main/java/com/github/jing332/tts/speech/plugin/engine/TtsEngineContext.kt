package com.github.jing332.tts.speech.plugin.engine

import android.content.Context
import androidx.annotation.Keep
import com.github.jing332.database.entities.systts.source.PluginTtsSource
import com.github.jing332.script.simple.ext.JsExtensions

/**
 * @param tts Accessed in JS via `ttsrv.tts`
 */
@Keep
data class TtsEngineContext(
    var tts: PluginTtsSource,
    val userVars: Map<String, String> = mutableMapOf(),
    override val context: Context,
    override val engineId: String
) : JsExtensions(context, engineId)