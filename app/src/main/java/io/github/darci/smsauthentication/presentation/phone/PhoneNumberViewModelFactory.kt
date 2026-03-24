package io.github.darci.smsauthentication.presentation.phone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.darci.smsauthentication.data.repository.PhoneHintRepositoryImpl
import io.github.darci.smsauthentication.domain.usecase.ExtractPhoneNumberUseCase
import io.github.darci.smsauthentication.domain.usecase.RequestPhoneHintUseCase
import io.github.darci.smsauthentication.domain.usecase.ValidatePhoneNumberUseCase

/**
 * Factory manual para o [PhoneNumberViewModel].
 *
 * Quando o projeto adotar um framework de DI (Hilt/Koin), esta classe pode ser substituída
 * pela injeção automática.
 */
class PhoneNumberViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = PhoneHintRepositoryImpl(context.applicationContext)

        return PhoneNumberViewModel(
            requestPhoneHintUseCase = RequestPhoneHintUseCase(repository),
            extractPhoneNumberUseCase = ExtractPhoneNumberUseCase(repository),
            validatePhoneNumberUseCase = ValidatePhoneNumberUseCase()
        ) as T
    }
}
