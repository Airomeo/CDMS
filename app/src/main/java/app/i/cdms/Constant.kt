package app.i.cdms


/**
 * App Constant
 */
object Constant {

    /**
     * 当前数据库
     */
    const val DB_NAME = "DB"
    const val DB_VERSION = 1

    /**
     * DataStore key
     */
    const val USER_PREFERENCES_NAME = "user_preferences"
    const val PREF_SORT_ORDER = "sort_order"
    const val PREF_SHOW_COMPLETED = "show_completed"
    const val PREF_TOKEN = "pref_language"
    const val PREF_MY_INFO = "pref_my_info"
    const val PREF_SELECTED_THEME = "pref_dark_mode"
    const val PREF_CONFERENCE_TIME_ZONE = "pref_conference_time_zone"
    const val PREF_LANGUAGE = "pref_language"

    /**
     * 密码长度规范
     */
    const val PWD_MIN_LENGTH = 6
    const val PWD_MAX_LENGTH = 16

    /**
     * 性别规范
     */
    const val UNDEFINED_GENDER = 0
    const val MALE = 1
    const val FEMALE = 2

    /**
     * HOST and API
     */
    val API_GET_CAPTCHA = "$API/prod-api/captchaImage"
    val API_LOGIN = "$API/prod-api/login"
    val API_MY_INFO = "$API/prod-api/wl/home/myInfo"
    val API_ADD_CHILD = "$API/prod-api/wl/home/addChild"
    val API_MY_TEAM = "$API/prod-api/wl/home/myTeam"
    private val HOST: String
        get() = when {
//            BuildConfig.PREPARE -> Service.PRE.HOST
            BuildConfig.DEBUG -> Service.DEV.HOST
            else -> Service.PROD.HOST
        }
    private val API: String
        get() = when {
//            BuildConfig.PREPARE -> Service.PRE.API
            BuildConfig.DEBUG -> Service.DEV.API
            else -> Service.PROD.API
        }

    private enum class Service(val HOST: String, val API: String) {
        DEV(
            "https://www.yida178.cn",
            "https://www.yida178.cn"
        ),
        PRE(
            "https://www.yida178.cn",
            "https://www.yida178.cn"
        ),
        PROD(
            "https://www.yida178.cn",
            "https://www.yida178.cn"
        )
    }
}