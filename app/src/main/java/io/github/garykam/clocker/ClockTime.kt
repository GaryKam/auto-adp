package io.github.garykam.clocker

import android.content.Context
import androidx.annotation.StringRes

enum class ClockTime(@StringRes private val id: Int) {
    MORNING_OUT(R.string.clock_out_morning),
    MORNING_IN(R.string.clock_in_morning),
    LUNCH_OUT(R.string.clock_out_lunch),
    LUNCH_IN(R.string.clock_in_lunch),
    EVENING_OUT(R.string.clock_out_evening);

    fun getText(context: Context): String {
        return context.getString(id)
    }

    fun getNext(): ClockTime {
        return when (this) {
            MORNING_OUT -> MORNING_IN
            MORNING_IN -> LUNCH_OUT
            LUNCH_OUT -> LUNCH_IN
            LUNCH_IN -> EVENING_OUT
            EVENING_OUT -> MORNING_OUT
        }
    }
}