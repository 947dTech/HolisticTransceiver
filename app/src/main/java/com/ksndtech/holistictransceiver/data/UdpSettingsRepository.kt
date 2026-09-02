package com.ksndtech.holistictransceiver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "udp_settings")

data class UdpSettings(
    val host: String = "192.168.1.100",
    val port: Int = 0x947d
)

class UdpSettingsRepository(private val context: Context) {

    private object Keys {
        val HOST = stringPreferencesKey("udp_host")
        val PORT = intPreferencesKey("udp_port")
    }

    val settingsFlow: Flow<UdpSettings> = context.dataStore.data.map { prefs ->
        UdpSettings(
            host = prefs[Keys.HOST] ?: "192.168.1.100",
            port = prefs[Keys.PORT] ?: 0x947d
        )
    }

    suspend fun getCurrent(): UdpSettings = settingsFlow.first()

    suspend fun save(host: String, port: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HOST] = host
            prefs[Keys.PORT] = port
        }
    }
}