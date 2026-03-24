package io.github.darci.smsauthentication.domain.usecase

import android.content.Intent
import io.github.darci.smsauthentication.domain.repository.PhoneHintRepository

/**
 * Caso de uso para extrair o número de telefone do resultado do Phone Hint picker.
 */
class ExtractPhoneNumberUseCase(
    private val repository: PhoneHintRepository
) {
    operator fun invoke(intentData: Intent?): Result<String> {
        return repository.extractPhoneNumber(intentData)
    }
}
