package io.github.darci.smsauthentication.presentation.phone

import android.content.Intent
import android.content.IntentSender

/**
 * Eventos da UI que o PhoneNumberScreen pode disparar para o ViewModel.
 * Segue o padrão unidirecional de dados (UDF).
 */
sealed class PhoneNumberEvent {
    /** Usuário alterou o campo de texto do telefone. */
    data class PhoneNumberChanged(val phoneNumber: String) : PhoneNumberEvent()

    /** Usuário clicou em "Enviar SMS". */
    data object SendSmsClicked : PhoneNumberEvent()

    /** A tela foi exibida e deve solicitar o Phone Hint. */
    data object RequestPhoneHint : PhoneNumberEvent()

    /** O resultado do Phone Hint picker foi recebido. */
    data class PhoneHintResult(val intentData: Intent?) : PhoneNumberEvent()

    /** Dismiss de mensagem de erro. */
    data object ErrorDismissed : PhoneNumberEvent()
}

/**
 * Efeitos colaterais únicos (one-shot) que o ViewModel emite para a UI.
 */
sealed class PhoneNumberEffect {
    /** Solicita à UI que lance o IntentSender do Phone Hint picker. */
    data class LaunchPhoneHint(val intentSender: IntentSender) : PhoneNumberEffect()

    /** Navegar para a tela de verificação OTP com o número de telefone. */
    data class NavigateToOtp(val phoneNumber: String) : PhoneNumberEffect()
}
