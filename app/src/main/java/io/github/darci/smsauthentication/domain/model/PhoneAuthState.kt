package io.github.darci.smsauthentication.domain.model

/**
 * Representa o estado da autenticação por telefone.
 */
data class PhoneAuthState(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val isSmsSent: Boolean = false,
    val errorMessage: String? = null
)
