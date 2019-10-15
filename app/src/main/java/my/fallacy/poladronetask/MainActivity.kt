package my.fallacy.poladronetask

// Your IDE likely can auto-import these classes, but there are several
// different implementations so we list them here to disambiguate
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
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
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    private lateinit var layoutContainer: RelativeLayout
    private lateinit var mapView: View
    private lateinit var ivCameraView: ImageView
    private lateinit var ibMap: ImageButton
    private lateinit var ibCapture: ImageButton

    //topbar
    private lateinit var ibHome: ImageButton
    private lateinit var ibFlightStatus: ImageButton
    private lateinit var ibGps: ImageButton
    private lateinit var ibSignal: ImageButton
    private lateinit var ibBattery: ImageButton
    private lateinit var ibSettings: ImageButton

    //controller
    private lateinit var ibJoystick: ImageButton
    private lateinit var ibFLightLand: ImageButton
    private lateinit var ibFlightTakeOff: ImageButton

    private lateinit var mMap: GoogleMap
    private lateinit var currentLatLng: LatLng
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker: Marker

    private var isCameraView = true

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val DEFAULT_ZOOM = 15f

        // This is an array of all the permission specified in the manifest
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // main
        layoutContainer = findViewById(R.id.layoutContainer)
        mapView = findViewById(R.id.mapView)
        ivCameraView = findViewById(R.id.ivCameraView)
        ibMap = findViewById(R.id.ibMap)
        ibCapture = findViewById(R.id.ibCapture)

        //controller
        ibJoystick = findViewById(R.id.ibJoystick)
        ibFLightLand = findViewById(R.id.ibFlightLand)
        ibFlightTakeOff = findViewById(R.id.ibFlightTakeOff)

        //topBar
        ibHome = findViewById(R.id.ibHome)
        ibFlightStatus = findViewById(R.id.ibFightStatus)
        ibGps = findViewById(R.id.ibGps)
        ibSignal = findViewById(R.id.ibSignal)
        ibBattery = findViewById(R.id.ibBattery)
        ibSettings = findViewById(R.id.ibSettings)

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
                ibCapture.visibility = View.GONE
            } else {
                mapView.visibility = View.GONE
                ivCameraView.visibility = View.VISIBLE
                ibCapture.visibility = View.VISIBLE
            }

            isCameraView = !isCameraView
        }

        ibCapture.setOnClickListener {
            showSnackbar("Captured")
        }
        ibHome.setOnClickListener {
            showSnackbar("Home")
        }
        ibFlightStatus.setOnClickListener {
            showSnackbar("Flight Status")
        }
        ibGps.setOnClickListener {
            showSnackbar("GPS Status")
        }
        ibSignal.setOnClickListener {
            showSnackbar("Wifi Signal")
        }
        ibBattery.setOnClickListener {
            showSnackbar("Battery Level")
        }
        ibSettings.setOnClickListener {
            showSnackbar("General Settings")
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
        markerOptions.position(LatLng(3.1466, 101.6958))
            .title("Drone")
            .icon(bitmapDescriptorFromVector(this))

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestLocation()
    }

    private fun bitmapDescriptorFromVector(
        context: Context
    ): BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_nav) as Drawable
        background.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) as Bitmap
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night))
        marker = mMap.addMarker(markerOptions)
    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            Log.d("yikes", "lat: " + p0.latitude + " long: " + p0.longitude)
            currentLatLng = LatLng(p0.latitude, p0.longitude)
            marker.position = currentLatLng
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
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

    private fun showSnackbar(message: String) {
        Snackbar.make(layoutContainer, message, Snackbar.LENGTH_SHORT).show()
    }
}
