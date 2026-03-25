package io.github.darci.smsauthentication.data.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays

/**
 * Utilitário para calcular o hash do app necessário pelo SMS Retriever API.
 *
 * O hash é composto a partir do package name + certificado de assinatura do APK.
 * Deve ser incluído no final do SMS de verificação.
 *
 * ⚠️ Use apenas em debug para descobrir o hash.
 * Em produção, o hash é fixo e deve ser configurado no backend.
 */
object AppSignatureHelper {

    private const val TAG = "AppSignatureHelper"
    private const val HASH_TYPE = "SHA-256"
    private const val NUM_HASHED_BYTES = 9
    private const val NUM_BASE64_CHAR = 11

    fun getAppSignatures(context: Context): List<String> {
        val signatures = mutableListOf<String>()

        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signingInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo ?: return signatures

            val signers = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }

            for (signature in signers) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    signatures.add(hash)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao obter assinaturas do app", e)
        }

        Log.d(TAG, "App hashes: $signatures")
        return signatures
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // Trunca para os primeiros NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)

            // Codifica em base64
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            return base64Hash
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao gerar hash", e)
        }
        return null
    }
}
