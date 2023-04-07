package com.example.silentgestures

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.RECORD_AUDIO
)

class MainActivity : AppCompatActivity() {
//    private val CAMERA_PERMISSION_CODE = 1
//    private val AUDIO_PERMISSION_CODE = 2
//    private val READ_STORAGE_PERMISSION_CODE = 3
//    private val WRITE_STORAGE_PERMISSION_CODE = 4

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && permissions[Manifest.permission.RECORD_AUDIO] == true) {
            Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            navigateToCamera()
        } else {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val PERMISSIONS_REQUIRED = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO
//        )

        when {
            hasPermissions(this) -> {
                navigateToCamera()
            }
            else -> {
                requestPermissionLauncher.launch(
                    PERMISSIONS_REQUIRED
                )
            }
        }
//        Log.d("lifecycle permission1", Manifest.permission.CAMERA)

//        val requestPermissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && permissions[Manifest.permission.RECORD_AUDIO] == true) {
//                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
//
//                val startButton = findViewById<Button>(R.id.startButton)
//
//                startButton.setOnClickListener {
//                    val intent = Intent(this, CameraActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                }
//
//            } else {
//                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
//            }
//        }
//        Log.d("lifecycle permission", PERMISSIONS_REQUIRED[Manifest.permission.CAMERA])
//            if (PERMISSIONS_REQUIRED[Manifest.permission.CAMERA] == true && PERMISSIONS_REQUIRED[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true && PERMISSIONS_REQUIRED[Manifest.permission.RECORD_AUDIO] == true) {
//                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
//                navigateToCamera()
//            } else {
//                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
//            }

//        // Check if camera permission is granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request camera permission if not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA),
//                CAMERA_PERMISSION_CODE
//            )
//        }
//
//        // Check if audio permission is granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request audio permission if not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECORD_AUDIO),
//                AUDIO_PERMISSION_CODE
//            )
//        }
//
//        // Check if read storage permission is granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request read storage permission if not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                READ_STORAGE_PERMISSION_CODE
//            )
//        }
//
//        // Check if write storage permission is granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Request write storage permission if not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                WRITE_STORAGE_PERMISSION_CODE
//            )
//        }

    }

    private fun navigateToCamera() {
//        Log.d("lifecycle camera", "lifecycle camera")
//        val startButton = findViewById<Button>(R.id.startButton)

//        startButton.setOnClickListener {
        Thread.sleep(500)
        val intent = Intent(this, CameraActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
//        }

        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            Thread.sleep(500)
            val intent = Intent(this, CameraActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    //    // Handle permission request results
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when (requestCode) {
//            CAMERA_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted for camera
//                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Permission denied for camera
//                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            AUDIO_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted for audio
//                    Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Permission denied for audio
//                    Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            READ_STORAGE_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted for audio
//                    Toast.makeText(this, "Read storage permission granted", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Permission denied for audio
//                    Toast.makeText(this, "Read storage permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            WRITE_STORAGE_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted for audio
//                    Toast.makeText(this, "Write storage permission granted", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Permission denied for audio
//                    Toast.makeText(this, "Write storage permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

}