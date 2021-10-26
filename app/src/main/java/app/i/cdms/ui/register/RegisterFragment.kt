package app.i.cdms.ui.register

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.databinding.FragmentLoginBinding
import app.i.cdms.databinding.FragmentRegisterBinding
import app.i.cdms.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        registerViewModel.registerFormState.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                binding.button.isEnabled = registerFormState.isDataValid
                registerFormState.usernameError?.let {
                    binding.username.error = getString(it)
                }
                registerFormState.passwordError?.let {
                    binding.password.error = getString(it)
                }
                registerFormState.phoneError?.let {
                    binding.phone.error = getString(it)
                }
            })

        registerViewModel.registerResult.observe(viewLifecycleOwner,
            Observer { registerResult ->
                registerResult ?: return@Observer
                binding.loading.visibility = View.GONE
                // {"code":500,"msg":"新增用户'小白'失败，登录账号已存在","data":null}
                // {"code":500,"msg":"新增用户'徐良'失败，手机号码已存在","data":null}
                // {"code":200,"msg":"操作成功","data":null}
                when (registerResult.code) {
                    200 -> {
                        Toast.makeText(requireContext(), registerResult.msg, Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }
                    500 -> {
                        Toast.makeText(requireContext(), registerResult.msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), registerResult.msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registerViewModel.registerDataChanged(
                    binding.username.text.toString(),
                    binding.password.text.toString(),
                    binding.phone.text.toString()
                )
            }
        }
        binding.username.addTextChangedListener(afterTextChangedListener)
        binding.password.addTextChangedListener(afterTextChangedListener)
        binding.phone.addTextChangedListener(afterTextChangedListener)
        binding.phone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.register(
                    mainViewModel.token.value,
                    binding.username.text.toString(),
                    binding.password.text.toString(),
                    binding.phone.text.toString()
                )
            }
            false
        }
        binding.button.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            registerViewModel.register(
                mainViewModel.token.value,
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.phone.text.toString()
            )
        }
    }
}