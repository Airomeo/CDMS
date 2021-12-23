package app.i.cdms.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.i.cdms.R
import app.i.cdms.databinding.FragmentRegisterBinding
import app.i.cdms.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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
                            Toast.makeText(
                                requireContext(),
                                it.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is RegisterUiState.UpdateChannelSuccess -> {
                            Toast.makeText(
                                requireContext(),
                                it.msg,
                                Toast.LENGTH_SHORT
                            ).show()
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
        binding.tvFirstCommission.text =
            getString(R.string.first_commission, binding.sldFirstCommission.value.toString())
        binding.tvAdditionalCommission.text =
            getString(
                R.string.additional_commission,
                binding.sldAdditionalCommission.value.toString()
            )
        binding.sldFirstCommission.addOnChangeListener { slider, value, fromUser ->
            binding.tvFirstCommission.text = getString(R.string.first_commission, value.toString())
        }
        binding.sldAdditionalCommission.addOnChangeListener { slider, value, fromUser ->
            binding.tvAdditionalCommission.text =
                getString(R.string.additional_commission, value.toString())
        }
        binding.button.setOnClickListener {
            registerViewModel.register(
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.phone.text.toString()
            )
        }
        binding.btnRegisterAndSetChannel.setOnClickListener {
            registerViewModel.registerAndSetChannel(
                binding.username.text.toString(),
                binding.password.text.toString(),
                binding.phone.text.toString(),
                binding.sldFirstCommission.value,
                binding.sldAdditionalCommission.value
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}