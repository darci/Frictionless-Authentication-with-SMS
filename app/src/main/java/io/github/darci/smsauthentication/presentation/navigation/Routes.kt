package io.github.darci.smsauthentication.presentation.navigation

/**
 * Definição das rotas de navegação do app.
 */
object Routes {
    const val PHONE_NUMBER = "phone_number"
    const val OTP_VERIFICATION = "otp_verification/{phoneNumber}"

    fun otpVerification(phoneNumber: String): String {
        return "otp_verification/$phoneNumber"
    }
}
