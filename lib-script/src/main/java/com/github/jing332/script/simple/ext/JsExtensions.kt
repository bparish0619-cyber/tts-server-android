package com.github.jing332.script.simple.ext

import android.content.Context
import cn.hutool.core.lang.UUID
import com.github.jing332.common.audio.AudioDecoder
import com.github.jing332.common.utils.FileUtils
import com.github.jing332.script.annotation.ScriptInterface
import java.io.File
import java.io.InputStream

@Suppress("unused")
open class JsExtensions(open val context: Context, open val engineId: String = "") :
    JsNet(engineId),
    JsCrypto,
    JsUserInterface {

    @Suppress("MemberVisibilityCanBePrivate")
    @ScriptInterface
    fun getAudioSampleRate(audio: ByteArray): Int {
        return AudioDecoder.getSampleRateAndMime(audio).first
    }

    @ScriptInterface
    fun getAudioSampleRate(ins: InputStream): Int {
        return getAudioSampleRate(ins.readBytes())
    }

    /* Convert String to ByteArray */
    @ScriptInterface
    fun strToBytes(str: String): ByteArray {
        return str.toByteArray(charset("UTF-8"))
    }

    @ScriptInterface
    fun strToBytes(str: String, charset: String): ByteArray {
        return str.toByteArray(charset(charset))
    }

    @ScriptInterface
            /* Convert ByteArray to String */
    fun bytesToStr(bytes: ByteArray): String {
        return String(bytes, charset("UTF-8"))
    }

    @ScriptInterface
    fun bytesToStr(bytes: ByteArray, charset: String): String {
        return String(bytes, charset(charset))
    }

    //**************** File operations ******************//
    /**
     * Get a local file
     * @param path Relative path
     * @return File
     */
    @ScriptInterface
    fun getFile(path: String): File {
        val cachePath = "${context.externalCacheDir!!.absolutePath}/${engineId}"
        if (!FileUtils.exists(cachePath)) File(cachePath).mkdirs()
        val aPath = if (path.startsWith(File.separator)) {
            cachePath + path
        } else {
            cachePath + File.separator + path
        }
        return File(aPath)
    }

    /**
     * Read a file as bytes
     */
    @ScriptInterface
    fun readFile(path: String): ByteArray? {
        val file = getFile(path)
        if (file.exists()) {
            return file.readBytes()
        }
        return null
    }

    /**
     * Read a text file
     */
    @ScriptInterface
    fun readTxtFile(path: String): String {
        val file = getFile(path)
        if (file.exists()) {
            return String(file.readBytes(), charset(charsetDetect(file)))
        }
        return ""
    }

    /**
     * Get the file encoding
     */
    @ScriptInterface
    fun charsetDetect(f: File): String = FileUtils.getFileCharsetSimple(f)

    @ScriptInterface
    fun readTxtFile(path: String, charsetName: String): String {
        val file = getFile(path)
        if (file.exists()) {
            return String(file.readBytes(), charset(charsetName))
        }
        return ""
    }

    @JvmOverloads
    @ScriptInterface
    fun writeTxtFile(path: String, text: String, charset: String = "UTF-8") {
        getFile(path).writeText(text, charset(charset))
    }

    @ScriptInterface
    fun fileExist(path: String): Boolean {
        return FileUtils.exists(getFile(path))
    }

    /**
     * Delete a local file
     * @return Whether the operation succeeded
     */
    @ScriptInterface
    fun deleteFile(path: String): Boolean {
        val file = getFile(path)
        return file.delete()
    }

    @ScriptInterface
    fun randomUUID(): String = UUID.randomUUID().toString()
}