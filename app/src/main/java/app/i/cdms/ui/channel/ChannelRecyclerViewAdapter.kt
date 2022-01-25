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
import app.i.cdms.databinding.ItemChannelPreviewToDetailBinding


/**
 * [ListAdapter] that can display a [Channel].
 */
class ChannelRecyclerViewAdapter :
    ListAdapter<ChannelDetail, ChannelRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChannelPreviewToDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = getItem(position)
        with(holder.binding) {
            channelName.text = channel.channelName
            customerType.text = channel.customerChannel?.customerType
            when (channel.calcFeeType) {
                "profit" -> {
                    discount.visibility = View.INVISIBLE
                    perAdd.visibility = View.INVISIBLE
                    firstPrice.text =
                        root.context.getString(R.string.price, channel.costPrice!!.first)
                    addPrice.text =
                        root.context.getString(R.string.price, channel.costPrice.blocks.first().add)
                    firstWeight.text =
                        root.context.getString(
                            R.string.weight,
                            channel.costPrice.blocks.first().start
                        )
                    limitWeight.text =
                        root.context.getString(
                            R.string.weight,
                            channel.costPrice.blocks.first().end
                        )
                }
                "discount" -> {
                    discount.visibility = View.VISIBLE
                    perAdd.visibility = View.VISIBLE
                    val percent = channel.discountPercent?.toFloat()?.times(100)
                    firstPrice.text = ""
                    addPrice.text = ""
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

    inner class ViewHolder(val binding: ItemChannelPreviewToDetailBinding) :
        RecyclerView.ViewHolder(binding.root)
}