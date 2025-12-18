package com.ssafy.hm.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("hm_auth")

class AuthStore(private val context: Context) {
    private val KEY_USER_ID = stringPreferencesKey("userId")

    val userIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    suspend fun setUser(userId: String?) {
        context.dataStore.edit { prefs ->
            if (userId == null) {
                prefs.remove(KEY_USER_ID)
            } else {
                prefs[KEY_USER_ID] = userId
            }
        }
    }
}
