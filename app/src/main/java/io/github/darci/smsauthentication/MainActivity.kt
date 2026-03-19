package io.github.darci.smsauthentication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import io.github.darci.smsauthentication.ui.theme.FrictionlessAuthenticationWithSMSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrictionlessAuthenticationWithSMSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PhoneNumberScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneNumberScreen(modifier: Modifier = Modifier) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Launcher que recebe o resultado do Phone Hint picker
    val phoneHintLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            // Extrai o número de telefone do resultado
            val phoneNumberFromHint = Identity
                .getSignInClient(context)
                .getPhoneNumberFromIntent(result.data)
            phoneNumber = phoneNumberFromHint
            Log.d("PhoneHint", "Número selecionado: $phoneNumberFromHint")
        } catch (e: Exception) {
            Log.e("PhoneHint", "Falha ao obter número de telefone", e)
        }
    }

    // Efeito lançado uma vez ao exibir a tela – solicita o Phone Hint automaticamente
    LaunchedEffect(Unit) {
        val request = GetPhoneNumberHintIntentRequest.builder().build()

        Identity.getSignInClient(context)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { pendingIntent ->
                val intentSenderRequest = IntentSenderRequest
                    .Builder(pendingIntent.intentSender)
                    .build()
                phoneHintLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e("PhoneHint", "Phone Hint não disponível", e)
            }
    }

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
            value = phoneNumber,
            onValueChange = { newValue ->
                // Permite dígitos e o prefixo '+' (para formato internacional)
                if (newValue.all { it.isDigit() || it == '+' } && newValue.length <= 16) {
                    phoneNumber = newValue
                }
            },
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
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // TODO: Implementar envio de SMS
            },
            enabled = phoneNumber.length >= 10,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enviar SMS")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhoneNumberScreenPreview() {
    FrictionlessAuthenticationWithSMSTheme {
        PhoneNumberScreen()
    }
}