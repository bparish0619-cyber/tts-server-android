package com.github.jing332.tts_server_android.model.rhino.speech_rule

import android.content.Context
import com.github.jing332.script.annotation.ScriptInterface
import com.github.jing332.script.simple.ext.JsExtensions
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.seg.Segment

class SpeechRuleEngineContext(
    override val context: Context, override val engineId: String = ""
) : JsExtensions(context, engineId) {

    /**
     * Create a tokenizer. This is a factory method.
     * Params:
     * algorithm - the tokenization algorithm; pass its English name. This fork
     * documents the English identifiers only, so scripts should use these values:
     * viterbi: the best balance of efficiency and quality
     * dat: extremely fast dictionary-based tokenization, tens of millions of characters per second
     * crf: high accuracy for tokenization, part-of-speech tagging, and named entity recognition; suited to demanding NLP tasks
     * perceptron: tokenization, part-of-speech tagging, and named entity recognition, with support for online learning
     * nshort: slightly better named entity recognition, at the cost of speed
     * Returns:
     * A tokenizer
     */
    @JvmOverloads
    @ScriptInterface
    fun newSegment(algorithm: String = "viterbi"): Segment = HanLP.newSegment(algorithm)!!
}