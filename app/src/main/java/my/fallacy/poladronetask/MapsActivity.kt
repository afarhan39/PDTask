package my.fallacy.poladronetask

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var currentLatLng : LatLng
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        markerOptions = MarkerOptions()
        markerOptions.position(LatLng(0.0, 0.0)).title("Drone")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestLocation()
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
        Log.d("yikes", "onMapReady")
        mMap = googleMap
        marker = mMap.addMarker(markerOptions)
    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            Log.d("yikes", "lat: " + p0.latitude + " long: " + p0.longitude)
            currentLatLng = LatLng(p0.latitude, p0.longitude)
            marker.position = currentLatLng
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        val provider = locationManager.getBestProvider(criteria, true)
        locationManager.requestLocationUpdates(provider, 1000, 10f, this)

    }
}
