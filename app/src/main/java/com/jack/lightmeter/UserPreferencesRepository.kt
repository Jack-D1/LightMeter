package com.jack.lightmeter

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.PreferencesProto.StringSet
import androidx.datastore.preferences.PreferencesProto.StringSetOrBuilder
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SavedApertures = stringSetPreferencesKey("apertures_preference")
        const val TAG = "UserPreferencesRepo"
    }
    var x:Set<String> = setOf()
    val savedApertures: Flow<StringSet> = dataStore.data
        .catch{
            if(it is IOException){
                Log.e(TAG,"Error reading preferences ", it)
            }else{
                throw it
            }
        }.map{preferences ->
            preferences[SavedApertures] ?:
        }
}