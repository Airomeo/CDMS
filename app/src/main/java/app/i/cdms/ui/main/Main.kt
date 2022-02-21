package app.i.cdms.ui.main

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Main

fun main() {

    val startFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val endFormatter = DateTimeFormatter.ofPattern("-HH:mm")
    var time = LocalDateTime.now()
    var startHour = if (time.hour < 9) 9 else time.hour
    for (d in 1..3) {
        for (i in startHour..20) {
            val start = time.withHour(i).withMinute(0).format(startFormatter)
            val end = time.withHour(i + 1).withMinute(0).format(endFormatter)
            println("$start$end")
        }
        time = time.plusDays(1)
        startHour = 9
    }

    val str = "2022-02-22 10:00-11:00"
    val dateStr = str.substring(0, 11)
    val startTimeStr = str.substring(11, 16) + ":00"
    val endTimeStr = str.substring(17, 22) + ":00"
    val pickUpStartTime = dateStr + startTimeStr
    val pickUpEndTime = dateStr + endTimeStr
    println(pickUpStartTime)
    println(pickUpEndTime)
}