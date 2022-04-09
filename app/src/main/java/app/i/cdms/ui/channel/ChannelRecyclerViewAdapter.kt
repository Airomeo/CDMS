package app.i.cdms.ui.channel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.R
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.DiscountZone
import app.i.cdms.data.model.ProfitBlock
import app.i.cdms.databinding.ItemChannelBinding
import kotlin.math.roundToInt


/**
 * [ListAdapter] that can display a [Channel].
 */
class ChannelRecyclerViewAdapter(
    private val rootOnClickCallback: (channel: Channel) -> Unit
) : ListAdapter<Channel, ChannelRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChannelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                rootOnClickCallback.invoke(channel)
            }
            actionDown.setOnClickListener {
                it.animate().rotation(it.rotation + 180F)
                if (it.rotation % 360F > 0) {
                    channel.unfold = false
                    lightGoods.visibility = View.GONE
                    backFeeType.visibility = View.GONE
                    priority.visibility = View.GONE
                    areaType.visibility = View.GONE
                } else {
                    channel.unfold = true
                    lightGoods.visibility = View.VISIBLE
                    backFeeType.visibility = View.VISIBLE
                    priority.visibility = View.VISIBLE
                    areaType.visibility = View.VISIBLE
                }
            }
            channelName.text = channel.channelName
            customerType.text = when (channel.customerType) {
                "kd" -> "快递"
                "ky" -> "快运"
                "poizon" -> "得物"
                else -> "其他"
            }
            if (channel.unfold) {
                actionDown.rotation = 180F
                lightGoods.visibility = View.VISIBLE
                backFeeType.visibility = View.VISIBLE
                priority.visibility = View.VISIBLE
                areaType.visibility = View.VISIBLE
            } else {
                actionDown.rotation = 0F
                lightGoods.visibility = View.GONE
                backFeeType.visibility = View.GONE
                priority.visibility = View.GONE
                areaType.visibility = View.GONE
            }
            when (channel.calcFeeType) {
                "profit" -> {
                    val blocks = channel.tariffZone as List<ProfitBlock>
                    discount.text = null
                    perAdd.text = null
                    when (blocks.size) {
                        1 -> {
                            // 1个重量计费区间 0-1
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = null
                            price2.text = null
                            price3.text = null
                            price4.text = null
                            block1.visibility = View.GONE
                            block2.visibility = View.GONE
                            block3.visibility = View.GONE
                            block4.visibility = View.GONE
                            weight1.text = null
                            weight2.text = null
                            weight3.text = null
                            weight4.text = null
                        }
                        2 -> {
                            // 2个重量计费区间 0-1-30
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[1].price)
                            )
                            price2.text = null
                            price3.text = null
                            price4.text = null
                            block1.visibility = View.VISIBLE
                            block2.visibility = View.GONE
                            block3.visibility = View.GONE
                            block4.visibility = View.GONE
                            weight1.text =
                                root.context.getString(R.string.weight, blocks[0].weight.toString())
                            weight2.text = null
                            weight3.text = null
                            weight4.text = null
                        }
                        3 -> {
                            // 3个重量计费区间 0-1-3-30
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[1].price)
                            )
                            price2.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[2].price)
                            )
                            price3.text = null
                            price4.text = null
                            block1.visibility = View.VISIBLE
                            block2.visibility = View.VISIBLE
                            block3.visibility = View.GONE
                            block4.visibility = View.GONE
                            weight1.text =
                                root.context.getString(R.string.weight, blocks[0].weight.toString())
                            weight2.text =
                                root.context.getString(R.string.weight, blocks[1].weight.toString())
                            weight3.text = null
                            weight4.text = null
                        }
                        4 -> {
                            // 4个重量计费区间 0-1-2-3-30
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[1].price)
                            )
                            price2.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[2].price)
                            )
                            price3.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[3].price)
                            )
                            price4.text = null
                            block1.visibility = View.VISIBLE
                            block2.visibility = View.VISIBLE
                            block3.visibility = View.VISIBLE
                            block4.visibility = View.GONE
                            weight1.text =
                                root.context.getString(R.string.weight, blocks[0].weight.toString())
                            weight2.text =
                                root.context.getString(R.string.weight, blocks[1].weight.toString())
                            weight3.text =
                                root.context.getString(R.string.weight, blocks[2].weight.toString())
                            weight4.text = null
                        }
                        5 -> {
                            // 5个重量计费区间 0-1-2-3-4-50
                            // 极兔22P艾悦-外部渠道2区",:"1-1公斤,价格6.4续0;\n2-3公斤,价格8.9续0;\n4-50公斤,价格11.3续2.5;"
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[1].price)
                            )
                            price2.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[2].price)
                            )
                            price3.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[3].price)
                            )
                            price4.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[4].price)
                            )
                            block1.visibility = View.VISIBLE
                            block2.visibility = View.VISIBLE
                            block3.visibility = View.VISIBLE
                            block4.visibility = View.VISIBLE
                            weight1.text =
                                root.context.getString(R.string.weight, blocks[0].weight.toString())
                            weight2.text =
                                root.context.getString(R.string.weight, blocks[1].weight.toString())
                            weight3.text =
                                root.context.getString(R.string.weight, blocks[2].weight.toString())
                            weight4.text =
                                root.context.getString(R.string.weight, blocks[3].weight.toString())
                        }
                        else -> {
                            // else个重量计费区间 else<0 or else>6. 暂时按5个显示页面
                            price.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[0].price)
                            )
                            price1.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[1].price)
                            )
                            price2.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[2].price)
                            )
                            price3.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[3].price)
                            )
                            price4.text = root.context.getString(
                                R.string.price,
                                String.format("%.2f", blocks[4].price)
                            )
                            block1.visibility = View.VISIBLE
                            block2.visibility = View.VISIBLE
                            block3.visibility = View.VISIBLE
                            block4.visibility = View.VISIBLE
                            weight1.text =
                                root.context.getString(R.string.weight, blocks[0].weight.toString())
                            weight2.text =
                                root.context.getString(R.string.weight, blocks[1].weight.toString())
                            weight3.text =
                                root.context.getString(R.string.weight, blocks[2].weight.toString())
                            weight4.text =
                                root.context.getString(R.string.weight, blocks[3].weight.toString())
                        }
                    }
                }
                "discount" -> {
                    val block = channel.tariffZone as DiscountZone
                    val percent = block.discount.times(100).roundToInt().toString()
                    discount.text = root.context.getString(R.string.config_discount, percent)
                    perAdd.text = root.context.getString(
                        R.string.config_per_add, String.format("%.2f", block.add)
                    )
                    price.text = null
                    price1.text = null
                    price2.text = null
                    price3.text = null
                    price4.text = null
                    block1.visibility = View.GONE
                    block2.visibility = View.GONE
                    block3.visibility = View.GONE
                    block4.visibility = View.GONE
                    weight1.text = null
                    weight2.text = null
                    weight3.text = null
                    weight4.text = null
                }
            }
            weight5.text = root.context.getString(
                R.string.weight,
                channel.limitWeight.toFloat().roundToInt().toString()
            )
            lightGoods.text =
                root.context.getString(R.string.channel_light_weight, channel.lightGoods)
            val backFeeStr = when (channel.backFeeType) {
                "whole" -> "全额"
                "half" -> "半价"
                else -> channel.backFeeType
            }
            backFeeType.text = root.context.getString(R.string.channel_back_fee_type, backFeeStr)
//            priority.text =
//                root.context.getString(R.string.channel_priority, channel.priority.toString())
            val areaStr = when (channel.areaType) {
                "P" -> "省"
                "C" -> "城"
                else -> channel.areaType
            }
            areaType.text = root.context.getString(R.string.channel_area_type, areaStr)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem.channelName == newItem.channelName
            }

            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root)
}