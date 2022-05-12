package app.i.cdms.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import app.i.cdms.BuildConfig
import app.i.cdms.R
import app.i.cdms.ui.theme.AppTheme
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
//    private val viewModel: AuthViewModel by viewModels()
//    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(context = context) {
                    Surface {
                        SignInAndSignUpScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SignInAndSignUpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    if (viewModel.openDialog) {
        RetrievePasswordDialog(
            onDismiss = { viewModel.openDialog = false },
            onPositive = {
                if (it.length != 11) {
                    Toast.makeText(context, R.string.invalid_phone, Toast.LENGTH_SHORT).show()
                    return@RetrievePasswordDialog
                }
                viewModel.retrievePassword(it)
            })
    }

    var username by remember { mutableStateOf(if (BuildConfig.DEBUG && viewModel.isSignIn) "朱朝阳" else "") }
    var password by remember { mutableStateOf(if (BuildConfig.DEBUG && viewModel.isSignIn) "bonjour" else "") }
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
    var captchaText by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxSize(0.08F))
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
        if (viewModel.isSignIn) {
            OutlinedTextField(
                value = captchaText,
                onValueChange = { captchaText = it.take(2) },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                maxLines = 1,
                label = { Text(text = stringResource(id = R.string.prompt_captcha)) },
                trailingIcon = {
                    val painter = rememberAsyncImagePainter(
                        model = viewModel.captchaBitmap,
                        placeholder = painterResource(
                            id = R.drawable.ic_baseline_refresh_24
                        ),
                        error = painterResource(
                            id = R.drawable.ic_baseline_refresh_24
                        ),
                        fallback = painterResource(
                            id = R.drawable.ic_baseline_refresh_24
                        )
                    )
                    Image(
                        painter = painter,
                        contentDescription = stringResource(id = R.string.prompt_captcha),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            // Set image size to 40 dp
                            .size(128.dp, 48.dp) // scale 8:3
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { viewModel.fetchLoginCaptcha() }
                    )
                }
            )
//            ClickableText(
//                text = AnnotatedString(stringResource(id = R.string.trouble_signing_in)),
//                style = TextStyle(color = MaterialTheme.colorScheme.primary),
//                onClick = { offset -> },
//                modifier = Modifier.padding(16.dp, 16.dp, 160.dp, 0.dp)
//            )
        } else {
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
                label = { Text(text = stringResource(id = R.string.prompt_captcha)) },
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                value = inviteCode,
                onValueChange = { inviteCode = it.take(12) },
                label = { Text(text = stringResource(id = R.string.prompt_invitation_code)) })
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp),
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
        }
        Row(modifier = Modifier.padding(horizontal = 48.dp)) {
            Button(
                onClick = {
                    if (viewModel.isSignIn) {
                        if (username.isEmpty() || username.length > 8) {
                            Toast.makeText(context, R.string.invalid_username, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (password.length < 6 || password.length > 16) {
                            Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (captchaText.isBlank()) {
                            Toast.makeText(context, R.string.invalid_captcha, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        viewModel.login(username, password, captchaText.toInt())
                    } else {
                        viewModel.isSignIn = !viewModel.isSignIn
                    }
                },
                modifier = (if (viewModel.isSignIn) Modifier.weight(1F) else Modifier)
                    .padding(
                        start = 0.dp,
                        top = 16.dp,
                        end = 8.dp,
                        bottom = 0.dp
                    ),
                elevation = if (viewModel.isSignIn) ButtonDefaults.buttonElevation() else ButtonDefaults.elevatedButtonElevation(),
                colors = if (viewModel.isSignIn) ButtonDefaults.buttonColors() else ButtonDefaults.elevatedButtonColors(),
            ) {
                Text(
                    text = stringResource(id = R.string.title_sign_in),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = {
                    if (viewModel.isSignIn) {
                        viewModel.isSignIn = !viewModel.isSignIn
                    } else {
                        if (username.isEmpty() || username.length > 8) {
                            Toast.makeText(context, R.string.invalid_username, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (password.length < 6 || password.length > 16) {
                            Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (phone.length != 11) {
                            Toast.makeText(context, R.string.invalid_phone, Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (phoneCaptcha.length != 6) {
                            Toast.makeText(context, R.string.invalid_captcha, Toast.LENGTH_SHORT)
                                .show()
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
                    }
                },
                modifier = (if (viewModel.isSignIn) Modifier else Modifier.weight(1F))
                    .padding(
                        start = 8.dp,
                        top = 16.dp,
                        end = 0.dp,
                        bottom = 0.dp
                    ),
                elevation = if (viewModel.isSignIn) ButtonDefaults.elevatedButtonElevation() else ButtonDefaults.buttonElevation(),
                colors = if (viewModel.isSignIn) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.buttonColors(),
            ) {
                Text(
                    text = stringResource(id = R.string.action_sign_up),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        if (viewModel.isSignIn) {
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.trouble_signing_in)),
                style = TextStyle(color = MaterialTheme.colorScheme.primary),
                onClick = { viewModel.openDialog = true },
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 64.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun RetrievePasswordDialog(
    onDismiss: () -> Unit,
    onPositive: (phone: String) -> Unit,
) {
    var phone by remember { mutableStateOf("") }
    var focusManager = LocalFocusManager.current
    Dialog(onDismissRequest = {
        focusManager.clearFocus(true)
        onDismiss()
    }) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 18.dp)
            ) {
                focusManager = LocalFocusManager.current
                Box(
                    // Align the title to the center when an icon is present.
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = stringResource(id = R.string.retrieve_password_dialog_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Box(
                    Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(bottom = 18.dp)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = stringResource(id = R.string.retrieve_password_dialog_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.take(11) },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = { Text(text = stringResource(id = R.string.prompt_phone_holder)) },
                    label = { Text(text = stringResource(id = R.string.prompt_phone)) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onDismiss()
                    }) {
                        Text(stringResource(id = R.string.dialog_negative_text))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onPositive(phone)
                    }) {
                        Text(stringResource(id = R.string.dialog_positive_text))
                    }
                }
            }
        }
    }
}