package app.i.cdms.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Token
import app.i.cdms.databinding.FragmentHomeBinding
import app.i.cdms.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.ivAvatar.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.button3.setOnClickListener {
            mainViewModel.updateToken(Token(System.currentTimeMillis().toString()))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.myInfo.collectLatest {
                    it ?: return@collectLatest
                    updateUiWithUser(it)
                    binding.textHome.text = it.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUiWithUser(myInfo: MyInfo) {
        binding.tvUsername.text = myInfo.userName
        binding.tvId.text = getString(R.string.my_info_id, myInfo.userId.toString())
        binding.tvBalance.text =
            getString(R.string.my_info_balance, myInfo.accountBalance.toString())
        if (myInfo.earns > 0) {
            binding.tvEarns.visibility = View.VISIBLE
            binding.tvEarns.text =
                getString(R.string.my_info_earns, myInfo.earns.toString())
        } else {
            binding.tvEarns.visibility = View.GONE
        }
    }
}