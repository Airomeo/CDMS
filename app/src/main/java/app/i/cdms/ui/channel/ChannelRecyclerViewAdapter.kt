package app.i.cdms.ui.channel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.R
import app.i.cdms.data.model.ChannelDetail
import app.i.cdms.databinding.ItemChannelBinding


/**
 * [ListAdapter] that can display a [ChannelDetail].
 */
class ChannelRecyclerViewAdapter :
    ListAdapter<ChannelDetail, ChannelRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

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
            root.setOnClickListener { }
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
            customerType.text = when (channel.customerChannel?.customerType) {
                "personal" -> "个人"
                "business" -> "商家"
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
                    discount.text = null
                    perAdd.text = null
                    firstPrice.text =
                        root.context.getString(R.string.price, channel.price!!.first)
                    addPrice.text =
                        root.context.getString(R.string.price, channel.price.blocks[0].add)
                    view2.visibility = View.VISIBLE
                    firstWeight.text =
                        root.context.getString(R.string.weight, channel.price.blocks[0].start)
                    val blocks = channel.price.blocks
                    when (blocks.size) {
                        1 -> {
                            /*1个重量计费区间 0-1 1-30
                            {
                                "start": "1.0",
                                "end": "60.0",
                                "add": "2.1"
                            }*/
                            addPrice2.text = null
                            addPrice3.text = null
                            view3.visibility = View.GONE
                            view4.visibility = View.GONE
                            hopWeight.text = null
                            hopWeight2.text = null
                        }
                        2 -> {
                            /*2个重量计费区间 0-1 1-3 3-30
                            {
                                "start": "1.0",
                                "end": "3.0",
                                "add": "1.5"
                            },
                            {
                                "start": "4.0",
                                "end": "30.0",
                                "add": "2.0"
                            }*/
                            addPrice2.text = root.context.getString(R.string.price, blocks[1].add)
                            addPrice3.text = null
                            view3.visibility = View.VISIBLE
                            view4.visibility = View.GONE
                            hopWeight.text = root.context.getString(R.string.weight, blocks[0].end)
                            hopWeight2.text = null
                        }
                        else -> {
                            /*3个重量计费区间 0-1 1-2 2-3 3-30
                            {
                                "start": "1.0",
                                "end": "2.0",
                                "add": "1.7"
                            },
                            {
                                "start": "3.0",
                                "end": "3.0",
                                "add": "0.0"
                            },
                            {
                                "start": "4.0",
                                "end": "30.0",
                                "add": "1.8"
                            }*/
                            addPrice2.text = root.context.getString(R.string.price, blocks[1].add)
                            addPrice3.text = root.context.getString(R.string.price, blocks[2].add)
                            view3.visibility = View.VISIBLE
                            view4.visibility = View.VISIBLE
                            hopWeight.text = root.context.getString(R.string.weight, blocks[0].end)
                            hopWeight2.text = root.context.getString(R.string.weight, blocks[1].end)
                        }
                    }
                }
                "discount" -> {
                    val percent = channel.discountPercent?.toFloat()?.times(100).toString()
                    discount.text = root.context.getString(R.string.config_discount, percent)
                    perAdd.text = root.context.getString(R.string.config_per_add, channel.perAdd)
                    firstPrice.text = null
                    addPrice.text = null
                    addPrice2.text = null
                    addPrice3.text = null
                    view2.visibility = View.GONE
                    view3.visibility = View.GONE
                    view4.visibility = View.GONE
                    firstWeight.text = null
                    hopWeight.text = null
                    hopWeight2.text = null
                }
            }
            limitWeight.text =
                root.context.getString(R.string.weight, channel.customerChannel?.limitWeight)
            lightGoods.text = root.context.getString(
                R.string.channel_light_weight,
                channel.customerChannel?.lightGoods
            )
            val backFeeStr = when (channel.customerChannel?.backFeeType) {
                "whole" -> "全额"
                "half" -> "半价"
                else -> channel.customerChannel?.backFeeType
            }
            backFeeType.text = root.context.getString(
                R.string.channel_back_fee_type,
                backFeeStr
            )
            priority.text = root.context.getString(
                R.string.channel_priority,
                channel.customerChannel?.priority.toString()
            )
            val areaStr = when (channel.customerChannel?.areaType) {
                "P" -> {
                    "省"
                }
                "C" -> {
                    "城"
                }
                else -> {
                    channel.customerChannel?.areaType
                }
            }
            areaType.text = root.context.getString(
                R.string.channel_area_type,
                areaStr
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChannelDetail>() {
            override fun areItemsTheSame(oldItem: ChannelDetail, newItem: ChannelDetail): Boolean {
                return oldItem.channelId == newItem.channelId
            }

            override fun areContentsTheSame(
                oldItem: ChannelDetail,
                newItem: ChannelDetail
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root)
}