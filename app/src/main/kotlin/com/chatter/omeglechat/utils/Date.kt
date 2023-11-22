package com.chatter.omeglechat.utils

import java.util.Date

fun Date.dateTwelveFormat(): String {
    val amPm = if (this.hours >= 12) "PM" else "AM"
    val hours = if (hours > 12) hours - 12 else hours
    return ("$hours:$minutes $amPm")
}