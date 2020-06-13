package ru.adonixis.aceventura56

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.yandex.mapkit.MapKitFactory

class AceVentura56 : Application() {

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        MapKitFactory.setApiKey(getString(R.string.yandex_maps_api_key))
    }

}