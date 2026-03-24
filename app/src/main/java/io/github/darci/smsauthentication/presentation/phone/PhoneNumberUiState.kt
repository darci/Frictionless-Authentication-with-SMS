package io.github.darci.smsauthentication.presentation.phone

import io.github.darci.smsauthentication.domain.model.PhoneAuthState

/**
 * Estado da UI da tela de número de telefone.
 * Wrapper sobre o [PhoneAuthState] do domínio, adicionando campos específicos de apresentação.
 */
data class PhoneNumberUiState(
    val phoneNumber: String = "",
    val isPhoneValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSmsSent: Boolean = false,
    val errorMessage: String? = null
)
