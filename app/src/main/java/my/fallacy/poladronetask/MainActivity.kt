package my.fallacy.poladronetask

// Your IDE likely can auto-import these classes, but there are several
// different implementations so we list them here to disambiguate
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var layoutContainer: RelativeLayout
    private lateinit var mapView: View
    private lateinit var ivCameraView: ImageView
    private lateinit var ibMap: ImageButton
    private lateinit var ibCapture: ImageButton
    private lateinit var ibBack: ImageButton

    private lateinit var mMap: GoogleMap
    private lateinit var currentLatLng : LatLng
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker

    private var isCameraView = true

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10

        // This is an array of all the permission specified in the manifest
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add this at the end of onCreate function

        layoutContainer = findViewById(R.id.layoutContainer)
        mapView = findViewById(R.id.mapView)
        ivCameraView = findViewById(R.id.ivCameraView)
        ibMap = findViewById(R.id.ibMap)
        ibCapture = findViewById(R.id.ibCapture)
        ibBack = findViewById(R.id.ibBack)

        // Request loc permissions
        if (allPermissionsGranted()) {
            startMap()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        ibMap.setOnClickListener {
            if (isCameraView) {
                mapView.visibility = View.VISIBLE
                ivCameraView.visibility = View.GONE
            }
            else {
                mapView.visibility = View.GONE
                ivCameraView.visibility = View.VISIBLE
            }

            isCameraView = !isCameraView
        }

        ibCapture.setOnClickListener {
            Snackbar.make(
                layoutContainer,
                "Captured",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        ibBack.setOnClickListener {
            Snackbar.make(layoutContainer, "Back", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startMap()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
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
