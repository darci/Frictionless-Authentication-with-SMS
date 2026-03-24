package io.github.darci.smsauthentication.domain.model

/**
 * Resultado da validação de um número de telefone.
 */
sealed class PhoneValidationResult {
    data object Valid : PhoneValidationResult()
    data class Invalid(val reason: String) : PhoneValidationResult()
}
