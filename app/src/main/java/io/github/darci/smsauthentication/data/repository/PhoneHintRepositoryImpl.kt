package io.github.darci.smsauthentication.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import io.github.darci.smsauthentication.domain.repository.PhoneHintRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementação concreta do [PhoneHintRepository] usando Google Play Services Identity API.
 * Pertence à camada de dados — depende diretamente de frameworks Android/Google.
 */
class PhoneHintRepositoryImpl(
    private val context: Context
) : PhoneHintRepository {

    private val signInClient by lazy { Identity.getSignInClient(context) }

    override suspend fun requestPhoneHintIntent(): Result<IntentSender> {
        return suspendCoroutine { continuation ->
            val request = GetPhoneNumberHintIntentRequest.builder().build()

            signInClient
                .getPhoneNumberHintIntent(request)
                .addOnSuccessListener { pendingIntent ->
                    continuation.resume(Result.success(pendingIntent.intentSender))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }
    }

    override fun extractPhoneNumber(intentData: Intent?): Result<String> {
        return try {
            val phoneNumber = signInClient.getPhoneNumberFromIntent(intentData)
            Result.success(phoneNumber)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
