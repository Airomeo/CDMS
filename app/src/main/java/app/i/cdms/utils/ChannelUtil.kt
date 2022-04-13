package app.i.cdms.utils

import app.i.cdms.data.model.DiscountZone
import app.i.cdms.data.model.ProfitBlock
import app.i.cdms.data.model.ProfitRange
import app.i.cdms.data.model.ProfitZone


object ChannelUtil {
    /**
     * 解析包含多个"xx区:xxx;"的字符串
     * 将"1区:1-2公斤,价格5.6续1.0;\n3-50公斤,价格9.5续1.5;\n2区:1-2公斤,价格5.6续1.0;\n3-50公斤,价格9.5续1.5;"
     * 解析为为List<ProfitZone>
     *
     * @param text: String  原始字符串
     * @return List<ProfitZone>
     */
    fun parseToProfitZone(text: String): List<ProfitZone> {
        val tariffAreaBlocks = mutableListOf<ProfitZone>()
        var blockName: String? = null
        var tariffBlocksText = ""

        text.replace("\n", "")
            .trim()
            .removeSuffix(";")
            .split(":", ";")
            .forEach {
                if (it.contains("区")) {
                    if (blockName != it) {
                        if (blockName != null) {
                            tariffAreaBlocks.add(
                                ProfitZone(blockName!!, parsePrice(tariffBlocksText))
                            )
                        }
                        blockName = it
                        tariffBlocksText = ""
                    }
                } else {
                    tariffBlocksText = "$tariffBlocksText$it;\n"
                }
            }
        // Add last tariffAreaBlocks
        tariffAreaBlocks.add(ProfitZone(blockName!!, parsePrice(tariffBlocksText)))

        return tariffAreaBlocks
    }

    /**
     * 解析不包含"xx区"的字符串
     * 将"1-1公斤,价格5.6续0;\n2-3公斤,价格7.8续0;\n4-50公斤,价格9.5续1.5;"
     * 解析为为List<TariffBlock>
     *
     * @param text: String 单个结构
     * @return List<TariffBlock>
     */
    fun parsePrice(text: String): List<ProfitBlock> {
        val list = text.replace("\n", "")
            .trim()
            .removeSuffix(";")
            .split(":", ";")
        val tariffRanges = list.map {
            parseTextToTariffRange(it)
        }
        return parseTariffRangeToBlock(tariffRanges)
    }

    /**
     * 提取数据，将"官网格式的字符串"转换为我要的"重量范围计费"类型
     * eg: 1-1公斤,价格5.2续0 to 1, 1, 5.2, 0.0;
     * eg: 1-50公斤,价格6续1.5 to 1, 50, 6.0, 1.5;
     * @param text: String
     * @return ProfitRange
     */
    private fun parseTextToTariffRange(text: String): ProfitRange {
        val start = text.substringBefore("公斤").substringBefore("-").toIntOrNull() ?: 0
        val end = text.substringBefore("公斤").substringAfter("-").toIntOrNull() ?: 0
        val first = text.substringAfterLast("价格").substringBefore("续").toFloatOrNull() ?: 0F
        val add = text.substringAfterLast("价格").substringAfter("续").toFloatOrNull() ?: 0F

        return ProfitRange(start, end, first, add)
    }

    /**
     * "1区:折扣0.45,单笔加收0.5\n2区:折扣1,单笔加收0.5" "折扣0.64,单笔加收0"
     * 提取数据，将"上述"转换为"下述"类型
     * eg: 1区 0.45 0.5
     * eg: 2区 1 0.5
     * @param text: String
     * @return List<DiscountZone>
     */
    fun parseToDiscountZone(text: String): List<DiscountZone> {
        return text.trim()
            .removeSuffix(";")
            .split("\n")
            .map {
                val zone = it.substringBefore(":", "")
                val discount = it.substringAfter("折扣").substringBefore(",").toFloatOrNull() ?: 0F
                val add = it.substringAfter("单笔加收").toFloatOrNull() ?: 0F
                DiscountZone(zone, discount, add)
            }
    }

    /**
     * 将重量范围、价格、续重转换为理想的"一个块一个价"类型
     *
     * @param blocks: List<ProfitRange>.
     * @return List<ProfitBlock>
     */
    private fun parseTariffRangeToBlock(blocks: List<ProfitRange>): List<ProfitBlock> {

//    "channelPrices": "1区:1-2公斤,价格5.2续0.6;\n3-5公斤,价格6.9续0;\n6-50公斤,价格8.6续1.8;\n2区:1-1公斤,价格6.4续0;\n2-3公斤,价格8.9续0;\n4-50公斤,价格11.3续2.5;\n3区:1-1公斤,价格8.6续0;\n2-3公斤,价格12.5续0;\n4-50公斤,价格16.5续4;\n4区:1-1公斤,价格18续0;\n2-3公斤,价格26续0;\n4-50公斤,价格34续8;",
//    "channelBlocks":[
//    {
//        "zone":"1区",
//        "blocks":[
//            {
//                "weight":"1",
//                "price":"5.2"
//            },
//            {
//                "weight":"2",
//                "price":"0.6"
//            },
//            {
//                "weight":"3",
//                "price":"1.1"
//            },
//            {
//                "weight":"5",
//                "price":"0"
//            },
//            {
//                "weight":"6",
//                "price":"1.7"
//            },
//            {
//                "weight":"50",
//                "price":"1.8"
//            }
//        ]
//    },

        val list = mutableListOf<ProfitBlock>()
        for (block in blocks) {
            val index = blocks.indexOf(block)
            if (index == 0) {
                list.add(ProfitBlock(blocks[0].start, blocks[0].first))
            } else {
                val preBlock = blocks[index - 1]
                // 之前所有区间的总价
                val sum = preBlock.first + (preBlock.end - preBlock.start) * preBlock.add
                list.add(ProfitBlock(block.start, block.first - sum))
            }
            if (block.start == block.end) {
                // 该区间重量范围就1kg，不添加续重区间
            } else {
                // 该区间重量范围大于1kg，添加续重区间
                list.add(ProfitBlock(block.end, block.add))
            }
        }

        // 如果相邻的两个区间续重相同，化简一下，去除重复
        var i = 0
        while (i < list.size - 1) {
            if (list[i].price == list[i + 1].price) {
                list.removeAt(i)
            } else {
                i++
            }
        }

        return list
    }
}