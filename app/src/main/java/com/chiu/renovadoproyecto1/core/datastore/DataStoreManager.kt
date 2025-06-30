package com.chiu.renovadoproyecto1.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class DataStoreManager (private val context: Context){

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_settings")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TOKEN] = token
        }
    }

    suspend fun getTokenSync(): String? {
        return try {
            val token = context.dataStore.data.first()[PreferenceKeys.TOKEN]
            if (token.isNullOrEmpty()) null else token
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.TOKEN)
        }
    }

}