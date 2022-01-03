package app.i.cdms.ui.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.R
import app.i.cdms.data.model.Channel
import app.i.cdms.databinding.ItemChannelBinding


/**
 * [ListAdapter] that can display a [Channel].
 */
class ChannelRecyclerViewAdapter :
    ListAdapter<Channel, ChannelRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

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
                Toast.makeText(
                    root.context,
                    "ToDo",
                    Toast.LENGTH_SHORT
                ).show()
            }
            channelName.text = channel.channelName
            firstPrice.text = root.context.getString(R.string.price, channel.firstPrice)
            addPrice.text = root.context.getString(R.string.price, channel.addPrice)
            limitAddPrice.text = root.context.getString(R.string.price, channel.limitAddPrice)
            firstWeight.text = root.context.getString(R.string.weight, channel.firstWeight)
            limitWeight.text =
                root.context.getString(R.string.weight, channel.limitWeight.toString())
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem.userId == newItem.userId
            }
        }
    }

    inner class ViewHolder(val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root)
}