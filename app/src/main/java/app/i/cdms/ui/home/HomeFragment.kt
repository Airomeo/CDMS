package app.i.cdms.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Token
import app.i.cdms.databinding.FragmentHomeBinding
import app.i.cdms.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome

        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        binding.ivAvatar.setOnClickListener {
            findNavController().navigate(R.id.navigation_login)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.myInfo.observe(viewLifecycleOwner, { it ->
            it ?: return@observe
            when (it.code) {
                200 -> {
                    it.data?.let {
                        updateUiWithUser(it)
                        mainViewModel.updateMyInfo(it)
                        binding.textHome.text = it.toString()
                    }
                }
                401 -> {
//                    {"code":401,"msg":"请求访问：/wl/home/myInfo，认证失败，无法访问系统资源","data":null}
                    findNavController().navigate(R.id.navigation_login)
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    findNavController().navigate(R.id.navigation_login)
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        })
        mainViewModel.token.observe(viewLifecycleOwner, {
            it?.let {
                mainViewModel.getMyInfo()
            }
        })
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.navigation_register)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUiWithUser(myInfo: MyInfo) {
        binding.tvUsername.text = myInfo.userName
        binding.tvBalance.text =
            getString(R.string.my_info_balance, myInfo.accountBalance.toString())
        if (myInfo.earns > 0) {
            binding.tvEarns.visibility = View.VISIBLE
            binding.tvEarns.text =
                getString(R.string.my_info_earns, myInfo.earns.toString())
        }else{
            binding.tvEarns.visibility = View.GONE
        }
    }
}