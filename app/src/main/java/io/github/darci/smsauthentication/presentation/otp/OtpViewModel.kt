package io.github.darci.smsauthentication.presentation.otp

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.darci.smsauthentication.domain.usecase.ListenForOtpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de verificação OTP.
 * Escuta SMS via SMS Retriever API para preenchimento automático do código.
 */
class OtpViewModel(
    savedStateHandle: SavedStateHandle,
    private val listenForOtpUseCase: ListenForOtpUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "OtpViewModel"
        private const val OTP_LENGTH = 6
    }

    private val phoneNumber: String = savedStateHandle["phoneNumber"] ?: ""

    private val _uiState = MutableStateFlow(OtpUiState(phoneNumber = phoneNumber))
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    init {
        startListeningForSms()
    }

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.OtpCodeChanged -> onOtpCodeChanged(event.code)
            is OtpEvent.VerifyClicked -> onVerifyClicked()
            is OtpEvent.ResendClicked -> onResendClicked()
        }
    }

    /**
     * Inicia a escuta do SMS Retriever para preenchimento automático.
     */
    private fun startListeningForSms() {
        viewModelScope.launch {
            listenForOtpUseCase().collect { result ->
                result
                    .onSuccess { otpCode ->
                        Log.d(TAG, "OTP recebido via SMS: $otpCode")
                        _uiState.update {
                            it.copy(otpCode = otpCode, errorMessage = null)
                        }
                        // Valida automaticamente ao receber o OTP via SMS
                        onVerifyClicked()
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Erro ao receber OTP via SMS", e)
                    }
            }
        }
    }

    private fun onOtpCodeChanged(code: String) {
        val filtered = code.filter { it.isDigit() }.take(OTP_LENGTH)
        _uiState.update {
            it.copy(otpCode = filtered, errorMessage = null)
        }
    }

    private fun onVerifyClicked() {
        val code = _uiState.value.otpCode
        if (code.length < OTP_LENGTH) {
            _uiState.update { it.copy(errorMessage = "O código deve ter $OTP_LENGTH dígitos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        // TODO: Implementar verificação real do OTP via repositório
        Log.d(TAG, "Verificando OTP '$code' para o número '${_uiState.value.phoneNumber}'")
        _uiState.update { it.copy(isLoading = false, isVerified = true) }
    }

    private fun onResendClicked() {
        // TODO: Implementar reenvio de SMS via repositório
        Log.d(TAG, "Reenviando SMS para '${_uiState.value.phoneNumber}'")
        // Reinicia o listener do SMS Retriever
        startListeningForSms()
    }
}
