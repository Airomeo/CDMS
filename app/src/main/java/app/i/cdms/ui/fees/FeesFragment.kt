package app.i.cdms.ui.fees

import android.os.Bundle
import android.view.Menu
import android.view.View
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
import app.i.cdms.data.model.BookChannelDetail
import app.i.cdms.databinding.FragmentFeesBinding
import app.i.cdms.ui.book.BookChannelRecyclerViewAdapter
import app.i.cdms.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeesFragment : Fragment(R.layout.fragment_fees) {

    private val feesViewModel: FeesViewModel by viewModels()
    private var _binding: FragmentFeesBinding? = null
    private val binding get() = _binding!!
    private val listener = View.OnClickListener {
        binding.apply {
            when (it) {
                switcher -> {
                    it.animate().rotation(it.rotation + 180F)
                    // 坑，要先设置tag后设置text，因为设置了doOnTextChanged
                    val text = from.editText?.text
                    val tag = from.editText?.tag
                    from.editText?.tag = to.editText?.tag
                    from.editText?.text = to.editText?.text
                    to.editText?.tag = tag
                    to.editText?.text = text
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
        binding.from.editText?.apply {
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
                    feesViewModel.updateCompareFeeBody(
                        feesViewModel.compareFeeBody.value.copy(
                            senderCity = null,
                            senderCityCode = null
                        )
                    )
                    return@doOnTextChanged
                }
                val pairedAreaList = tag as List<Pair<String, String>>
                val fromCityName = pairedAreaList[1].first
                val fromCityCode = pairedAreaList[1].second
                feesViewModel.updateCompareFeeBody(
                    feesViewModel.compareFeeBody.value.copy(
                        senderCity = fromCityName,
                        senderCityCode = fromCityCode
                    )
                )
            }
        }
        binding.to.editText?.apply {
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
                    feesViewModel.updateCompareFeeBody(
                        feesViewModel.compareFeeBody.value.copy(
                            receiveCity = null,
                            receiveCityCode = null
                        )
                    )
                    return@doOnTextChanged
                }
                val pairedAreaList = tag as List<Pair<String, String>>
                val toCityName = pairedAreaList[1].first
                val toCityCode = pairedAreaList[1].second
                feesViewModel.updateCompareFeeBody(
                    feesViewModel.compareFeeBody.value.copy(
                        receiveCity = toCityName,
                        receiveCityCode = toCityCode
                    )
                )
            }
        }
        binding.goodsWeight.editText?.apply {
            doOnTextChanged { text, start, before, count ->
                feesViewModel.updateCompareFeeBody(
                    feesViewModel.compareFeeBody.value.copy(weight = text.toString().toIntOrNull())
                )
            }
            setText("1") // 手动触发doOnTextChanged，初始化数据
        }
        val rootOnClickCallback: (channel: BookChannelDetail) -> Unit = { channel -> }
        val mAdapter = BookChannelRecyclerViewAdapter(rootOnClickCallback)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feesViewModel.compareFeeBody.collectLatest {
                    if (it.receiveCity != null && it.receiveCityCode != null &&
                        it.senderCity != null && it.senderCityCode != null && it.weight != null
                    ) {
                        // 获取所有快递价格
                        feesViewModel.getCompareFee()
                    } else {
                        // 提示信息不完整
                        feesViewModel.updateBookChannelDetailList(null)
                    }
                }
            }
        }
        // 列出用户所有可用渠道
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feesViewModel.bookChannelDetailList.collectLatest {
                    if (it == null) {
                        binding.tips.text = getString(R.string.fees_tips)
                        binding.tips.visibility = View.VISIBLE
                        mAdapter.submitList(listOf())
                    } else {
                        if (it.isEmpty()) {
                            binding.tips.text = getString(R.string.fees_tips_no_available_channel)
                            binding.tips.visibility = View.VISIBLE
                            return@collectLatest
                        } else {
                            binding.tips.visibility = View.INVISIBLE
                            mAdapter.submitList(it)
                        }
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
        feesViewModel.areaList?.let {
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
}