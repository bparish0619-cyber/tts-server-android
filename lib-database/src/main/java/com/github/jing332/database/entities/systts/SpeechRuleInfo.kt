package com.github.jing332.database.entities.systts

import android.os.Parcelable
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.jing332.database.constants.SpeechTarget
import com.github.jing332.database.entities.MapConverters
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@TypeConverters(MapConverters::class)
data class SpeechRuleInfo(
    var target: Int = SpeechTarget.ALL,

    var isStandby: Boolean = false,
    var specifiedStandbyId: Long? = null,

    var tag: String = "",
    var tagRuleId: String = "",

    // Tag name shown in the top-right corner of the list
    var tagName: String = "",

    // Data used to store the tag
    // Example: key=role, value=Zhang San
    var tagData: Map<String, String> = mutableMapOf(),

    // Used to identify the uniqueness of a TTS configuration; after script processing, the tag and id are returned to the program to locate the speech entry
    var configId: Long = 0L
) : Parcelable {
    val mutableTagData: MutableMap<String, String>
        get() = tagData as MutableMap<String, String>


    /**
     * Determine whether the tags are the same
     * @return true if they are the same
     */
    fun isTagSame(rule: SpeechRuleInfo): Boolean {
        return tag == rule.tag && tagRuleId == rule.tagRuleId
    }

    fun resetTag() {
        tag = ""
        tagRuleId = ""
        tagName = ""
        mutableTagData.clear()
    }

    fun isTagDataEmpty(): Boolean = tagData.filterValues { it.isNotEmpty() }.isEmpty()

}