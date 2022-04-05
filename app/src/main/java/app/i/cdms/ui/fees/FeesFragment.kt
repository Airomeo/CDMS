package app.i.cdms.ui.fees

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.R
import app.i.cdms.data.model.Channel
import app.i.cdms.databinding.FragmentFeesBinding
import app.i.cdms.ui.book.BookViewModel
import app.i.cdms.ui.channel.ChannelRecyclerViewAdapter
import app.i.cdms.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeesFragment : Fragment(R.layout.fragment_fees) {

    private val viewModel: BookViewModel by viewModels()
    private var _binding: FragmentFeesBinding? = null
    private val binding get() = _binding!!
    private val listener = View.OnClickListener {
        binding.apply {
            when (it) {
                switcher -> {
                    it.animate().rotation(it.rotation + 180F)
                    // 坑，要先设置tag后设置text，因为设置了doOnTextChanged
                    val text = fromMenu.text
                    val tag = fromMenu.tag
                    fromMenu.tag = toMenu.tag
                    fromMenu.text = toMenu.text
                    toMenu.tag = tag
                    toMenu.text = text
                }
                weightMinus -> {
                    var weight = tvGoodsWeight.text.toString().toIntOrNull() ?: 1
                    weight -= 1
                    if (weight < 1) {
                        weight = 1
                    }
                    tvGoodsWeight.setText(weight.toString())
                }
                weightPlus -> {
                    var weight = tvGoodsWeight.text.toString().toIntOrNull() ?: 1
                    weight += 1
                    tvGoodsWeight.setText(weight.toString())
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeesBinding.bind(view)

        binding.weightMinus.setOnClickListener(listener)
        binding.weightPlus.setOnClickListener(listener)
        binding.switcher.setOnClickListener(listener)
        binding.fromMenu.apply {
            showSoftInputOnFocus = false
            setOnClickListener { showMenu(it) }
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.performClick()
                    v.hideKeyboard()
                }
            }
            doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank()) {
                    tag = null
                    viewModel.updateBookBodyFlow(
                        viewModel.bookBody.value.copy(
                            senderCity = null,
                            senderValues = null
                        )
                    )
                    return@doOnTextChanged
                }
                val pairedAreaList = tag as List<Pair<String, String>>
                val fromCityName = pairedAreaList[1].first
                val fromProvinceCode = pairedAreaList[0].second
                val fromCityCode = pairedAreaList[1].second
                viewModel.updateBookBodyFlow(
                    viewModel.bookBody.value.copy(
                        senderCity = fromCityName,
                        senderValues = listOf(fromProvinceCode, fromCityCode)
                    )
                )
            }
        }
        binding.toMenu.apply {
            showSoftInputOnFocus = false
            setOnClickListener { showMenu(it) }
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.performClick()
                    v.hideKeyboard()
                }
            }
            doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank()) {
                    tag = null
                    viewModel.updateBookBodyFlow(
                        viewModel.bookBody.value.copy(
                            receiveCity = null,
                            receiveValues = null
                        )
                    )
                    return@doOnTextChanged
                }
                val pairedAreaList = tag as List<Pair<String, String>>
                val toCityName = pairedAreaList[1].first
                val toProvinceCode = pairedAreaList[0].second
                val toCityCode = pairedAreaList[1].second
                viewModel.updateBookBodyFlow(
                    viewModel.bookBody.value.copy(
                        receiveCity = toCityName,
                        receiveValues = listOf(toProvinceCode, toCityCode)
                    )
                )
            }
        }
        binding.tvGoodsWeight.apply {
            doOnTextChanged { text, start, before, count ->
                viewModel.updateBookBodyFlow(
                    viewModel.bookBody.value.copy(weight = text.toString().toIntOrNull())
                )
            }
            setText("1") // 手动触发doOnTextChanged，初始化数据
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getIntArray(R.array.cat_goods_weight).toTypedArray()
                )
            )
        }
        val rootOnClickCallback: (channel: Channel) -> Unit = { channel -> }
        val mAdapter = ChannelRecyclerViewAdapter(rootOnClickCallback)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        // 列出用户所有可用渠道
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.compareFeeBody.collectLatest {
                        if (it.isFulfilled()) {
                            // 获取所有快递价格
                            viewModel.fetchCompareFee()
                        } else {
                            // 提示信息不完整
                            viewModel._channelFees.value = emptyList()
                        }
                    }
                }
                launch {
                    viewModel.channelsFlow.collectLatest { list ->
                        binding.tips.text = if (list.isEmpty()) {
                            if (viewModel.compareFeeBody.value.isFulfilled()) {
                                getString(R.string.fees_tips_no_available_channel)
                            } else {
                                getString(R.string.fees_tips)
                            }
                        } else {
                            null
                        }
                        mAdapter.submitList(list)
                    }
                }
            }
        }
    }

    /**
     * 选择省市区窗口
     *
     * @param view: The view which popup attach to
     * @return
     */
    private fun showMenu(view: View) {
        viewModel.areaList?.let {
            val popup = PopupMenu(requireContext(), view)
            for (a in it) {
                if (a.children == null) {
                    popup.menu.add(Menu.NONE, a.value.toInt(), Menu.NONE, a.label)
                } else {
                    val aMenu =
                        popup.menu.addSubMenu(Menu.NONE, a.value.toInt(), Menu.NONE, a.label)
                    for (b in a.children) {
                        aMenu.add(Menu.NONE, b.value.toInt(), Menu.NONE, b.label)
                    }
                }
            }
            val edit = view as EditText
            var str = ""
            val list = mutableListOf<Pair<String, String>>()
            popup.setOnMenuItemClickListener { item ->
                str += item.title.toString()
                list.add(Pair(item.title.toString(), item.itemId.toString()))
                if (!item.hasSubMenu()) {
                    edit.tag = list
                    edit.setText(str)
                }
                true
            }
            popup.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}