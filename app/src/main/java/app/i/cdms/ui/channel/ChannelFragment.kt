package app.i.cdms.ui.channel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.R
import app.i.cdms.databinding.FragmentChannelBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!
    private val channelViewModel: ChannelViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChannelBinding.bind(view)

        channelViewModel.getAllChannelDetail()

        val mAdapter = ChannelRecyclerViewAdapter()
        // Set the adapter
        with(binding.list) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                channelViewModel.channelDetailListFlow.collect {
                    mAdapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}