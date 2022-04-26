package app.i.cdms.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (viewModel.registerState) {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.loginFragment)
                }
                AppTheme(context = context) {
                    Surface {
                        RegisterAndLogin()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
)
@Composable
fun RegisterAndLogin(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phoneCaptcha by remember { mutableStateOf("") }
    val countDownTime = viewModel.countDownTime
    val countDownText = with(countDownTime) {
        if (countDownTime <= 0) {
            stringResource(id = R.string.prompt_captcha_get)
        } else {
            stringResource(id = R.string.prompt_captcha_phone_retry, this / 1000)
        }
    }
    var inviteCode by remember { mutableStateOf("") }
    var checkedState by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.take(10) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                imeAction = ImeAction.Next
            ),
            maxLines = 1,
            placeholder = { Text(text = stringResource(id = R.string.prompt_account_holder)) },
            label = { Text(text = stringResource(id = R.string.prompt_account)) })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it.take(16) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                imeAction = ImeAction.Next
            ),
            maxLines = 1,
            placeholder = { Text(text = stringResource(id = R.string.prompt_password_holder)) },
            label = { Text(text = stringResource(id = R.string.prompt_password)) })
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.take(11) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            maxLines = 1,
            placeholder = { Text(text = stringResource(id = R.string.prompt_phone_holder)) },
            label = { Text(text = stringResource(id = R.string.prompt_phone)) })
        OutlinedTextField(
            value = phoneCaptcha,
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            onValueChange = { phoneCaptcha = it.take(6) },
            label = { Text(text = stringResource(id = R.string.prompt_captcha_phone)) },
            maxLines = 1,
            trailingIcon = {
                ClickableText(
                    text = AnnotatedString(text = countDownText),
                    modifier = Modifier.padding(
                        start = 0.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    onClick = {
                        if (countDownTime <= 0 && phone.length == 11) {
                            viewModel.fetchRegisterCaptcha(phone)
                            viewModel.fetchLordInviteCode()
                        }
                    },
                    style = TextStyle(
                        color = if (countDownTime <= 0 && phone.length == 11) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        textAlign = TextAlign.Center
                    )
                )
            }
        )
        OutlinedTextField(
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                imeAction = ImeAction.Next
            ),
            value = inviteCode,
            onValueChange = { inviteCode = it.take(12) },
            label = { Text(text = stringResource(id = R.string.prompt_invitation_code)) })
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            Checkbox(checked = checkedState, onCheckedChange = { checkedState = it })
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                    append(stringResource(id = R.string.prompt_agree))
                }

                pushStringAnnotation(tag = "policy", annotation = "https://google.com/policy")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(id = R.string.prompt_privacy_policy))
                }
                pop()

                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                    append(stringResource(id = R.string.prompt_agree_and))
                }

                pushStringAnnotation(tag = "terms", annotation = "https://google.com/terms")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(id = R.string.prompt_terms_of_service))
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "policy",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        ContextCompat.startActivity(context, intent, null)
                        return@ClickableText
                    }
                    annotatedString.getStringAnnotations(
                        tag = "terms",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                        ContextCompat.startActivity(context, intent, null)
                        return@ClickableText
                    }
                    checkedState = checkedState.not()
                })
        }
        Button(
            onClick = {
                if (username.isEmpty() || username.length > 8) {
                    Toast.makeText(context, R.string.invalid_username, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password.length < 6 || password.length > 16) {
                    Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (phone.length != 11) {
                    Toast.makeText(context, R.string.invalid_phone, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (phoneCaptcha.length != 6) {
                    Toast.makeText(context, R.string.invalid_captcha, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!checkedState) {
                    val str = context.getString(R.string.prompt_agree_tips) +
                            context.getString(R.string.prompt_privacy_policy) +
                            context.getString(R.string.prompt_agree_and) +
                            context.getString(R.string.prompt_terms_of_service)
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.register(username, password, phone, phoneCaptcha, inviteCode)
            },
        ) {
            Text(
                text = stringResource(id = R.string.action_register),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}