package app.i.cdms.ui.fees

import android.os.Bundle
import android.view.Menu
import android.view.View
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
import app.i.cdms.ui.channel.ChannelRecyclerViewAdapter
import app.i.cdms.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeesFragment : Fragment(R.layout.fragment_fees) {

    private val viewModel: FeesViewModel by viewModels()
    private var _binding: FragmentFeesBinding? = null
    private val binding get() = _binding!!
    private var scCode: String? = null
    private var rcCode: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Restore state members from saved instance
        savedInstanceState?.run {
            scCode = getString(STATE_SCCODE)
            rcCode = getString(STATE_RCCODE)
        }
        _binding = FragmentFeesBinding.bind(view)
        with(binding) {
            switcher.setOnClickListener {
                it.animate().rotation(it.rotation + 180F)
                // 坑，要先设置code后设置text，因为设置了doOnTextChanged
                val code = scCode
                scCode = rcCode
                rcCode = code
                val text = fromMenu.text
                fromMenu.text = toMenu.text
                toMenu.text = text
            }
            with(fromMenu) {
                showSoftInputOnFocus = false
                setOnClickListener {
                    showMenu(it) { list ->
                        scCode = list[1].second
                        setText(list[0].first + list[1].first)
                    }
                }
                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        v.performClick()
                        v.hideKeyboard()
                    }
                }
                doOnTextChanged { text, start, before, count ->
                    viewModel.fetchCustomerChannels(scCode, rcCode)
                }
            }
            with(toMenu) {
                showSoftInputOnFocus = false
                setOnClickListener {
                    showMenu(it) { list ->
                        rcCode = list[1].second
                        setText(list[0].first + list[1].first)
                    }
                }
                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        v.performClick()
                        v.hideKeyboard()
                    }
                }
                doOnTextChanged { text, start, before, count ->
                    viewModel.fetchCustomerChannels(scCode, rcCode)
                }
            }
            val rootOnClickCallback: (channel: Channel) -> Unit = { channel -> }
            val mAdapter = ChannelRecyclerViewAdapter(rootOnClickCallback)
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)

            // 列出用户所有可用渠道
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.channels.collectLatest { list ->
                            tips.text = if (list.isEmpty()) {
                                if (scCode != null && rcCode != null) {
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
    }

    /**
     * 选择省市区窗口
     *
     * @param view: The view which popup attach to
     * @return
     */
    private fun showMenu(view: View, block: (list: List<Pair<String, String>>) -> Unit) {
        viewModel.areaList?.let {
            val popup = PopupMenu(view.context, view)
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
            val list = mutableListOf<Pair<String, String>>()
            popup.setOnMenuItemClickListener { item ->
                list.add(Pair(item.title.toString(), item.itemId.toString()))
                if (!item.hasSubMenu()) {
                    block.invoke(list)
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString(STATE_SCCODE, scCode)
            putString(STATE_RCCODE, rcCode)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val STATE_SCCODE = "scCode"
        const val STATE_RCCODE = "rcCode"
    }
}