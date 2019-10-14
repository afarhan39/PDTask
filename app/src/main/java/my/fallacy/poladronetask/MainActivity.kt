package my.fallacy.poladronetask

// Your IDE likely can auto-import these classes, but there are several
// different implementations so we list them here to disambiguate
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {


    private lateinit var layoutContainer: RelativeLayout
    private lateinit var ibMap: ImageButton
    private lateinit var ibCapture: ImageButton
    private lateinit var ibBack: ImageButton
    // This is an arbitrary number we are using to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts

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
        ibMap = findViewById(R.id.ibMap)
        ibCapture = findViewById(R.id.ibCapture)
        ibBack = findViewById(R.id.ibBack)

        // Request loc permissions
        if (allPermissionsGranted()) {
            //do nothing

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        ibMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        ibCapture.setOnClickListener { Snackbar.make(layoutContainer, "Captured", Snackbar.LENGTH_SHORT).show() }
        ibBack.setOnClickListener { Snackbar.make(layoutContainer, "Back", Snackbar.LENGTH_SHORT).show() }
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
                //do nothing
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
}
