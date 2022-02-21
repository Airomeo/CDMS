package app.i.cdms.ui.channel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.R
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.ChannelDetail
import app.i.cdms.databinding.ItemChannelBinding


/**
 * [ListAdapter] that can display a [Channel].
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
        ).apply {
            setIsRecyclable(false)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = getItem(position)
        with(holder.binding) {
            root.setOnClickListener { }
            actionDown.setOnClickListener {
                it.animate().rotation(it.rotation + 180F)
                if (it.rotation % 360F > 0) {
//                    channel.copy(unfold = false)
//                    channel.unfold = false
                    lightGoods.visibility = View.GONE
                    backFeeType.visibility = View.GONE
                    priority.visibility = View.GONE
                    areaType.visibility = View.GONE
                } else {
//                    channel.copy(unfold = true)
//                    channel.unfold = true
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
            when (channel.calcFeeType) {
                "profit" -> {
                    discount.visibility = View.INVISIBLE
                    perAdd.visibility = View.INVISIBLE
                    firstPrice.text =
                        root.context.getString(R.string.price, channel.price!!.first)
                    addPrice.text =
                        root.context.getString(R.string.price, channel.price.blocks[0].add)
                    firstWeight.text =
                        root.context.getString(R.string.weight, channel.price.blocks[0].start)
                    limitWeight.text =
                        root.context.getString(
                            R.string.weight,
                            channel.customerChannel?.limitWeight
                        )
                    if (channel.price.blocks.size == 2) {
                        view3.visibility = View.VISIBLE
                        addPrice2.visibility = View.VISIBLE
                        hop.visibility = View.VISIBLE
                        addPrice2.text =
                            root.context.getString(R.string.price, channel.price.blocks[1].add)
                        hop.text =
                            root.context.getString(R.string.weight, channel.price.blocks[0].end)
                    }
                }
                "discount" -> {
                    discount.visibility = View.VISIBLE
                    perAdd.visibility = View.VISIBLE
                    val percent = channel.discountPercent?.toFloat()?.times(100)
                    firstPrice.text = null
                    addPrice.text = null
                    discount.text =
                        root.context.getString(R.string.config_discount, percent.toString())
                    perAdd.text =
                        root.context.getString(R.string.config_per_add, channel.perAdd)
                }
            }
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