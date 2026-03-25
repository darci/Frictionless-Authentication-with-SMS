package io.github.darci.smsauthentication.domain.usecase

import io.github.darci.smsauthentication.domain.repository.SmsRetrieverRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para iniciar a escuta de SMS e extrair o código OTP automaticamente.
 */
class ListenForOtpUseCase(
    private val repository: SmsRetrieverRepository
) {
    operator fun invoke(): Flow<Result<String>> {
        return repository.startSmsRetriever()
    }
}
