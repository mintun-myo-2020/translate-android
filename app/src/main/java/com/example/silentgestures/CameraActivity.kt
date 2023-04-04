package com.example.silentgestures

//import android.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), ObjectDetectorHelperClass.DetectorListener {
    private val TAG = "ObjectDetection"
    private lateinit var objectDetectorHelper: ObjectDetectorHelperClass
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
//        Log.d("lifecycle oncreate", "lifecycle oncreate")

        // set navigation listener
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

        // set buttonRecord on click listener
        buttonRecord = findViewById(R.id.buttonRecord)
        buttonRecord.setOnClickListener {
            toggleVideoRecording()
            Log.d("FragmentLifecycle1", "buttonRecord Clicked")
        }

        objectDetectorHelper = ObjectDetectorHelperClass(
            context = this,
            objectDetectorListener = this)

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()


        // Set up the camera and its use cases
        setUpCamera()
    }



    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    @SuppressLint("RestrictedApi")
    private fun bindCameraUseCases() {
        val view_finder = findViewById<PreviewView>(R.id.view_finder)

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(view_finder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        Log.d("lifecycle checkimage", "lifecycle checkimage")
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(view_finder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }
//                        Log.d("lifecycle imagedetect1", "lifecycle imagedetect")
                        detectObjects(image)
                    }
                }

        videoCapture =
            VideoCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(view_finder.display.rotation)
                .build()


        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer, videoCapture)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(view_finder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun getOutputFile(): File {
        val mediaDir = externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(mediaDir, "VID_${timeStamp}.mp4")
    }


    @SuppressLint("RestrictedApi")
    private fun toggleVideoRecording() {

        if (videoCapture == null) {
            return
        }

        if (buttonRecord.text == getString(R.string.start_recording)) {
            val file = getOutputFile()
            val outputFileOptions = VideoCapture.OutputFileOptions.Builder(file).build()

            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )

            val permissionResults = ArrayList<Int>()

            for (permission in permissions) {
                permissionResults.add(ContextCompat.checkSelfPermission(this, permission))
            }

            val allPermissionsGranted = permissionResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allPermissionsGranted) {
                if (videoCapture == null) {
                    return
                }
                Log.d("lifecycle Recording", "lifecycle Recording")
                videoCapture?.startRecording(
                    outputFileOptions,
                    cameraExecutor,
                    object : VideoCapture.OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@CameraActivity,
                                    "Video saved: ${file.absolutePath}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.d("lifecycle videosaved", "lifecycle videosaved")

                                // Convert mp4 to gif
                                val gifFilePath =
                                    file.absolutePath.substringBeforeLast(".") + ".gif"
                                convertMp4ToGif(file.absolutePath, gifFilePath)

                                val txtFile = File(
                                    filesDir,
                                    file.name.substringBeforeLast(".") + ".txt"
                                )
                                outputStream = FileOutputStream(txtFile, true)

                                // appendWords += file.name + System.lineSeparator()
                                Log.d("CHECK words APPEND", "$appendWords")

                                // the file is in Device File Explorer
                                // data/data/org.tensorflow.lite.examples.objectdetection/files
                                outputStream?.write(appendWords?.toByteArray())
                                appendWords = ""
                            }
                        }

                        override fun onError(
                            videoCaptureError: Int,
                            message: String,
                            cause: Throwable?
                        ) {
                            Log.e(TAG, "Video recording failed: $message", cause)
                        }
                    }
                )

                buttonRecord.text = getString(R.string.stop_recording)
            }
        } else {
            videoCapture?.stopRecording()
            buttonRecord.text = getString(R.string.start_recording)
        }

    }

    private fun convertMp4ToGif(mp4FilePath: String, gifFilePath: String) {
        val width = "320"
        val height = "240"
        val frameRate = "10"

        val command = arrayOf(
            "-y",
            "-i", mp4FilePath,
            "-vf", "scale=$width:$height,setsar=1/1",
            "-r", frameRate,
            "-f", "gif",
            gifFilePath
        )

        FFmpeg.executeAsync(command) { executionId, returnCode ->
            if (returnCode == 0) {
                // Conversion successful
                Log.d("mp4 to gif", "conversion success")
            } else {
                // Conversion failed
                Log.e("mp4 to gif", "conversion failed")
            }
        }
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }


    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        runOnUiThread {
//            var overlay = OverlayView(this, null)
            var overlay = findViewById<OverlayView>(R.id.overlay)
            // Pass necessary information to OverlayView for drawing on the canvas
            var textLabel = overlay.setResults(
                results ?: LinkedList<Detection>(),
                imageHeight,
                imageWidth,
            )

            // Force a redraw
            overlay.invalidate()

            // display the text detected
            Log.d("CHECK words FIRST", "$words")

            if(words.size==0){
                prevWord = ""
            } else {
                prevWord = words.last()
            }

            Log.d("CHECK prevWord", "$prevWord")

            if(textLabel !in prevWord!!){
                words.add(textLabel)
                for(word in words){
                    showWords = (showWords + " " + word)

                    // when camera is recording -> append the words
                    if (buttonRecord.text== getString(R.string.stop_recording)) {
                        appendWords = "$appendWords$word" + System.lineSeparator()
                    }
                }
            }

            var displayTv = findViewById<TextView>(R.id.tvBottomSheet)
            displayTv?.text = showWords ?: "null detected! :("

            var clearBtn = findViewById<Button>(R.id.buttonClear)
            clearBtn?.setOnClickListener {
                words.clear()
                showWords = ""
                Toast.makeText(this, "Text cleared!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }



}