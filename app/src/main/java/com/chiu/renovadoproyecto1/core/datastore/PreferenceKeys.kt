package com.chiu.renovadoproyecto1.core.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("auth_token")
    val FCM_TOKEN = stringPreferencesKey("fcm_token") // ✅ NUEVO
}