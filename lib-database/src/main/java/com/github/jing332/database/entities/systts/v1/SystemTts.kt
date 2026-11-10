package com.github.jing332.database.entities.systts.v1

import android.os.Parcelable
import androidx.room.*
import com.github.jing332.database.entities.AbstractListGroup
import com.github.jing332.database.entities.systts.SpeechRuleInfo
import com.github.jing332.database.entities.systts.v1.tts.ITextToSpeechEngine
import com.github.jing332.database.entities.systts.v1.tts.LocalTTS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
@kotlinx.parcelize.Parcelize
@TypeConverters(SystemTts.Converters::class)
@Entity(tableName = "sysTts")
data class SystemTts(
    @PrimaryKey(autoGenerate = true)
    val id: Long = System.currentTimeMillis(),

    // ID of the group it belongs to
    @ColumnInfo(defaultValue = AbstractListGroup.DEFAULT_GROUP_ID.toString())
    var groupId: Long = AbstractListGroup.DEFAULT_GROUP_ID,

    var displayName: String? = null,

    var isEnabled: Boolean = false,

    @Embedded("speechRule_")
    var speechRule: SpeechRuleInfo = SpeechRuleInfo(),

    var tts: ITextToSpeechEngine,

    // Index, used for sorting
    @ColumnInfo(defaultValue = "0")
    var order: Int = 0,
) : Parcelable {
    // Speech target
    @Deprecated("")
     @SerialName("readAloudTarget")
    @get:Ignore
    var speechTarget: Int
        get() = speechRule.target
        set(value) {
            speechRule.target = value
        }

    @Deprecated("")

    @SerialName("isStandby")
    @get:Ignore
    var isStandby: Boolean
        get() = speechRule.isStandby
        set(value) {
            speechRule.isStandby = value
        }

    // Converter
    class Converters {
        companion object {
            @OptIn(ExperimentalSerializationApi::class)
            val json by lazy {
                Json {
                    ignoreUnknownKeys = true // Ignore unknown keys
                    explicitNulls = false // Ignore fields that are null
                    allowStructuredMapKeys = true
                }
            }

            private inline fun <reified T> decodeFromString(s: String?): T? {
                if (s == null) return null
                return try {
                    json.decodeFromString<T>(s.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        @TypeConverter
        fun ttsToString(tts: ITextToSpeechEngine): String {
            return json.encodeToString(tts)
        }

        @TypeConverter
        fun stringToTts(json: String?): ITextToSpeechEngine {
            return decodeFromString<ITextToSpeechEngine>(json).run { LocalTTS() }
        }
    }
}