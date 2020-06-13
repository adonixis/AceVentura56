package ru.adonixis.aceventura56.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_map.*
import ru.adonixis.aceventura56.R


class MapActivity : AppCompatActivity() {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        super.onCreate(savedInstanceState)

        mapView.map.isRotateGesturesEnabled = true

        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            mapView.map.move(CameraPosition(Point(latitude, longitude), 12.0F, 0.0F, 0.0F))
        }

        val locationListener: LocationListener = LocationListener { p0 ->
            latitude = p0.latitude
            longitude = p0.longitude
            mapView.map.move(
                    CameraPosition(Point(latitude, longitude), 20.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 2F),
                    null)
            mapView.map.mapObjects.clear()
            mapView.map.mapObjects
                    .addPlacemark(Point(latitude, longitude), ImageProvider.fromResource(this@MapActivity, R.drawable.ic_map_marker))
                    .addTapListener { p0, p1 ->
                        Log.d("MapActivity", "onMapObjectTap: ${p1?.latitude} ${p1?.longitude}")
                        true
                    }
            btnHere.isEnabled = true
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0F, locationListener)

        btnHere.setOnClickListener { saveGeoPosition(latitude, longitude) }
    }

    private fun saveGeoPosition(latitude: Double, longitude: Double) {
        val intent = Intent()
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

}