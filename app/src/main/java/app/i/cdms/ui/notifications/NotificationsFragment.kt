package app.i.cdms.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.i.cdms.R
import app.i.cdms.databinding.FragmentNotificationsBinding
import app.i.cdms.ui.book.BookFragment
import app.i.cdms.ui.fees.FeesFragment
import app.i.cdms.ui.home.HomeFragment
import app.i.cdms.ui.main.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
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
        with(binding) {
            val fragments = listOf(
                Pair(R.string.title_home, HomeFragment()),
                Pair(R.string.title_fees, FeesFragment()),
                Pair(R.string.title_book, BookFragment()),
            )
            pager.adapter =
                object : FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
                    override fun getItemCount() = fragments.size

                    override fun createFragment(position: Int) = fragments[position].second
                }
            TabLayoutMediator(tabLayouts, pager) { tab, position ->
                tab.text = getString(fragments[position].first)
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}