package io.github.garykam.clocker

import android.content.Context
import androidx.annotation.StringRes

enum class ClockOption(@StringRes private val id: Int) {
    MORNING_OUT(R.string.clock_out_morning),
    MORNING_IN(R.string.clock_in_morning),
    LUNCH_OUT(R.string.clock_out_lunch),
    LUNCH_IN(R.string.clock_in_lunch),
    EVENING_OUT(R.string.clock_out_evening);

    fun getText(context: Context): String {
        return context.getString(id)
    }

    fun getNext(): ClockOption {
        val values = enumValues<ClockOption>()
        val nextOrdinal = (ordinal + 1) % values.size
        return values[nextOrdinal]
    }
}