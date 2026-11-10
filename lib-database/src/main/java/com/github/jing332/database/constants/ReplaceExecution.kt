package com.github.jing332.database.constants

import androidx.annotation.IntDef
import com.github.jing332.database.constants.ReplaceExecution.ReplaceExecution.AFTER
import com.github.jing332.database.constants.ReplaceExecution.ReplaceExecution.BEFORE

/**
 * When a replacement rule is executed
 */
@IntDef(BEFORE, AFTER)
@Retention(AnnotationRetention.SOURCE)
annotation class ReplaceExecution {
    companion object ReplaceExecution {
        const val BEFORE = 0 // Before the speech rules
        const val AFTER = 1 // After the speech rules
    }
}
