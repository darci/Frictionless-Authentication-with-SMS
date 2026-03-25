package io.github.darci.smsauthentication.presentation.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Tela de verificação do código OTP.
 */
@Composable
fun OtpVerificationScreen(
    viewModel: OtpViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    OtpVerificationContent(
        uiState = uiState,
        onOtpCodeChanged = { viewModel.onEvent(OtpEvent.OtpCodeChanged(it)) },
        onVerifyClicked = { viewModel.onEvent(OtpEvent.VerifyClicked) },
        onResendClicked = { viewModel.onEvent(OtpEvent.ResendClicked) },
        modifier = modifier
    )
}

/**
 * Conteúdo puro (stateless) da tela de OTP.
 */
@Composable
private fun OtpVerificationContent(
    uiState: OtpUiState,
    onOtpCodeChanged: (String) -> Unit,
    onVerifyClicked: () -> Unit,
    onResendClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verificação de Código",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Digite o código de 6 dígitos enviado para",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = uiState.phoneNumber,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.otpCode,
            onValueChange = onOtpCodeChanged,
            label = { Text("Código de verificação") },
            placeholder = { Text("000000") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            isError = uiState.errorMessage != null,
            supportingText = uiState.errorMessage?.let { error ->
                { Text(text = error, color = MaterialTheme.colorScheme.error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerifyClicked,
            enabled = uiState.otpCode.isNotEmpty() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (uiState.isLoading) "Verificando..." else "Verificar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onResendClicked) {
            Text(text = "Reenviar código")
        }

        if (uiState.isVerified) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "✅ Código verificado com sucesso!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
