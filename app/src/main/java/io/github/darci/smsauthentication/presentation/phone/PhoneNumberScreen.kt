package io.github.darci.smsauthentication.presentation.phone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Tela de número de telefone — camada de apresentação (View no MVVM).
 *
 * Responsabilidades:
 *  - Renderizar o [PhoneNumberUiState]
 *  - Disparar [PhoneNumberEvent]s para o [PhoneNumberViewModel]
 *  - Reagir a [PhoneNumberEffect]s (ex.: lançar o Phone Hint picker, navegar)
 */
@Composable
fun PhoneNumberScreen(
    viewModel: PhoneNumberViewModel,
    onNavigateToOtp: (phoneNumber: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Launcher que recebe o resultado do Phone Hint picker
    val phoneHintLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.onEvent(PhoneNumberEvent.PhoneHintResult(result.data))
    }

    // Observa efeitos colaterais (one-shot)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PhoneNumberEffect.LaunchPhoneHint -> {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(effect.intentSender)
                        .build()
                    phoneHintLauncher.launch(intentSenderRequest)
                }
                is PhoneNumberEffect.NavigateToOtp -> {
                    onNavigateToOtp(effect.phoneNumber)
                }
            }
        }
    }

    // Solicita Phone Hint automaticamente ao exibir a tela
    LaunchedEffect(Unit) {
        viewModel.onEvent(PhoneNumberEvent.RequestPhoneHint)
    }

    PhoneNumberContent(
        uiState = uiState,
        onPhoneNumberChanged = { viewModel.onEvent(PhoneNumberEvent.PhoneNumberChanged(it)) },
        onSendSmsClicked = { viewModel.onEvent(PhoneNumberEvent.SendSmsClicked) },
        modifier = modifier
    )
}

/**
 * Conteúdo puro (stateless) da tela — facilita previews e testes.
 */
@Composable
private fun PhoneNumberContent(
    uiState: PhoneNumberUiState,
    onPhoneNumberChanged: (String) -> Unit,
    onSendSmsClicked: () -> Unit,
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
            text = "Autenticação por SMS",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Digite seu número de telefone para receber o código de verificação",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChanged,
            label = { Text("Número de telefone") },
            placeholder = { Text("Ex: +5511999998888") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Ícone de telefone"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
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
            onClick = onSendSmsClicked,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (uiState.isLoading) "Enviando..." else "Enviar SMS")
        }
    }
}
