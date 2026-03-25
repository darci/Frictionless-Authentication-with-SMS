package io.github.darci.smsauthentication.presentation.otp

/**
 * Estado da UI da tela de verificação OTP.
 */
data class OtpUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val errorMessage: String? = null
)
