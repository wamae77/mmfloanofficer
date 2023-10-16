package com.simplemobiletools.calendar.pro.models

@Keep
data class DayYearly(var eventColors: HashSet<Int> = HashSet()) {
    fun addColor(color: Int) = eventColors.add(color)
}
