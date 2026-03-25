package io.github.darci.smsauthentication.presentation.otp

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel da tela de verificação OTP.
 */
class OtpViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "OtpViewModel"
        private const val OTP_LENGTH = 6
    }

    private val phoneNumber: String = savedStateHandle["phoneNumber"] ?: ""

    private val _uiState = MutableStateFlow(OtpUiState(phoneNumber = phoneNumber))
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.OtpCodeChanged -> onOtpCodeChanged(event.code)
            is OtpEvent.VerifyClicked -> onVerifyClicked()
            is OtpEvent.ResendClicked -> onResendClicked()
        }
    }

    private fun onOtpCodeChanged(code: String) {
        // Aceita apenas dígitos, limitado ao comprimento do OTP
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
    }
}
