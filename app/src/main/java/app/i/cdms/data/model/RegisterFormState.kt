package app.i.cdms.data.model

/**
 * Data validation state of the register form.
 */
data class RegisterFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val phoneError: Int? = null,
    val isDataValid: Boolean = false
)