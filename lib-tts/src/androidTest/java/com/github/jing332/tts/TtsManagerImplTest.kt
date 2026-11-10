package com.github.jing332.tts

import android.content.Context
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.jing332.tts.synthesizer.SynthesisCallback
import com.github.jing332.tts.synthesizer.SystemParams
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TtsManagerImplTest {
    // get context
    fun context(): Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    private val text = """
        The Simple Logging Facade for Java (SLF4J) serves as a simple facade or abstraction for various logging frameworks, implemented with the facade pattern. It lets you decide at deployment time which logging framework to use; the main supported frameworks are the Java Logging API, log4j, and logback.
    """.trimIndent()

    @Test
    fun test() = runBlocking {
        val context = context()
        var audioTrack: AudioTrack? = null


        val manager = MixSynthesizer.global
        manager.init()

        manager.synthesize(
            params = SystemParams(text = text, speed = 1f, pitch = 1f),
            callback = object : SynthesisCallback {
                override fun onSynthesizeStart(sampleRate: Int) {
                    audioTrack = AudioTrack.Builder()
                        .setAudioFormat(
                            AudioFormat.Builder().setSampleRate(sampleRate)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build()
                        )
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .setBufferSizeInBytes(
                            AudioTrack.getMinBufferSize(
                                sampleRate,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT
                            )
                        )
                        .build()

                    if (audioTrack?.playState != AudioTrack.PLAYSTATE_PLAYING) {
                        audioTrack?.play()
                    }
                }


                override fun onSynthesizeAvailable(audio: ByteArray) {
//                    println("onSynthesizeAvailable: ${audio.size}")
                    audioTrack!!.write(audio, 0, audio.size)

//                        val maxBufferSize: Int = 1024
//                        var offset = 0
//                        while (offset < audio.size) {
//                            val bytesToWrite = maxBufferSize.coerceAtMost(audio.size - offset)
//                            audioTrack!!.write(audio, offset, bytesToWrite)
//                            offset += bytesToWrite
//                        }

                }

            })
    }
}
