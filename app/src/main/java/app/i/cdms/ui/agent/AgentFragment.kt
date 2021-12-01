package app.i.cdms.ui.agent

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.databinding.AgentTransferBinding
import app.i.cdms.databinding.FragmentAgentBinding
import app.i.cdms.ui.team.TeamViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgentFragment : Fragment(R.layout.fragment_agent) {

    private var _binding: FragmentAgentBinding? = null
    private val binding get() = _binding!!
    private val agentViewModel: AgentViewModel by viewModel()
    private val teamViewModel: TeamViewModel by sharedViewModel()
    private lateinit var agent: Agent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentBinding.bind(view)

        agent = requireArguments().get("agent") as Agent
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                teamViewModel.myTeam.collectLatest { myTeam ->
                    agent = myTeam.records.first { it.userId == agent.userId }
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
                        tvFirstCommission.text =
                            getString(
                                R.string.first_commission,
                                sldFirstCommission.value.toString()
                            )
                        tvAdditionalCommission.text =
                            getString(
                                R.string.additional_commission,
                                sldAdditionalCommission.value.toString()
                            )
                    }
                }
            }
        }

        with(binding) {
            sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
                tvFirstCommission.text =
                    getString(R.string.first_commission, value.toString())
            }
            sldAdditionalCommission.addOnChangeListener { slider, value, fromUser ->
                tvAdditionalCommission.text =
                    getString(R.string.additional_commission, value.toString())
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
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                R.string.agent_transfer_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            teamViewModel.updateAgentBalanceUIData(
                                it.changeAmount + agent.accountBalance,
                                agent
                            )
                        }
                        is AgentUiState.WithdrawSuccess -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                R.string.agent_withdraw_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            teamViewModel.updateAgentBalanceUIData(0F, agent)
                        }
                        is AgentUiState.Error -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.exception.message ?: "网络异常/异常信息为空",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AgentUiState.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is AgentUiState.None -> {
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