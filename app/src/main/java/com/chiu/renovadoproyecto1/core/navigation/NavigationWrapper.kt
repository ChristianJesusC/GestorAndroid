package com.chiu.renovadoproyecto1.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chiu.renovadoproyecto1.core.hardware.di.HardwareModule
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.JuegosScreen
import com.chiu.renovadoproyecto1.features.login.di.AppModule
import com.chiu.renovadoproyecto1.features.login.presentation.View.LoginScreen
import com.chiu.renovadoproyecto1.features.login.presentation.ViewModel.LoginViewModel
import com.chiu.renovadoproyecto1.features.login.presentation.ViewModel.LoginViewModelFactory
import com.chiu.renovadoproyecto1.features.register.di.RegisterModule
import com.chiu.renovadoproyecto1.features.register.presentation.View.RegisterScreen
import com.chiu.renovadoproyecto1.features.register.presentation.ViewModel.RegisterViewModel
import com.chiu.renovadoproyecto1.features.register.presentation.ViewModel.RegisterViewModelFactory

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val biometricUseCase = remember {
        HardwareModule.provideBiometricUseCase(context, activity)
    }

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            loginUseCase = AppModule.loginUseCase,
            biometricUseCase = biometricUseCase
        )
    )

    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToJuegos = {
                    navController.navigate(Juegos) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Register)
                }
            )
        }

        composable<Register> {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(RegisterModule.registerUseCase)
            )

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Juegos> {
            JuegosScreen(
                onNavigateToLogin = {
                    loginViewModel.logout()
                    navController.navigate(Login) {
                        popUpTo(Juegos) { inclusive = true }
                    }
                }
            )
        }
    }
}