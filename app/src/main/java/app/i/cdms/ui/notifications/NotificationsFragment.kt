package app.i.cdms.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.databinding.FragmentNotificationsBinding
import app.i.cdms.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationsBinding.bind(view)

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.navigation_team)
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.bookFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}