package com.example.whattoeat.presentation.settings

import androidx.hilt.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rx3.RxDataStore
import androidx.datastore.preferences.rx3.RxDataStoreBuilder
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // DataStore would be injected here
) : ViewModel() {

    // Theme settings
    var themeMode: Int = 0 // 0 = system, 1 = light, 2 = dark
        private set

    var spoonacularApiKey: String = ""
        private set

    fun setThemeMode(mode: Int) {
        themeMode = mode
        // Save to DataStore
    }

    fun setSpoonacularApiKey(key: String) {
        spoonacularApiKey = key
        // Save to DataStore
    }
}

// DataStore keys
object SettingsKeys {
    val THEME_MODE = preferencesKey<Int>("theme_mode")
    val SPOONACULAR_API_KEY = preferencesKey<String>("spoonacular_api_key")
    val LANGUAGE = preferencesKey<String>("language")
}