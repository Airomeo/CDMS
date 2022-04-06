package app.i.cdms.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.MyInfo
import app.i.cdms.databinding.DialogChargeBinding
import app.i.cdms.databinding.FragmentHomeBinding
import app.i.cdms.ui.main.MainViewModel
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

        binding.refresh.setOnRefreshListener {
            homeViewModel.getMyInfo()
        }
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
        binding.charge.setOnClickListener {
            showChargeQrCodeDialog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.routers.collectLatest {
                        if (it.isEmpty()) return@collectLatest
                        val accountRouter = it.find { router -> router.name == "Account" }
                        val myTeamRouter =
                            accountRouter?.children?.find { router -> router.name == "My_team" }
                        binding.team.visibility =
                            if (myTeamRouter == null) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    homeViewModel.myInfo.collectLatest {
                        binding.refresh.isRefreshing = false
                        it ?: return@collectLatest
                        homeViewModel.updateMyInfo(it)
                        updateUiWithUser(it)
                    }
                }
                launch {
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
    }

    /**
     * 显示充值二维码弹窗
     *
     * @param
     * @return
     */
    private fun showChargeQrCodeDialog() {
        val b = DialogChargeBinding.inflate(layoutInflater, null, false)
        val adapter: ArrayAdapter<Int> = ArrayAdapter<Int>(
            requireContext(),
            R.layout.cat_exposed_dropdown_popup_item,
            resources.getIntArray(R.array.cat_charge_amount).toTypedArray()
        )
        b.tvAmount.setAdapter(adapter)
        val amountFlow = MutableStateFlow("")
        b.tvAmount.doOnTextChanged { text, start, before, count ->
            amountFlow.value = text.toString()
        }
        b.qrCode.setOnClickListener {
            homeViewModel.fetchChargeQrCode(
                b.tvAmount.text.toString().toFloat().times(100.6).roundToInt()
            )
        }
        lateinit var job1: Job
        lateinit var job2: Job
        viewLifecycleOwner.lifecycleScope.launch {
            job1 = launch {
                amountFlow.debounce(500).collectLatest {
                    val amount = if (it.isBlank()) {
                        0
                    } else {
                        it.toFloat().times(100.6).roundToInt()
                    }
                    homeViewModel.fetchChargeQrCode(amount)
                }
            }
            job2 = launch {
                homeViewModel.qrCode.collectLatest {
                    if (it == null) {
                        b.qrCode.setImageResource(R.drawable.ic_baseline_refresh_24)
                        return@collectLatest
                    }
                    val imageByteArray = Base64.decode(
                        it.qrCode.substringAfter("data:image/PNG;base64,"),
                        Base64.DEFAULT
                    )
                    val bitmap =
                        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    b.qrCode.load(bitmap) {
                        crossfade(true)
                        placeholder(R.drawable.ic_baseline_refresh_24)
                    }
                }
            }
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.common_charge)
            .setMessage(R.string.charge_tips)
            .setView(b.root)
            .setOnDismissListener {
                // cancel job
                job1.cancel()
                job2.cancel()
            }
            .show()
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