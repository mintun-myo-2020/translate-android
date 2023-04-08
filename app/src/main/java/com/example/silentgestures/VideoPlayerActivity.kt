package com.example.silentgestures

import android.os.Bundle
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        // Get the video path from the intent extra
        val videoPath =intent.getStringExtra("VIDEO_PATH_EXTRA")
        if (videoPath != null) {
            // Initialize the VideoView
            val vvSavedVideo: VideoView = findViewById(R.id.vvSavedVideo)
            vvSavedVideo.setVideoPath(videoPath)

            // Set the MediaController for the VideoView
            val mediaController = MediaController(this)
            mediaController.setAnchorView(vvSavedVideo)
            vvSavedVideo.setMediaController(mediaController)

            // Start the video
            vvSavedVideo.start()

            // Get the translated words from the intent extra
            val tvVideoWords:TextView = findViewById(R.id.tvVideoWords)
            tvVideoWords.text=intent.getStringExtra("VIDEO_WORDS_EXTRA")
        }


    }


}