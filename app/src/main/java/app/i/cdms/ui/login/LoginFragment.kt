package app.i.cdms.ui.login

import android.graphics.BitmapFactory
import androidx.lifecycle.Observer
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import app.i.cdms.databinding.FragmentLoginBinding

import app.i.cdms.R
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Token
import coil.load
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var captchaData: CaptchaData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val captchaEditText = binding.captcha
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val captchaImageView = binding.imageView
        val result = binding.textView

        loginViewModel.getCaptcha()
        captchaImageView.setOnClickListener {
            loginViewModel.getCaptcha()
        }
        loginViewModel.captchaResult.observe(viewLifecycleOwner, {
            it.data ?: return@observe
            captchaData = it.data
            result.text = it.msg
            val imageByteArray = Base64.decode(it.data.imgBytes, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            captchaImageView.load(bitmap) {
                crossfade(true)
            }
        })

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
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

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                when (loginResult.code) {
                    200 -> {
                        loginResult.data?.let {
                            loginViewModel.updateToken(loginResult.data)
                        }
                        findNavController().popBackStack()
                    }
                    500 -> {
                        showLoginFailed(loginResult.msg)
                        loginViewModel.getCaptcha()
                    }
                    else -> {
                        showLoginFailed("TAG" + "loginResult.code else")
                        loginViewModel.getCaptcha()
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
                    captchaEditText.text.toString().toInt(),
                    captchaData.uuid
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                captchaEditText.text.toString().toInt(),
                captchaData.uuid
            )
        }
    }

    private fun showLoginFailed(error: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, error, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}