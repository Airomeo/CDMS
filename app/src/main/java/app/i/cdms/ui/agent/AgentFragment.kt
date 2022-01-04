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
import app.i.cdms.databinding.DialogChannelConfigBinding
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
        agentViewModel.getUserConfig(agent.userId)

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
                        tvYto.text =
                            getString(
                                R.string.agent_yto_order_count,
                                agent.ytoOrderCount.toString()
                            )
                        tvSto.text =
                            getString(
                                R.string.agent_sto_order_count,
                                agent.stoOrderCount.toString()
                            )
                        tvJd.text =
                            getString(R.string.agent_jd_order_count, agent.jdOrderCount.toString())
                        tvDpk.text =
                            getString(
                                R.string.agent_dpk_order_count,
                                agent.dopOrderCount.toString()
                            )
                    }
                }
            }
        }
        with(binding) {
            btnUpdateChannel.setOnClickListener {
                showUpdateDialog()
            }
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                agentViewModel.userConfig.collectLatest {
                    binding.tvFirstWeight.text = getString(
                        R.string.config_first_weight,
                        (it?.firstWeight ?: 1.0F).toString()
                    )
                    binding.tvFirstCommission.text = getString(
                        R.string.config_first_commission,
                        (it?.firstCommission ?: 0.5F).toString()
                    )
                    binding.tvAddCommission.text = getString(
                        R.string.config_add_commission,
                        (it?.addCommission ?: 0.2F).toString()
                    )
                }
            }
        }
    }

    private fun showUpdateDialog() {
        val b = DialogChannelConfigBinding.inflate(layoutInflater, null, false)
        with(b) {
            tvFirstWeight.text = binding.tvFirstWeight.text
            tvFirstCommission.text = binding.tvFirstCommission.text
            tvAddCommission.text = binding.tvAddCommission.text
            val config = agentViewModel.userConfig.value
            sldFirstWeight.value = config?.firstWeight ?: 1.0F
            sldFirstCommission.value = config?.firstCommission ?: 0.5F
            sldAddCommission.value = config?.addCommission ?: 0.2F
            sldFirstWeight.addOnChangeListener { slider, value, fromUser ->
                tvFirstWeight.text =
                    getString(R.string.config_first_weight, sldFirstWeight.value.toString())
            }
            sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
                tvFirstCommission.text =
                    getString(R.string.config_first_commission, value.toString())
            }
            sldAddCommission.addOnChangeListener { slider, value, fromUser ->
                tvAddCommission.text =
                    getString(R.string.config_add_commission, value.toString())
            }
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_warning)
            .setView(b.root)
            .setMessage(getString(R.string.agent_dialog_update_channel_message, agent.userName))
            .setPositiveButton(
                R.string.dialog_positive_text
            ) { dialog, which ->
                val userConfig = agentViewModel.userConfig.value
                val doConfig = if (userConfig == null) {
                    1 // addUserConfig
                } else if (userConfig.firstWeight != b.sldFirstWeight.value || userConfig.firstCommission != b.sldFirstCommission.value || userConfig.addCommission != b.sldAddCommission.value) {
                    2 // updateUserConfig
                } else {
                    0 // Do Nothing
                }
                agentViewModel.updateChannelByUserId(
                    agent.userId,
                    b.sldFirstWeight.value,
                    b.sldFirstCommission.value,
                    b.sldAddCommission.value,
                    doConfig
                )
                binding.tvFirstWeight.text = b.tvFirstWeight.text
                binding.tvFirstCommission.text = b.tvFirstCommission.text
                binding.tvAddCommission.text = b.tvAddCommission.text
            }
            .setNegativeButton(R.string.dialog_negative_text, null)
            .show()
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