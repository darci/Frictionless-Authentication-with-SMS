package io.github.darci.smsauthentication.domain.usecase

import io.github.darci.smsauthentication.domain.model.PhoneValidationResult

/**
 * Caso de uso para validar um número de telefone.
 * Contém a regra de negócio de validação, isolada da UI.
 */
class ValidatePhoneNumberUseCase {

    operator fun invoke(phoneNumber: String): PhoneValidationResult {
        if (phoneNumber.isBlank()) {
            return PhoneValidationResult.Invalid("O número de telefone não pode estar vazio")
        }

        // Permite apenas dígitos e prefixo '+'
        if (!phoneNumber.all { it.isDigit() || it == '+' }) {
            return PhoneValidationResult.Invalid("O número contém caracteres inválidos")
        }

        if (phoneNumber.length > 16) {
            return PhoneValidationResult.Invalid("O número de telefone é muito longo")
        }

        if (phoneNumber.length < 10) {
            return PhoneValidationResult.Invalid("O número de telefone é muito curto")
        }

        return PhoneValidationResult.Valid
    }
}
