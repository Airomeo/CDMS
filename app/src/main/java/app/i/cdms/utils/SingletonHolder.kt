package app.i.cdms.utils

/**
 * @author ZZY
 * 2021/11/29.
 * url: https://juejin.cn/post/6844904196500619277
 */
open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

//    fun getInstance(arg: A): T {
//        val i = instance
//        if (i != null) {
//            return i
//        }
//
//        return synchronized(this) {
//            val i2 = instance
//            if (i2 != null) {
//                i2
//            } else {
//                val created = creator!!(arg)
//                instance = created
//                creator = null
//                created
//            }
//        }
//    }

    //对上述方法的一种更简洁的写法
    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: creator!!(arg).apply {
                instance = this
            }
        }

}