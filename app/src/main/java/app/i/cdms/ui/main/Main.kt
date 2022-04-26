package app.i.cdms.ui.main

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val _a = MutableStateFlow<List<Int>>(emptyList())
    val a = _a.asStateFlow()
    val list = mutableListOf<Int>()
    runBlocking {
        launch {
            repeat(3) {
                println("add: $it")
                list.add(it)
                _a.value = list.toList()
                delay(1000)
            }
        }
        a.collectLatest { println("collectLatest: $it") }
    }
}

fun PickUpTime() {

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