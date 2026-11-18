package com.example.grsapp5

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
//import com.mapbox.maps.plugin.gestures.GesturesUtils
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.gestures.gestures


class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var annotationManager: PointAnnotationManager
    private lateinit var markerBitmap: Bitmap
    private lateinit var clearButton: Button


    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        clearButton = findViewById(R.id.clearButton)

        markerBitmap = createMarkerBitmap()
        annotationManager = mapView.annotations.createPointAnnotationManager()

        clearButton.setOnClickListener {
            annotationManager.deleteAll()
        }

        mapView.mapboxMap.loadStyle(Style.STANDARD) {

            enableLocationComponent()

            setupMapClickListener()
        }
    }


    private fun createMarkerBitmap(): Bitmap {
        val sizePx = 40
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
        }
        canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2.5f, paint)
        return bitmap
    }


    private fun enableLocationComponent() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mapView.location.enabled = true
            mapView.location.pulsingEnabled = true
        } else {
            // разрешения нет — запрашиваем
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mapView.location.enabled = true
                mapView.location.pulsingEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Без разрешения на геолокацию текущее местоположение не отобразится",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupMapClickListener() {
        mapView.gestures.addOnMapClickListener { point ->
            annotationManager.create(
                PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(markerBitmap)
            )
            true
        }
    }

    // Управление жизненным циклом MapView (обязательно для Mapbox)

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}