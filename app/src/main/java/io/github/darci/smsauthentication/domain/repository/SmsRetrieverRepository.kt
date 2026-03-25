package io.github.darci.smsauthentication.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Abstração do repositório para captura de SMS via SMS Retriever API.
 */
interface SmsRetrieverRepository {
    /**
     * Inicia o listener do SMS Retriever e retorna um Flow que emite o código OTP
     * quando o SMS é recebido.
     */
    fun startSmsRetriever(): Flow<Result<String>>

    /**
     * Retorna o app hash necessário para compor o SMS de teste.
     */
    suspend fun getAppHash(): String
}
