package io.github.darci.smsauthentication.domain.repository

import android.content.IntentSender

/**
 * Abstração do repositório para obter o Phone Hint do Google Play Services.
 * A interface pertence à camada de domínio; a implementação fica na camada de dados.
 */
interface PhoneHintRepository {
    /**
     * Solicita o IntentSender do Phone Hint API.
     * @return [Result] contendo o [IntentSender] em caso de sucesso, ou a exceção em caso de falha.
     */
    suspend fun requestPhoneHintIntent(): Result<IntentSender>

    /**
     * Extrai o número de telefone do Intent retornado pelo Phone Hint picker.
     * @param intentData O Intent retornado pelo picker.
     * @return O número de telefone selecionado.
     */
    fun extractPhoneNumber(intentData: android.content.Intent?): Result<String>
}
