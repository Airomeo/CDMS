package app.i.cdms.ui.home

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.MyInfo
import app.i.cdms.databinding.FragmentHomeBinding
import app.i.cdms.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.ivAvatar.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.channel.setOnClickListener {
            findNavController().navigate(R.id.channelFragment)
        }

//        binding.logout.setOnClickListener {
//            mainViewModel.updateToken(null)
//            homeViewModel.updateMyInfo(null)
//        }

        binding.team.setOnClickListener {
            findNavController().navigate(R.id.navigation_team)
        }
        binding.intro.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.myInfo.collectLatest {
                    it ?: return@collectLatest
                    homeViewModel.updateMyInfo(it)
                    homeViewModel.getNotice()
                    updateUiWithUser(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.noticeList.collectLatest {
                    it ?: return@collectLatest
                    var text = ""
                    for (notice in it.notices) {
                        text = text + notice.updateTime + Html.fromHtml(
                            notice.noticeContent,
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    }
                    binding.notice.text = text
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