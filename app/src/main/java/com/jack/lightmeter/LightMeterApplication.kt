package com.jack.lightmeter

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val APERTURES_SAVED = "apertures_preference"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APERTURES_SAVED
)

class LightMeterApplication: Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
    override fun onCreate(){
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}