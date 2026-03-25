package io.github.darci.smsauthentication.presentation.phone

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.darci.smsauthentication.domain.model.PhoneValidationResult
import io.github.darci.smsauthentication.domain.usecase.ExtractPhoneNumberUseCase
import io.github.darci.smsauthentication.domain.usecase.RequestPhoneHintUseCase
import io.github.darci.smsauthentication.domain.usecase.ValidatePhoneNumberUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de autenticação por telefone.
 *
 * Responsabilidades:
 *  - Gerenciar o [PhoneNumberUiState] (fluxo de estado unidirecional)
 *  - Processar [PhoneNumberEvent]s vindos da UI
 *  - Emitir [PhoneNumberEffect]s (efeitos colaterais como lançar o picker)
 *  - Delegar lógica de negócio aos Use Cases
 */
class PhoneNumberViewModel(
    private val requestPhoneHintUseCase: RequestPhoneHintUseCase,
    private val extractPhoneNumberUseCase: ExtractPhoneNumberUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "PhoneNumberViewModel"
    }

    private val _uiState = MutableStateFlow(PhoneNumberUiState())
    val uiState: StateFlow<PhoneNumberUiState> = _uiState.asStateFlow()

    private val _effect = Channel<PhoneNumberEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Ponto de entrada para todos os eventos da UI.
     */
    fun onEvent(event: PhoneNumberEvent) {
        when (event) {
            is PhoneNumberEvent.PhoneNumberChanged -> onPhoneNumberChanged(event.phoneNumber)
            is PhoneNumberEvent.SendSmsClicked -> onSendSmsClicked()
            is PhoneNumberEvent.RequestPhoneHint -> onRequestPhoneHint()
            is PhoneNumberEvent.PhoneHintResult -> onPhoneHintResult(event)
            is PhoneNumberEvent.ErrorDismissed -> onErrorDismissed()
        }
    }

    private fun onPhoneNumberChanged(newNumber: String) {
        // Filtra caracteres inválidos antes de validar
        val filtered = newNumber.filter { it.isDigit() || it == '+' }.take(16)
        val isValid = validatePhoneNumberUseCase(filtered) is PhoneValidationResult.Valid

        _uiState.update {
            it.copy(
                phoneNumber = filtered,
                isPhoneValid = isValid,
                errorMessage = null
            )
        }
    }

    private fun onSendSmsClicked() {
        val currentNumber = _uiState.value.phoneNumber

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // TODO: Implementar envio real de SMS via repositório
            Log.d(TAG, "Enviando SMS para: $currentNumber")
            _uiState.update { it.copy(isLoading = false, isSmsSent = true) }
            _effect.send(PhoneNumberEffect.NavigateToOtp(currentNumber))
        }
    }

    private fun onRequestPhoneHint() {
        viewModelScope.launch {
            requestPhoneHintUseCase()
                .onSuccess { intentSender ->
                    _effect.send(PhoneNumberEffect.LaunchPhoneHint(intentSender))
                }
                .onFailure { e ->
                    Log.e(TAG, "Phone Hint não disponível", e)
                }
        }
    }

    private fun onPhoneHintResult(event: PhoneNumberEvent.PhoneHintResult) {
        extractPhoneNumberUseCase(event.intentData)
            .onSuccess { number ->
                Log.d(TAG, "Número selecionado: $number")
                onPhoneNumberChanged(number)
                // Navega automaticamente para a tela de OTP após selecionar o número
                onSendSmsClicked()
            }
            .onFailure { e ->
                Log.e(TAG, "Falha ao obter número de telefone", e)
            }
    }

    private fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
