package app.i.cdms.ui.agent

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.databinding.AgentTransferBinding
import app.i.cdms.databinding.FragmentAgentBinding
import app.i.cdms.ui.team.TeamViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AgentFragment : Fragment(R.layout.fragment_agent) {

    private var _binding: FragmentAgentBinding? = null
    private val binding get() = _binding!!
    private val agentViewModel: AgentViewModel by viewModels()
    private val teamViewModel: TeamViewModel by hiltNavGraphViewModels(R.id.navigation_team)
    private lateinit var agent: Agent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentBinding.bind(view)

        agent = requireArguments().get("agent") as Agent
        // TODO: 2022/1/5 待优化，避免重复加载。 
        agentViewModel.getOrderCount(agent.userId)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                teamViewModel.myTeam.collectLatest { myTeam ->
                    myTeam ?: return@collectLatest
                    agent = myTeam.records.find {
                        it.userId == agent.userId
                    }!!
                    with(binding) {
                        tvUsername.text = agent.userName
                        tvId.text = getString(R.string.my_info_id, agent.userId.toString())
                        tvBalance.text =
                            getString(R.string.my_info_balance, agent.accountBalance.toString())
                        tvEarns.text = getString(R.string.my_info_earns, agent.earns.toString())
                        tvChannelCount.text =
                            getString(R.string.agent_channel_count, agent.channelCount.toString())
                        tvSubUserCount.text =
                            getString(R.string.agent_sub_user_count, agent.sonUserCount.toString())
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                agentViewModel.orderCount.collectLatest {
                    with(binding) {
                        tvYto.text =
                            getString(
                                R.string.agent_yto_order_count,
                                it?.ytoOrderCount.toString()
                            )
                        tvSto.text =
                            getString(
                                R.string.agent_sto_order_count,
                                it?.stoOrderCount.toString()
                            )
                        tvJd.text =
                            getString(R.string.agent_jd_order_count, it?.jdOrderCount.toString())
                        tvDpk.text =
                            getString(
                                R.string.agent_dpk_order_count,
                                it?.dopOrderCount.toString()
                            )
                    }
                }
            }
        }
        with(binding) {
            btnChannel.setOnClickListener {
                val bundle = bundleOf("userId" to agent.userId)
                findNavController().navigate(R.id.action_agentFragment_to_channelFragment, bundle)
            }
            btnTransfer.setOnClickListener {
                showTransferDialog(agent)
            }
            btnWithdraw.setOnClickListener {
                showWithdrawDialog(agent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                agentViewModel.uiState.collectLatest {
                    when (it) {
                        is AgentUiState.TransferSuccess -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.agent_transfer_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            teamViewModel.updateAgentInMyTeam(agent)
                        }
                        is AgentUiState.WithdrawSuccess -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.agent_withdraw_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            teamViewModel.updateAgentInMyTeam(agent)
                        }
                        is AgentUiState.UpdateChannelSuccess -> {
                            Toast.makeText(
                                requireContext(),
                                it.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                            teamViewModel.updateAgentInMyTeam(agent)
                        }
                    }
                }
            }
        }
    }

    private fun showTransferDialog(agent: Agent) {
        val b = AgentTransferBinding.inflate(layoutInflater, null, false)
        val adapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireContext(),
            R.layout.cat_exposed_dropdown_popup_item,
            resources.getIntArray(R.array.cat_transfer_amount).toTypedArray()
        )
        b.tvAmount.setAdapter(adapter)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.agent_dialog_transfer)
            .setView(b.root)
            .setPositiveButton(
                R.string.dialog_positive_text
            ) { dialog, which ->
                val chip = b.cgRecordType.findViewById<Chip>(b.cgRecordType.checkedChipId)
                if (b.tvAmount.text.isNotBlank()) {
                    agentViewModel.transfer(
                        agent.userId,
                        agent.userName,
                        b.etRemark.text.toString(),
                        chip.tag.toString(),
                        b.tvAmount.text.toString().toFloat()
                    )
                }
            }
            .setNegativeButton(R.string.dialog_negative_text, null)
            .show()
    }

    private fun showWithdrawDialog(agent: Agent) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.agent_dialog_withdraw)
            .setMessage(getString(R.string.agent_dialog_withdraw_message, agent.userName))
            .setPositiveButton(
                R.string.dialog_positive_text
            ) { dialog, which ->
                agentViewModel.withdraw(agent.userId)
            }
            .setNegativeButton(R.string.dialog_negative_text, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}