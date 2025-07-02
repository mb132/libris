package de.hs_kl.libris.util

import android.content.Context
import de.hs_kl.libris.R
import de.hs_kl.libris.data.model.ReadingStatus

// not sure if this is the right package
// could possibly be de.hs_kl.libris.data.model.ReadingStatus

fun ReadingStatus.getDisplayName(context: Context): String {
    return when (this) {
        ReadingStatus.IN_PROGRESS -> context.getString(R.string.reading_status_in_progress)
        ReadingStatus.NOT_STARTED -> context.getString(R.string.reading_status_not_started)
        ReadingStatus.COMPLETED -> context.getString(R.string.reading_status_completed)
        ReadingStatus.ON_HOLD -> context.getString(R.string.reading_status_on_hold)
        ReadingStatus.DROPPED -> context.getString(R.string.reading_status_dropped)
    }
}