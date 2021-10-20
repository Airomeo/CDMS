//package app.i.cdms.data.model
//
//import android.os.Parcelable
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import kotlinx.parcelize.Parcelize
//import java.util.*
//
///**
// * @author ZZY
// * 2/10/21.
// */
//@Entity(tableName = "to_do_table")
//data class ToDo(
//    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    @ColumnInfo(name = "toDo") val toDo: String = UUID.randomUUID().toString(),
//    @ColumnInfo(name = "details") val details: String? = "Details",
//    @ColumnInfo(name = "deadline") val deadline: Long? = null,
//    @ColumnInfo(name = "tag") val tag: String? = null,
//    @ColumnInfo(name = "repeat") val repeat: Repeat = Repeat.NONE,
//    @ColumnInfo(name = "completed") val completed: Boolean = false,
//    @ColumnInfo(name = "priority") val priority: Priority = Priority.NONE,
//    @ColumnInfo(name = "deleted") val deleted: Boolean = false,
//    @ColumnInfo(name = "user") val user: String = "local",
//    @ColumnInfo(name = "list") val list: String = "Default",
//    @ColumnInfo(name = "uuid") val uuid: String = UUID.randomUUID().toString()
//) : Parcelable
//
//enum class Repeat {
//    NONE,//无重复
//    DAILY,//每天
//    WEEKLY,//每周某天
//    WEEKDAY,//每周五个工作日
//    WEEKEND,//每周周末两天
//    MONTHLY,//每月某天
//    YEARLY,//每年某天
//    YEARLY_LUNAR,//农历每年某天
//    CUSTOM,//自定义
////    "1 * * * *"
//}
//
//enum class Priority {
//    HIGH, MEDIUM, LOW, NONE
//}