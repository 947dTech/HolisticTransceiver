package com.ksndtech.holistictransceiver.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.ksndtech.holistictransceiver.data.UdpSettings
import com.ksndtech.holistictransceiver.data.UdpSettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UdpSettingsRepository(application)

    val settings: StateFlow<UdpSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UdpSettings()
    )

    fun save(host: String, port: Int) {
        viewModelScope.launch {
            repository.save(host, port)
        }
    }
}