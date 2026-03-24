package io.github.darci.smsauthentication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.darci.smsauthentication.presentation.phone.PhoneNumberScreen
import io.github.darci.smsauthentication.presentation.phone.PhoneNumberViewModel
import io.github.darci.smsauthentication.presentation.phone.PhoneNumberViewModelFactory
import io.github.darci.smsauthentication.ui.theme.FrictionlessAuthenticationWithSMSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrictionlessAuthenticationWithSMSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: PhoneNumberViewModel = viewModel(
                        factory = PhoneNumberViewModelFactory(applicationContext)
                    )
                    PhoneNumberScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}