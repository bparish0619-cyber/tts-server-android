package com.github.jing332.database.entities.replace

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.jing332.database.entities.AbstractListGroup.Companion.DEFAULT_GROUP_ID
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "replaceRule")
data class ReplaceRule(
    @PrimaryKey(autoGenerate = true)
    var id: Long = System.currentTimeMillis(),

    // ID of the group it belongs to
    @ColumnInfo(defaultValue = DEFAULT_GROUP_ID.toString())
    var groupId: Long = DEFAULT_GROUP_ID,

    // Display name
    var name: String = "",
    // Whether it is enabled
    var isEnabled: Boolean = true,
    // Whether it is a regular expression
    var isRegex: Boolean = false,
    // Pattern to match
    var pattern: String = "",
    // Replacement
    var replacement: String = "",
    // Index, used for sorting
    @ColumnInfo(defaultValue = "0")
    var order: Int = 0,

    @ColumnInfo(defaultValue = "")
    var sampleText: String = ""
) : Parcelable