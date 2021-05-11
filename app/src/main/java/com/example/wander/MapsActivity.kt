package com.example.wander

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val TAG = MapsActivity::class.java.simpleName

    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //Activity contains SupportFragment Must Implement onMapReady CallBack
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker at Home and move the camera
        val latitude = -34.7820117
        val longitude = 138.6128763
        val zoomLevel = 15f

        val homeLocation = LatLng(latitude,longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLocation,zoomLevel))
        map.addMarker(MarkerOptions().position(homeLocation).title("Marker at Home"))

        //float overlaysize width in meter
        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.sine_id))
                .position(homeLocation,overlaySize)
        map.addGroundOverlay(androidOverlay)



        setMapLongClick(map)
        setPoiClick(map)

        setMapStyle(map)

        //to set check and set location button
        enableMyLocation()
    }

    //customize the map style by adding json and this method
    private fun setMapStyle(map: GoogleMap) {
        val success = map.setMapStyle(MapStyleOptions.
        loadRawResourceStyle(this,R.raw.map_style))

        try {
            if(!success){
                Log.e(TAG,"Map Styling failed")
            }
        }catch (e:Resources.NotFoundException){
                Log.e(TAG,"Can't find Style")
        }
    }

    //to set point of interest
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener {
            poi ->
            val poiMarker = map.addMarker(
                    MarkerOptions().position(poi.latLng)
                    .title(poi.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            //to show info of poi
            poiMarker.showInfoWindow()
        }

    }

    //to add marker on long click
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener {
            latLang ->
            val snippet = String.format(Locale.getDefault(),getString(R.string.lat_long_snippet),
            latLang.latitude,latLang.longitude)
            //icon used for styling marker
            map.addMarker(
                    MarkerOptions().position(latLang).title(getString(R.string.dropped_pin))
                    .snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker
                    (HUE_MAGENTA))
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when(item.itemId){
            R.id.normal_map ->{
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map ->{
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map ->{
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map ->{
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun isPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if(isPermissionGranted())
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled =true
            } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

}
