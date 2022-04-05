package app.i.cdms.data.model


// 以折扣方式的计费表
data class DiscountZone(
    val zone: String,
    val discount: Float,
    val add: Float
)

// 带有渠道区间的阶梯计费表
data class ProfitZone(
    val zone: String,
    val blocks: List<ProfitBlock>
)

// 阶梯计费表中单个阶梯块，一个块一个价
data class ProfitBlock(
    val weight: Int,
    val price: Float
)

// 重量范围计费，按重量区间分别计算首重续重
data class ProfitRange(
    val start: Int,
    val end: Int,
    val first: Float,
    val add: Float
)