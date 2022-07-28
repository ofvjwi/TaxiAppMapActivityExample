package com.example.defaultmapactivity

import android.app.Application
import com.example.defaultmapactivity.utils.Simulator
import com.google.maps.GeoApiContext
import com.google.android.libraries.places.api.Places

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_maps_key));
        Simulator.geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build()
    }


    /**

    // Android Lint
    @UiThread
    fun updateUi() {
    // code goes here
    networkRequest()
    }

    //  @WorkerThread
    fun networkRequest() {
    // code goes here
    }

     **/

}

