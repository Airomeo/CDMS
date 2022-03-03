package app.i.cdms.ui.channel

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.databinding.DialogChannelConfigBinding
import app.i.cdms.databinding.FragmentChannelBinding
import app.i.cdms.ui.main.MainViewModel
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!
    private val channelViewModel: ChannelViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChannelBinding.bind(view)

        channelViewModel.getAllChannelDetail()

        val mAdapter = ChannelRecyclerViewAdapter()
        // Set the adapter
        with(binding.list) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                channelViewModel.channelDetailListFlow.collect {
                    mAdapter.submitList(it)
                }
            }
        }
    }

    private fun showUserDialog(
        firstCommission: Float,
        addCommission: Float,
        discountCommission: Float,
        perAddCommission: Float
    ) {
        channelViewModel.getMyTeam(1, 9999, null, null, null, null)
        viewLifecycleOwner.lifecycleScope.launch {
            channelViewModel.myTeam.collectLatest { myTeam ->
                myTeam?.rows ?: return@collectLatest
                // 直系下级
                val myDirectTeam = myTeam.rows.filter { agent ->
                    agent.parentUserId == mainViewModel.myInfo?.userId
                }
                val choicesTextArray = myDirectTeam.map { agent ->
                    agent.userName
                }.toTypedArray()

                // all elements initialized to false.
                val choicesCheckedArray = BooleanArray(choicesTextArray.size)

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.channel_update_config_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_positive_text, null)
                    .setNeutralButton(R.string.channel_update_config_dialog_select_user, null)
                    .setNegativeButton(R.string.dialog_negative_text, null)
                    .setMultiChoiceItems(
                        choicesTextArray,
                        choicesCheckedArray,
                    ) { dialogInterface, position, isChecked ->
                        choicesCheckedArray[position] = isChecked
                    }
                    .setOnDismissListener {
                        this.coroutineContext.job.cancel()
                    }
                    .create().apply {
                        setOnShowListener {
                            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                if (listView.checkedItemCount <= 0) {
                                    EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_user_selected))
                                    return@setOnClickListener
                                }
                                val agents = mutableListOf<Agent>()
                                for (i in choicesCheckedArray.indices) {
                                    if (choicesCheckedArray[i]) {
                                        agents.add(myDirectTeam[i])
                                    }
                                }

                                channelViewModel.updateChannel(
                                    firstCommission,
                                    addCommission,
                                    discountCommission * 0.01F,
                                    perAddCommission,
                                    agents
                                )
                                dismiss()
                            }
                            getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                                for (i in 0 until listView.count) {
                                    choicesCheckedArray[i] = !choicesCheckedArray[i]
                                    listView.setItemChecked(i, choicesCheckedArray[i])
                                }
                            }
                        }
                    }.show()
            }
        }
    }

    private fun showConfigDialog() {
        val b = DialogChannelConfigBinding.inflate(layoutInflater, null, false)
        b.sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
            b.tvFirstCommission.text =
                getString(R.string.config_first_commission, value.toString())
        }
        b.sldAddCommission.addOnChangeListener { slider, value, fromUser ->
            b.tvAddCommission.text =
                getString(R.string.config_add_commission, value.toString())
        }
        b.sldDiscount.addOnChangeListener { slider, value, fromUser ->
            b.tvDiscount.text = getString(R.string.config_discount_commission, value.toString())
        }
        b.sldPerAdd.addOnChangeListener { slider, value, fromUser ->
            b.tvPerAdd.text =
                getString(R.string.config_per_add_commission, value.toString())
        }
        b.sldFirstCommission.value = 0.5F
        b.sldAddCommission.value = 0.2F
        b.sldDiscount.value = 5F
        b.sldPerAdd.value = 0.2F

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.channel_config_commission_title)
            .setCancelable(false)
            .setMessage(R.string.agent_dialog_update_channel_message)
            .setView(b.root)
            .setPositiveButton(R.string.dialog_next) { dialog, which ->
                showUserDialog(
                    b.sldFirstCommission.value,
                    b.sldAddCommission.value,
                    b.sldDiscount.value,
                    b.sldPerAdd.value
                )
            }
            .setNeutralButton(R.string.channel_config_commission_default, null)
            .setNegativeButton(R.string.dialog_negative_text, null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        b.sldFirstCommission.value = 0.5F
                        b.sldAddCommission.value = 0.2F
                        b.sldDiscount.value = 5F
                        b.sldPerAdd.value = 0.2F
                    }
                }
            }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_register -> {
                true
            }
            R.id.action_bind_channel_to_user -> {
                showConfigDialog()
                true
            }
            else -> item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(
                item
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.channel, menu)
//        val searchItem = menu.findItem(R.id.action_search)
//        val searchView = searchItem.actionView as SearchView
//        val pendingQuery = teamViewModel.filter.value.keyName
//        if (!pendingQuery.isNullOrEmpty()) {
//            searchItem.expandActionView()
//            searchView.onActionViewExpanded() // Expand the SearchView
//            searchView.setQuery(pendingQuery, false)
//        }
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                teamViewModel.search(AgentFilter(keyName = newText.orEmpty()))
//                return true
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}