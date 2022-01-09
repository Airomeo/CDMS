package app.i.cdms.ui.register

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import app.i.cdms.R
import app.i.cdms.databinding.DialogRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterDialogFragment : DialogFragment() {

    private var _binding: DialogRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRegisterBinding.inflate(layoutInflater, null, false)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_register)
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_positive_text, null)
            .setNegativeButton(R.string.dialog_negative_text) { dialog, which ->
                dialog.dismiss()
            }
            .create().apply {
                // don't close dialog
                // https://stackoverflow.com/a/65165514/10276438
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        registerViewModel.register(
                            binding.username.text.toString(),
                            binding.password.text.toString(),
                            binding.phone.text.toString()
                        )
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

        // DialogFragment lifecycleOwner
        // https://developer.android.com/guide/fragments/dialogs#lifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registerFormState.collectLatest { registerFormState ->
                    registerFormState ?: return@collectLatest
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                        registerFormState.isDataValid
                    binding.textInputLayout.error = registerFormState.usernameError?.let {
                        getString(it)
                    }
                    binding.textInputLayout2.error = registerFormState.passwordError?.let {
                        getString(it)
                    }
                    binding.textInputLayout3.error = registerFormState.phoneError?.let {
                        getString(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registerResult.collectLatest {
                    it ?: return@collectLatest
                    if (it.code == 200) {
                        Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}