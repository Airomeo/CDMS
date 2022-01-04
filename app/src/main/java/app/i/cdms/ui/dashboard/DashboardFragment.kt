package app.i.cdms.ui.dashboard

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.i.cdms.Constant
import app.i.cdms.R
import app.i.cdms.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        binding.webView.webViewClient = WebViewClient() // set the WebViewClient
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.loadUrl(Constant.API_DOC)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}