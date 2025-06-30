package com.chiu.renovadoproyecto1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chiu.renovadoproyecto1.core.appcontext.AppContextHolder
import com.chiu.renovadoproyecto1.features.login.di.AppModule
import com.chiu.renovadoproyecto1.core.navigation.NavigationWrapper
import com.chiu.renovadoproyecto1.ui.theme.RenovadoProyecto1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Iniciando aplicación")
        AppContextHolder.init(this)

        enableEdgeToEdge()
        setContent {
            RenovadoProyecto1Theme {
                NavigationWrapper()
            }
        }
    }
}
