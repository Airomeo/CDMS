package app.i.cdms.ui.channel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.R
import app.i.cdms.data.model.Channel
import app.i.cdms.databinding.FragmentChannelBinding
import app.i.cdms.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!
    private val channelViewModel: ChannelViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChannelBinding.bind(view)

        val rootOnClickCallback: (channel: Channel) -> Unit = { channel -> }
        val mAdapter = ChannelRecyclerViewAdapter(rootOnClickCallback)
        // Set the adapter
        with(binding.list) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)// Explanation of setHasFixedSize. https://stackoverflow.com/a/59033210/10276438
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                channelViewModel.channelsFlow.collectLatest {
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