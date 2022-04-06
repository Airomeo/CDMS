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
    const val PREF_AREA_LIST = "pref_area_list"
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
    const val API_MY_TEAM = "$API/wl/myTeam/list"
    const val API_CLEAR_ACCOUNT = "$API/wl/home/clearAccount"
    const val API_RECORD_LIST = "$API/wl/home/recordList"
    const val API_TRANSFER = "$API/wl/userAccount/transfer"
    const val API_NOTICE = "$API/system/notice/list" // no need token
    const val API_ORDER_COUNT = "$API/wl/home/getUserOrderCount"
    const val API_CUSTOMER_CHANNEL = "$API/price/self"
    const val API_CUSTOMER_CHANNEL_DETAIL = "$API/price/selfDetail"
    const val API_GET_CHILDREN_PRICE_BY_CHANNEL =
        "$API/logistics/userChannelPrice/getChildrenPriceByChannel"
    const val API_BIND_CHANNEL_TO_USER = "$API/logistics/userChannelPrice/bindChannel2Users"
    const val API_UPDATE_CHILD_PRICE = "$API/logistics/userChannelPrice/updateChildPrice"
    const val API_DELETE_CHILD_PRICE = "$API/logistics/userChannelPrice/126/2770"
    const val API_GET_AREA = "$API/logistics/area/getAreaCascaderVo" // no need token
    const val API_GET_PER_ORDER_FEE = "$API/commonOrder/getPreOrderFee"
    const val API_SUBMIT_ORDER = "$API/commonOrder/submitOrder"
    const val API_GET_COMPARE_FEE = "$API/commonOrder/getCompareFee"
    const val API_GET_DELIVERY_ID = "$API/commonOrder/getDeliveryId"
    const val API_FETCH_POST_CODE = "$API/getPostCode"
    const val API_FETCH_INVITE_CODE = "$API/getInviteCode"
    const val API_FETCH_ROUTERS = "$API/getRouters"

    const val API_PARSE_ADDRESS_BY_SF =
        "https://www.sf-express.com/sf-service-owf-web/service/order/batch/orderAddressSplit"
    const val API_PARSE_ADDRESS_BY_JD = "https://www.yunyangwl.com/jdserver2/ParseAddressByJd"

    private const val API_TCB_BASE =
        "https://i-7g9v864y639e8b0a-1256871713.ap-shanghai.app.tcloudbase.com/express"
    const val URL_DOC = "https://docs.qq.com/doc/DWmVtWWFJQ2R1cGVU?tdsourcetag=scan-qr"

    val ignoreTokenList = listOf(
        API_GET_CAPTCHA,
        API_LOGIN,
        API_PARSE_ADDRESS_BY_SF,
        API_PARSE_ADDRESS_BY_JD,
        API_GET_AREA,
        API_NOTICE
    )
    private val API_SCF_BASE: String
        get() = "https://service-icw3n5t3-1256871713.sh.apigw.tencentcs.com/" + when (BuildConfig.BUILD_TYPE) {
            "debug" -> "test"
            "staging" -> "prepub"
            "release" -> "release"
            else -> "release"
        }

}