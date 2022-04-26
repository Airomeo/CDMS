package app.i.cdms.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import app.i.cdms.R
import app.i.cdms.data.model.AgentLevel
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
            homeViewModel.getNotice()
        }
        binding.ivAvatar.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        val mAdapter = HomeMenuAdapter()
        with(binding.recyclerView) {
            adapter = mAdapter
            layoutManager = GridLayoutManager(context, 4)
        }
        val menus = listOf(
            HomeMenuItem(
                R.drawable.ic_baseline_list_24,
                R.string.agent_channel_detail
            ) {
                findNavController().navigate(R.id.channelFragment)
            },
            HomeMenuItem(
                R.drawable.ic_baseline_currency_yen_24,
                R.string.common_charge
            ) {
                showChargeQrCodeDialog()
            },
            HomeMenuItem(
                R.drawable.ic_baseline_person_add_24,
                R.string.title_register
            ) {
                findNavController().navigate(R.id.AuthFragment)
            },
            HomeMenuItem(
                R.drawable.ic_baseline_apps_24,
                R.string.common_libs
            ) {
                findNavController().navigate(R.id.aboutLibrariesFragment)
            },
            HomeMenuItem(
                R.drawable.ic_baseline_menu_book_24,
                R.string.common_doc
            ) {
                findNavController().navigate(R.id.dashboardFragment)
            },
            HomeMenuItem(
                R.drawable.ic_baseline_logout_24,
                R.string.title_logout
            ) {
                mainViewModel.updateToken(null)
                homeViewModel.updateMyInfo(null)
            },
        )
        mAdapter.submitList(menus)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.routers.collectLatest {
                        if (it.isEmpty()) return@collectLatest
                        val accountRouter = it.find { router -> router.name == "Account" }
                        val myTeamRouter =
                            accountRouter?.children?.find { router -> router.name == "My_team" }
                        if (myTeamRouter != null) {
                            mAdapter.submitList(
                                listOf(
                                    HomeMenuItem(
                                        R.drawable.ic_baseline_people_24,
                                        R.string.my_team
                                    ) {
                                        findNavController().navigate(R.id.navigation_team)
                                    },
                                    HomeMenuItem(
                                        R.drawable.ic_baseline_alternate_email_24,
                                        R.string.action_invite
                                    ) {
                                        mainViewModel.fetchAgentLevel()
                                    }).plus(menus)
                            )
                        }
                    }
                }
                launch {
                    homeViewModel.refreshing.collectLatest {
                        binding.refresh.isRefreshing = it
                    }
                }
                launch {
                    homeViewModel.myInfo.collectLatest {
                        if (it == null) {
                            binding.tvUsername.setText(R.string.title_not_login)
                            binding.tvBalance.setText(R.string.title_not_login_tips)
                            binding.tvEarns.text = null
                            binding.tvBalance.text = null
                            return@collectLatest
                        }
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
                launch {
                    mainViewModel.agentLevelList.collectLatest {
                        if (it.isEmpty()) {
                            Toast.makeText(requireContext(), "您当前的代理等级没有邀请权限", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            showInviteDialog(it)
                        }
                    }
                }
                launch {
                    mainViewModel.inviteCode.collectLatest {
                        it ?: return@collectLatest
                        showInviteCodeDialog(it)
                    }
                }
            }
        }
    }


    /**
     * 显示获取邀请码窗口
     *
     * @param list: List<AgentLevel>
     * @return
     */
    private fun showInviteDialog(list: List<AgentLevel>) {
        val array = list.map { it.desc }.toTypedArray()
        var selectedIndex = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.invite_choice_dialog_title)
            .setPositiveButton(R.string.dialog_positive_text) { dialog, which ->
                mainViewModel.fetchInviteCode(list[selectedIndex].type)
            }
            .setNegativeButton(R.string.dialog_negative_text, null)
            .setSingleChoiceItems(
                array, selectedIndex
            ) { dialog, which ->
//                ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                selectedIndex = which
            }
            .show()
    }

    /**
     * 显示生成的邀请码文案
     *
     * @param code: 邀请码
     * @return
     */
    private fun showInviteCodeDialog(code: String) {
        val msg =
            "欢迎加入易达，邀请码${code}，有效期24小时，打开链接注册账户：https://www.yida178.cn/register?code=${code}"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.common_result)
            .setMessage(msg)
            .setPositiveButton(R.string.common_copy) { dialog, which ->
                // copy
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("simple text", msg)
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip)
            }
            .show()
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
                    if (it.isBlank()) {
                        homeViewModel.fetchChargeQrCode(0)
                        b.pay.text = null
                    } else {
                        val amount = it.toFloat().times(100.6).roundToInt()
                        homeViewModel.fetchChargeQrCode(amount)
                        val pay = amount.div(100F)
                        b.pay.text = getString(R.string.price, pay.toString())
                    }
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

data class HomeMenuItem(val iconRes: Int, val titleRes: Int, val block: () -> Unit)
