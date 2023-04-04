package com.example.silentgestures

//import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.silentgestures.databinding.ActivityCameraBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService


class CameraActivity : AppCompatActivity() {

    private val TAG = "ObjectDetection"

    private var _activityCameraBinding: ActivityCameraBinding? = null

    private val activityCameraBinding
        get() = _activityCameraBinding!!

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService
    private var videoCapture: VideoCapture? = null
    private lateinit var buttonRecord: Button
    private val REQUEST_CODE_PERMISSIONS = 1001


    private var showWords:String? = ""
    private var prevWord:String? = ""
    private var words = ArrayList<String>()
//    private var words = arrayListOf("")

    private val txtFileName = ""
    private lateinit var outputStream: FileOutputStream
    private var appendWords:String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeButton -> {
                    // Handle click on Home button
                    false
                }
                R.id.savedButton -> {
                    // Handle click on Saved button
                    val intent = Intent(this, SavedActivity::class.java)
                    startActivity(intent)
                    false
                }
                else -> false
            }
        }

    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0);
    }
}