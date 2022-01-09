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
    private const val API = "https://www.yida178.cn/prod-api"
    const val API_GET_CAPTCHA = "$API/captchaImage"
    const val API_LOGIN = "$API/login"
    const val API_MY_INFO = "$API/wl/home/myInfo"
    const val API_ADD_CHILD = "$API/userAccountAudit"
    const val API_MY_TEAM = "$API/wl/home/myTeamNew"
    const val API_CLEAR_ACCOUNT = "$API/wl/home/clearAccount"
    const val API_RECORD_LIST = "$API/wl/home/recordList"
    const val API_ADD_PRICE = "$API/wl/home/addPrice"
    const val API_EDIT_PRICE = "$API/wl/home/editPrice"
    const val API_REMOVE_PRICE = "$API/wl/home/removePrice"
    const val API_TRANSFER = "$API/wl/userAccount/transfer"
    const val API_NOTICE = "$API/system/notice/list"
    const val API_ORDER_COUNT = "$API/wl/home/getUserOrderCount"
    const val API_GET_CHILDREN_PRICE_BY_CHANNEL =
        "$API/logistics/userChannelPrice/getChildrenPriceByChannel"
    const val API_CUSTOMER_CHANNEL = "$API/wl/home/selfCustomer"
    const val API_CUSTOMER_CHANNEL_DETAIL = "$API/wl/home/selfCustomerDetail"

    private const val API_TCB_BASE =
        "https://i-7g9v864y639e8b0a-1256871713.ap-shanghai.app.tcloudbase.com/express"
    val API_UPDATE_CHANNEL_BY_USERNAME = "$API_SCF_BASE/YIDA/updateChannelByUsername"
    val API_UPDATE_CHANNEL_BY_USER_ID = "$API_SCF_BASE/YIDA/updateChannelByUserId"
    val API_BATCH_UPDATE_CHANNEL = "$API_SCF_BASE/YIDA/batchUpdateChannel"
    const val API_DOC = "https://docs.qq.com/doc/DWmVtWWFJQ2R1cGVU?tdsourcetag=scan-qr"

    private val API_SCF_BASE: String
        get() = "https://service-icw3n5t3-1256871713.sh.apigw.tencentcs.com/" + when (BuildConfig.BUILD_TYPE) {
            "debug" -> "test"
            "staging" -> "prepub"
            "release" -> "release"
            else -> "release"
        }

}