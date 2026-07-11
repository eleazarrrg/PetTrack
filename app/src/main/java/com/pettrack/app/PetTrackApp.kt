package com.pettrack.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class PetTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // osmdroid: load persisted config (enables the tile cache) and set a user agent
        // so the OSM tile server doesn't reject requests.
        val osmPrefs = getSharedPreferences("osmdroid", MODE_PRIVATE)
        Configuration.getInstance().load(this, osmPrefs)
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}
