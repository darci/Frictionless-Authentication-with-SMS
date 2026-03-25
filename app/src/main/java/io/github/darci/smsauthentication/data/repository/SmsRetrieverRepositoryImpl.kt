package io.github.darci.smsauthentication.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.github.darci.smsauthentication.domain.repository.SmsRetrieverRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementação concreta do [SmsRetrieverRepository] usando SMS Retriever API.
 *
 * Fluxo:
 *  1. Inicia o SmsRetriever client
 *  2. Registra um BroadcastReceiver para escutar o SMS
 *  3. Extrai o código OTP de 6 dígitos do corpo do SMS
 *  4. Emite o código via Flow
 */
class SmsRetrieverRepositoryImpl(
    private val context: Context
) : SmsRetrieverRepository {

    companion object {
        private const val TAG = "SmsRetrieverRepo"
        // Regex para capturar 6 dígitos consecutivos
        private val OTP_REGEX = Regex("(\\d{6})")
    }

    override fun startSmsRetriever(): Flow<Result<String>> = callbackFlow {
        val client = SmsRetriever.getClient(context)

        // Inicia o SMS Retriever
        client.startSmsRetriever()
            .addOnSuccessListener {
                Log.d(TAG, "SMS Retriever iniciado com sucesso")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Falha ao iniciar SMS Retriever", e)
                trySend(Result.failure(e))
            }

        // BroadcastReceiver para capturar o SMS
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return

                val extras = intent.extras ?: return
                val status = extras.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java) ?: return

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        Log.d(TAG, "SMS recebido: $message")

                        if (message != null) {
                            val otpCode = OTP_REGEX.find(message)?.value
                            if (otpCode != null) {
                                Log.d(TAG, "Código OTP extraído: $otpCode")
                                trySend(Result.success(otpCode))
                            } else {
                                trySend(Result.failure(Exception("Código OTP não encontrado no SMS")))
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d(TAG, "SMS Retriever timeout")
                        trySend(Result.failure(Exception("Timeout ao aguardar SMS")))
                    }
                }
            }
        }

        // Registra o receiver
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(
            context,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        // Limpeza ao cancelar o Flow
        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                Log.w(TAG, "Receiver já desregistrado", e)
            }
        }
    }

    override suspend fun getAppHash(): String {
        return suspendCoroutine { continuation ->
            val client = SmsRetriever.getClient(context)
            client.startSmsRetriever()
                .addOnSuccessListener {
                    // O hash do app é gerado com base no package name e no signing certificate
                    // Para obtê-lo, use o helper AppSignatureHelper (veja documentação)
                    continuation.resume(context.packageName)
                }
                .addOnFailureListener {
                    continuation.resume(context.packageName)
                }
        }
    }
}
