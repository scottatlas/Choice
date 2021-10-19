package com.example.choice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NearbyActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "NearbyActivity"
        private const val ZOOM = 10F
    }

    private lateinit var mMap: GoogleMap

    private val usersRef = Firebase.database.reference.child("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableMyLocation()

        val latitude = -36.8509
        val longitude = 174.7645

        val homeLatLng = LatLng(latitude, longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, ZOOM))


        usersRef.get().addOnSuccessListener {
            Log.d(TAG, it.value.toString())
            it.children.forEach { ds ->
                val firstName = ds.child("first_name").getValue(String::class.java)
                val lastName = ds.child("last_name").getValue(String::class.java)
                val lat = ds.child("lat").getValue(Double::class.java)
                val lng = ds.child("lng").getValue(Double::class.java)
                if (lat != null && lng != null) {
                    val snippet = "$firstName $lastName"
                    val latLng = LatLng(lat, lng)
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(snippet)
                            .snippet(snippet)
                    )
                    marker.tag = ds
                }
            }
            mMap.setOnMarkerClickListener {
                val ds = (it.tag as DataSnapshot?) ?: return@setOnMarkerClickListener true
                val firstName = ds.child("first_name").getValue(String::class.java)
                it.showInfoWindow()
                val lastName = ds.child("last_name").getValue(String::class.java)
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("first_name" + " " + "last_name", "$firstName $lastName")
                intent.putExtra("uid", ds.key)
                intent.putExtra("isNearby", true)
                startActivity(intent)
                true
            }
        }.addOnFailureListener {
            Log.e(TAG, "Error getting data", it)
        }

    }

    private val REQUEST_LOCATION_PERMISSION = 1
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled = true
            LocationServices.getFusedLocationProviderClient(this)
                .lastLocation
                .addOnSuccessListener {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), ZOOM))
                    usersRef.child(Firebase.auth.currentUser!!.uid).updateChildren(
                        mapOf(
                            "lat" to it.latitude,
                            "lng" to it.longitude
                        )
                    )
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }


    // Callback for the result from requesting permissions.
    // This method is invoked for every call on requestPermissions(android.app.Activity, String[],
    // int).
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


}