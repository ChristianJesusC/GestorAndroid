package com.chiu.renovadoproyecto1.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.JuegosScreen
import com.chiu.renovadoproyecto1.features.login.di.AppModule
import com.chiu.renovadoproyecto1.features.login.presentation.LoginScreen
import com.chiu.renovadoproyecto1.features.login.presentation.LoginViewModel
import com.chiu.renovadoproyecto1.features.login.presentation.LoginViewModelFactory

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(AppModule.loginUseCase)
    )

    val uiState by loginViewModel.uiState.collectAsState()

    val startDestination = if (uiState.isLoginSuccessful) Juegos else Login

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToJuegos = {
                    navController.navigate(Juegos) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Juegos> {
            JuegosScreen(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Juegos) { inclusive = true }
                    }
                }
            )
        }
    }
}