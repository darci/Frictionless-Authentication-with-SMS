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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.darci.smsauthentication.presentation.navigation.Routes
import io.github.darci.smsauthentication.presentation.otp.OtpVerificationScreen
import io.github.darci.smsauthentication.presentation.otp.OtpViewModel
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
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.PHONE_NUMBER,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.PHONE_NUMBER) {
                            val viewModel: PhoneNumberViewModel = viewModel(
                                factory = PhoneNumberViewModelFactory(applicationContext)
                            )
                            PhoneNumberScreen(
                                viewModel = viewModel,
                                onNavigateToOtp = { phoneNumber ->
                                    navController.navigate(Routes.otpVerification(phoneNumber))
                                }
                            )
                        }

                        composable(
                            route = Routes.OTP_VERIFICATION,
                            arguments = listOf(
                                navArgument("phoneNumber") { type = NavType.StringType }
                            )
                        ) {
                            val viewModel: OtpViewModel = viewModel()
                            OtpVerificationScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}