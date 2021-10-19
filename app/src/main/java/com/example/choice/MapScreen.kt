package com.example.choice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*

class MapScreen : Fragment(R.layout.fragment_map) , OnMapReadyCallback {

    companion object {
        private const val TAG = "MapsActivity"
        private const val ZOOM = 10F
    }

    private lateinit var mMap: GoogleMap

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.d(TAG, "Place: ${place.name}, ${place.id}")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 15f))
            }

            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred: $status")
            }
        })
        btnNearby.setOnClickListener {
            startActivity(Intent(requireContext(), NearbyActivity::class.java))
        }
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


        val latitude = -36.8509
        val longitude = 174.7645

        val homeLatLng = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, ZOOM))
//        mMap.addMarker(MarkerOptions().position(homeLatLng))
        setMapLongClick(mMap)
        setPoiClick(mMap)

        addMarkFromFireStore()

    }
    private fun setMapLongClick(mMap: GoogleMap) {
        mMap.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            mMap.clear()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)

            )


            saveLocation(latLng.latitude, latLng.longitude)
        }
    }
    private fun setPoiClick(mMap: GoogleMap) {
        mMap.setOnPoiClickListener { poi ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }

    }
    private val REQUEST_LOCATION_PERMISSION = 1
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
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
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
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
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
    internal class OnPoiClickDemoActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleMap.OnPoiClickListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.poi_click_demo)
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        override fun onMapReady(map: GoogleMap) {
            map.setOnPoiClickListener(this)
        }

        override fun onPoiClick(poi: PointOfInterest) {
            Toast.makeText(this, """Clicked: ${poi.name}
            Place ID:${poi.placeId}
            Latitude:${poi.latLng.latitude} Longitude:${poi.latLng.longitude}""",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveLocation(latitude:Double, longitude:Double) {
        val location = mapOf(
            "id" to DeviceId.id(requireContext()),
            "latitude" to latitude,
            "longitude" to longitude,
            "ts" to System.currentTimeMillis()
        )
        db.collection("location")
            .add(location)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun addMarkFromFireStore() {
        db.collection("location")
            .whereEqualTo("id", DeviceId.id(requireContext()))
            .orderBy("ts", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val location = document.data
                    val latitude = location["latitude"] as Double
                    val longitude = location["longitude"] as Double

                    val snippet = String.format(
                        Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latitude,
                        longitude
                    )
                    mMap.clear()
                    val latLng = LatLng(latitude, longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)
                    )

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }
    }
}