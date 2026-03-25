package io.github.darci.smsauthentication.presentation.otp

/**
 * Eventos da UI da tela de verificação OTP.
 */
sealed class OtpEvent {
    /** Usuário alterou o código OTP. */
    data class OtpCodeChanged(val code: String) : OtpEvent()

    /** Usuário clicou em "Verificar". */
    data object VerifyClicked : OtpEvent()

    /** Usuário clicou em "Reenviar código". */
    data object ResendClicked : OtpEvent()
}
