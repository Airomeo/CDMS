package app.i.cdms.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.databinding.FragmentTeamAgentItemBinding


/**
 * [ListAdapter] that can display a [Agent].
 */
class MyTeamRecyclerViewAdapter :
    ListAdapter<Agent, MyTeamRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTeamAgentItemBinding.inflate(
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
                it.findNavController().navigate(R.id.navigation_agent)
            }
            tvId.text = item.userId.toString()
            tvUsername.text = item.userName
            tvBalance.text = root.context.getString(
                R.string.my_team_agent_balance, item.accountBalance.toString()
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Agent>() {
            override fun areItemsTheSame(oldItem: Agent, newItem: Agent): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Agent, newItem: Agent): Boolean {
                return oldItem.userId == newItem.userId
            }
        }
    }

    inner class ViewHolder(val binding: FragmentTeamAgentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}