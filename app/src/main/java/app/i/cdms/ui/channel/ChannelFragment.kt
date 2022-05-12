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
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.databinding.DialogChannelConfigBinding
import app.i.cdms.databinding.FragmentChannelBinding
import app.i.cdms.ui.main.MainViewModel
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!
    private val channelViewModel: ChannelViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accountRouter = mainViewModel.routers.value.find { router -> router.name == "Account" }
        val myTeamRouter =
            accountRouter?.children?.find { router -> router.name == "My_team" }
        if (myTeamRouter != null) {
            setHasOptionsMenu(true)
            channelViewModel.getMyTeam(1, 9999, null, null, null, null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChannelBinding.bind(view)

        val rootOnClickCallback: (channel: Channel) -> Unit = { channel -> }
        val mAdapter = ChannelRecyclerViewAdapter(rootOnClickCallback)
        // Set the adapter
        with(binding.list) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)// Explanation of setHasFixedSize. https://stackoverflow.com/a/59033210/10276438
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    channelViewModel.channelsFlow.collectLatest {
                        mAdapter.submitList(it)
                    }
                }
            }
        }
    }

    /**
     * 配置佣金
     *
     * @return
     */
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
                channelViewModel.selectedChannelOfUsers =
                    channelViewModel.selectedChannelOfUsers.copy(
                        firstCommission = b.sldFirstCommission.value,
                        addCommission = b.sldAddCommission.value,
                        discountPercent = b.sldDiscount.value * 0.01F,
                        perAdd = b.sldPerAdd.value
                    )
                showCustomerDialog(ChannelOperation.ADD_AND_UPDATE)
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

    /**
     * 显示所有商家渠道
     *
     * @param operation: ChannelOperation 操作类型
     * @return
     */
    private fun showCustomerDialog(operation: ChannelOperation) {
        val customerChannels = arrayListOf<CustomerChannel>()
        channelViewModel.customerChannels.value.forEach { customerChannels.addAll(it.mapNotNull()) }

        val choicesTextArray = customerChannels.map { it.customerName }.toTypedArray()
        // all elements initialized to false.
        val choicesCheckedArray = BooleanArray(choicesTextArray.size)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.channel_update_config_dialog_title)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_positive_text, null)
            .setNeutralButton(R.string.channel_update_config_dialog_invert_select, null)
            .setNegativeButton(R.string.dialog_negative_text, null)
            .setMultiChoiceItems(
                choicesTextArray,
                choicesCheckedArray,
            ) { dialogInterface, position, isChecked ->
                choicesCheckedArray[position] = isChecked
            }
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (listView.checkedItemCount <= 0) {
                            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_channel_selected))
                            return@setOnClickListener
                        }
                        val selectedCustomerIds = mutableListOf<Int>()
                        for (i in choicesCheckedArray.indices) {
                            if (choicesCheckedArray[i]) {
                                selectedCustomerIds.add(customerChannels[i].customerId)
                            }
                        }
                        channelViewModel.selectedChannelOfUsers =
                            channelViewModel.selectedChannelOfUsers.copy(customerIds = selectedCustomerIds)

                        dismiss()
                        showUserLevelDialog(operation)
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

    /**
     * 如果该用户有多层下级，则弹窗让用户选择要操作哪层下级
     *
     * @param operation: ChannelOperation 操作类型
     * @return
     */
    private fun showUserLevelDialog(operation: ChannelOperation) {
        // 直系下级
        val myDirectTeam = channelViewModel.myTeam!!.rows!!.filter { agent ->
            agent.parentUserId == mainViewModel.myInfo?.userId
        }
        // 根据levelType 把用户分组
        val group = myDirectTeam.groupBy { it.levelType }
        if (group.size == 1) {
            showUserDialog(myDirectTeam, operation)
            return
        }
        val choicesTextArray = group.keys.map { distinctLevelType ->
            when (distinctLevelType) {
                10 -> "核心代理  "
                20 -> "一级代理  "
                30 -> "二级代理  "
                else -> "其他层级  "
            } + group[distinctLevelType]!!.size + "位"
        }.toTypedArray()
        // all elements initialized to false.
        val choicesCheckedArray = BooleanArray(choicesTextArray.size)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.channel_update_config_dialog_title_group)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_positive_text, null)
            .setNeutralButton(R.string.channel_update_config_dialog_invert_select, null)
            .setNegativeButton(R.string.dialog_negative_text, null)
            .setMultiChoiceItems(
                choicesTextArray,
                choicesCheckedArray,
            ) { dialogInterface, position, isChecked ->
                choicesCheckedArray[position] = isChecked
            }
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (listView.checkedItemCount <= 0) {
                            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_user_group_selected))
                            return@setOnClickListener
                        }
                        val agents = arrayListOf<Agent>()
                        for (i in choicesCheckedArray.indices) {
                            if (choicesCheckedArray[i]) {
                                agents.addAll(group[group.keys.elementAt(i)]!!)
                            }
                        }
                        dismiss()
                        showUserDialog(agents, operation)
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

    /**
     * 提示用户选择下级
     *
     * @param agents: List<Agent> 用户所选层级内的所有下级
     * @param operation: ChannelOperation 操作类型
     * @return
     */
    private fun showUserDialog(agents: List<Agent>, operation: ChannelOperation) {
        val choicesTextArray = agents.map { it.userName }.toTypedArray()
        // all elements initialized to false.
        val choicesCheckedArray = BooleanArray(choicesTextArray.size)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.channel_update_config_dialog_title)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_positive_text, null)
            .setNeutralButton(R.string.channel_update_config_dialog_invert_select, null)
            .setNegativeButton(R.string.dialog_negative_text, null)
            .setMultiChoiceItems(
                choicesTextArray,
                choicesCheckedArray,
            ) { dialogInterface, position, isChecked ->
                choicesCheckedArray[position] = isChecked
            }
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (listView.checkedItemCount <= 0) {
                            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_user_selected))
                            return@setOnClickListener
                        }
                        val userIds = arrayListOf<Int>()
                        for (i in choicesCheckedArray.indices) {
                            if (choicesCheckedArray[i]) {
                                userIds.add(agents[i].userId)
                            }
                        }
                        channelViewModel.selectedChannelOfUsers =
                            channelViewModel.selectedChannelOfUsers.copy(userIds = userIds)
                        when (operation) {
                            ChannelOperation.ADD_AND_UPDATE -> {
                                channelViewModel.updateChannel()
                            }
                            ChannelOperation.DELETE -> {
                                channelViewModel.deleteUserPrice()
                            }
                        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bind_channel_to_user -> {
                if (channelViewModel.myTeam?.rows == null) {
                    channelViewModel.getMyTeam(1, 9999, null, null, null, null)
                    return true
                } else if (channelViewModel.myTeam?.rows!!.isEmpty()) {
                    EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_user))
                    return true
                }
                showConfigDialog()
                true
            }
            R.id.action_delete_channel_to_user -> {
                if (channelViewModel.myTeam?.rows == null) {
                    channelViewModel.getMyTeam(1, 9999, null, null, null, null)
                    return true
                } else if (channelViewModel.myTeam?.rows!!.isEmpty()) {
                    EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_no_user))
                    return true
                }
                showCustomerDialog(ChannelOperation.DELETE)
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