package app.i.cdms.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.databinding.FragmentRegisterBinding
import app.i.cdms.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        registerViewModel.registerFormState.observe(
            viewLifecycleOwner,
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.uiState.collectLatest {
                    when (it) {
                        is RegisterUiState.RegisterSuccess -> {
                            binding.loading.visibility = View.GONE
                            findNavController().popBackStack()
                        }
                        is RegisterUiState.Error -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.exception.message ?: "网络异常/异常信息为空",
                                Toast.LENGTH_SHORT
                            ).show()
//                            findNavController().navigate(R.id.navigation_login)
                        }
                        is RegisterUiState.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is RegisterUiState.None -> {
                        }
                    }
                }
            }
        }

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
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.phone.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}