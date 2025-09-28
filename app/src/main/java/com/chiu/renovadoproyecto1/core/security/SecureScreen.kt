package com.chiu.renovadoproyecto1.core.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun SecureScreen(
    content: @Composable () -> Unit
) {
    DisposableEffect(Unit) {
        SecureScreenManager.enterSecureScreen()

        onDispose {
            SecureScreenManager.exitSecureScreen()
        }
    }

    content()
}