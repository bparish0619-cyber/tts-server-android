package com.github.jing332.tts_server_android

import android.content.Context
import android.os.Build
import ch.qos.logback.classic.LoggerContext
import com.github.jing332.common.utils.ClipboardUtils
import com.github.jing332.common.utils.longToast
import com.github.jing332.common.utils.runOnUI
import com.github.jing332.tts_server_android.constant.AppConst
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler {
    companion object{
        const val TAG = "CrashHandler"
    }
    private var mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val logger = KotlinLogging.logger(TAG)

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        handleException(e)
        val ctx = LoggerFactory.getILoggerFactory() as LoggerContext
        ctx.stop()

        mDefaultHandler?.uncaughtException(t, e)
    }

    private fun handleException(e: Throwable) {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val brand = Build.BRAND
        val device = Build.DEVICE
        val androidVersion = Build.VERSION.RELEASE
        val modelName = Build.MODEL

        logger.error (e) {
            "$brand / $device / $modelName / $androidVersion" +
                    "\n$versionName ($versionCode)"
        }
        if (BuildConfig.DEBUG) return

        context.longToast("TTS Server crashed. The log is being uploaded and will be copied to the clipboard shortly")
        val log = "\n${LocalDateTime.now()}" +
                "\nVersion code: ${AppConst.appInfo.versionCode}, version name: ${AppConst.appInfo.versionName}\n" +
                "Crash details:\n${e.stackTraceToString()}"

        runOnUI {
            ClipboardUtils.copyText("TTS-Server crash log", log)
            context.longToast("Log copied to the clipboard")
        }
    }
}