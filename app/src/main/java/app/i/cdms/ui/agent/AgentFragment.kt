package app.i.cdms.ui.agent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import app.i.cdms.R
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

        binding.tvFirstCommission.text =
            getString(R.string.first_commission, binding.sldFirstCommission.value.toString())
        binding.tvAdditionalCommission.text =
            getString(
                R.string.additional_commission,
                binding.sldAdditionalCommission.value.toString()
            )
        binding.sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
            binding.tvFirstCommission.text = getString(R.string.first_commission, value.toString())
        }
        binding.sldAdditionalCommission.addOnChangeListener { slider, value, fromUser ->
            binding.tvAdditionalCommission.text =
                getString(R.string.additional_commission, value.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}