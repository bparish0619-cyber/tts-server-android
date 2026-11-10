package com.github.jing332.database.entities.systts.v1.tts

import android.os.Parcelable
import com.github.jing332.database.entities.systts.AudioParams
import com.github.jing332.database.entities.systts.BasicAudioFormat
import com.github.jing332.database.entities.systts.SpeechRuleInfo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import java.io.ByteArrayInputStream
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
@Parcelize
@Serializable
@JsonClassDiscriminator("#type")
sealed class ITextToSpeechEngine(
) : Parcelable {
    companion object {
        const val VALUE_FOLLOW_SYSTEM = 0
    }

    abstract var pitch: Int
    abstract var volume: Int
    abstract val rate: Int

    abstract var locale: String

    abstract var speechRule: SpeechRuleInfo
    abstract var audioFormat: BasicAudioFormat
    abstract var audioPlayer: PlayerParams
    abstract var audioParams: AudioParams


    /**
     * Whether the speech rate follows the system
     */
    open fun isRateFollowSystem(): Boolean = rate == VALUE_FOLLOW_SYSTEM

    /**
     * Whether the pitch follows the system
     */
    open fun isPitchFollowSystem(): Boolean = pitch == VALUE_FOLLOW_SYSTEM

    /**
     * Type shown in the bottom-right corner of the UI
     */
    abstract fun getType(): String

    /**
     * Format shown at the bottom of the UI
     */
    open fun getBottomContent(): String {
        return audioFormat.toString()
    }

    /**
     * Description shown below the display name in the UI, such as volume and speech rate
     */
    open fun getDescription(): String = ""

    open fun onLoad() {}

    open fun onStop() {}

    open fun onDestroy() {}

    /**
     * Whether to play directly
     */
    open fun isDirectPlay(): Boolean = false

    protected open suspend fun startPlay(text: String, rate: Int = 0, pitch: Int = 0): Boolean =
        false

    /**
     * Play audio; the parameters automatically determine whether to follow the system
     */
    open suspend fun startPlayWithSystemParams(
        text: String,
        sysRate: Int = 0,
        sysPitch: Int = 0
    ): Boolean {
        val r = if (isRateFollowSystem()) sysRate else this.rate
        val p = if (isPitchFollowSystem()) sysPitch else this.pitch
        return startPlay(text, r, p)
    }

    open suspend fun getAudio(
        speakText: String,
        rate: Int = 50,
        pitch: Int = 0
    ): InputStream? = null

    suspend fun getAudioWithSystemParams(
        text: String,
        sysRate: Int = 50,
        sysPitch: Int = 0
    ): InputStream? {

        val r = if (isRateFollowSystem()) sysRate else this.rate
        val p = if (isPitchFollowSystem()) sysPitch else this.pitch
        return getAudio(text, r, p)
    }

//    open suspend fun getAudioStream(text: String, rate: Int, pitch: Int): InputStream? = null
//
//    suspend fun getAudioStreamSysParams(
//        text: String,
//        sysRate: Int = 50,
//        sysPitch: Int = 0
//    ): InputStream? {
//        val r = if (isRateFollowSystem()) sysRate else this.rate
//        val p = if (isPitchFollowSystem()) sysPitch else this.pitch
//        return getAudioStream(text, r, p)
//    }

    /**
     * Get the PCM audio stream
     */
    @Deprecated("Not used for now")
    open suspend fun getAudioStream(
        speakText: String,
        chunkSize: Int = 0,
        onData: (ByteArray?) -> Unit
    ) {
    }


    protected fun ByteArray?.toStream(): ByteArrayInputStream? {
        return if (this == null) null else ByteArrayInputStream(this)
    }
}