package app.i.cdms.ui.login

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
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
import app.i.cdms.databinding.FragmentLoginBinding
import app.i.cdms.ui.main.MainViewModel
import coil.load
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val captchaEditText = binding.captcha
        val loginButton = binding.login
        val captchaImageView = binding.imageView

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collectLatest {
                    when (it) {
                        is LoginUiState.GetCaptchaSuccessful -> {
                            binding.loading.visibility = View.GONE
                            val imageByteArray =
                                Base64.decode(it.captchaData.imgBytes, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(
                                imageByteArray,
                                0,
                                imageByteArray.size
                            )
                            captchaImageView.load(bitmap) {
                                crossfade(true)
                            }
                        }
                        is LoginUiState.GetCaptchaFailed -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.apiResult.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            captchaImageView.setImageResource(R.drawable.ic_baseline_refresh_24)
                        }
                        is LoginUiState.LoginSuccessful -> {
                            binding.loading.visibility = View.GONE
                            mainViewModel.updateToken(it.token)
                            findNavController().popBackStack()
                        }
                        is LoginUiState.LoginFailed -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                it.apiResult.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            loginViewModel.getCaptcha()
                        }
                        is LoginUiState.Error -> {
                            binding.loading.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "网络异常。" + it.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is LoginUiState.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is LoginUiState.None -> {
                        }
                    }
                }
            }
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer { loginFormState ->
            loginFormState ?: return@Observer
            loginButton.isEnabled = loginFormState.isDataValid
            loginFormState.usernameError?.let {
                usernameEditText.error = getString(it)
            }
            loginFormState.passwordError?.let {
                passwordEditText.error = getString(it)
            }
            loginFormState.captchaError?.let {
                captchaEditText.error = getString(it)
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
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    captchaEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        captchaEditText.addTextChangedListener(afterTextChangedListener)
        captchaEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    captchaEditText.text.toString().toInt()
                )
            }
            false
        }

        captchaImageView.setOnClickListener {
            loginViewModel.getCaptcha()
        }

        loginButton.setOnClickListener {
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                captchaEditText.text.toString().toInt()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}