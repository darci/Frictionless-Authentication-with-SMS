package io.github.darci.smsauthentication.domain.usecase

import android.content.IntentSender
import io.github.darci.smsauthentication.domain.repository.PhoneHintRepository

/**
 * Caso de uso para solicitar o Phone Hint do Google Play Services.
 * Encapsula a interação com o repositório.
 */
class RequestPhoneHintUseCase(
    private val repository: PhoneHintRepository
) {
    suspend operator fun invoke(): Result<IntentSender> {
        return repository.requestPhoneHintIntent()
    }
}
