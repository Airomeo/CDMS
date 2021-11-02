package app.i.cdms.ui.agent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.databinding.FragmentAgentBinding
import app.i.cdms.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgentFragment : Fragment(R.layout.fragment_agent) {

    private var _binding: FragmentAgentBinding? = null
    private val binding get() = _binding!!
    private val agentViewModel: AgentViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAgentBinding.bind(view)

        val agent = (requireArguments().get("agent") as Agent)
        with(binding) {
            tvUsername.text = agent.userName
            tvId.text = getString(R.string.my_info_id, agent.userId.toString())
            tvBalance.text = getString(R.string.my_info_balance, agent.accountBalance.toString())
            tvEarns.text = getString(R.string.my_info_earns, agent.earns.toString())
            tvChannelCount.text =
                getString(R.string.agent_channel_count, agent.channelCount.toString())
            tvYto.text = getString(R.string.agent_yto_order_count, agent.ytoOrderCount.toString())
            tvSto.text = getString(R.string.agent_sto_order_count, agent.stoOrderCount.toString())
            tvJd.text = getString(R.string.agent_jd_order_count, agent.jdOrderCount.toString())

            tvFirstCommission.text =
                getString(R.string.first_commission, sldFirstCommission.value.toString())
            tvAdditionalCommission.text =
                getString(
                    R.string.additional_commission,
                    sldAdditionalCommission.value.toString()
                )
            sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
                tvFirstCommission.text =
                    getString(R.string.first_commission, value.toString())
            }
            sldAdditionalCommission.addOnChangeListener { slider, value, fromUser ->
                tvAdditionalCommission.text =
                    getString(R.string.additional_commission, value.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}