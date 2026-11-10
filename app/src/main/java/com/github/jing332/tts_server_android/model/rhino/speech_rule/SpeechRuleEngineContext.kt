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
     * algorithm – the tokenization algorithm; either its Chinese or English name can be passed. Options:
     * 维特比 (viterbi): the best balance of efficiency and quality
     * 双数组trie树 (dat): extremely fast dictionary-based tokenization, tens of millions of characters per second
     * 条件随机场 (crf): high accuracy for tokenization, part-of-speech tagging, and named entity recognition; suited to demanding NLP tasks
     * 感知机 (perceptron): tokenization, part-of-speech tagging, and named entity recognition, with support for online learning
     * N最短路 (nshort): slightly better named entity recognition, at the cost of speed
     * Returns:
     * A tokenizer
     */
    @JvmOverloads
    @ScriptInterface
    fun newSegment(algorithm: String = "viterbi"): Segment = HanLP.newSegment(algorithm)!!
}