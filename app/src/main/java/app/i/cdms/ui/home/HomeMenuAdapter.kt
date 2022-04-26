package app.i.cdms.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.databinding.ItemHomeMenuBinding


/**
 * [ListAdapter] that can display a [HomeMenuItem].
 */
class HomeMenuAdapter :
    ListAdapter<HomeMenuItem, HomeMenuAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                item.block.invoke()
            }
            icon.setImageResource(item.iconRes)
            title.setText(item.titleRes)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeMenuItem>() {
            override fun areItemsTheSame(
                oldItem: HomeMenuItem,
                newItem: HomeMenuItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeMenuItem,
                newItem: HomeMenuItem
            ): Boolean {
                return oldItem.titleRes == newItem.titleRes
            }
        }
    }

    inner class ViewHolder(val binding: ItemHomeMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

}