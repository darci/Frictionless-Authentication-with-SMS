package io.github.darci.smsauthentication.presentation.otp

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.darci.smsauthentication.data.repository.SmsRetrieverRepositoryImpl
import io.github.darci.smsauthentication.domain.usecase.ListenForOtpUseCase

/**
 * Factory para o [OtpViewModel].
 * Necessária para injetar o [ListenForOtpUseCase] e o [SavedStateHandle].
 */
class OtpViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        val repository = SmsRetrieverRepositoryImpl(context.applicationContext)
        val listenForOtpUseCase = ListenForOtpUseCase(repository)

        return OtpViewModel(
            savedStateHandle = savedStateHandle,
            listenForOtpUseCase = listenForOtpUseCase
        ) as T
    }
}
