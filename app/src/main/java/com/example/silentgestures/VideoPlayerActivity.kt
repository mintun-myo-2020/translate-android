package com.example.silentgestures

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        // Get the video path from the intent extra
        val videoPath =intent.getStringExtra("VIDEO_PATH_EXTRA")
        if (videoPath != null) {
            Log.d("video mp4", videoPath)
        }
        val gifUri: Uri = Uri.parse(intent.getStringExtra("GIF_URI_EXTRA"))
        // Initialize the VideoView
        val vvSavedVideo: VideoView = findViewById(R.id.vvSavedVideo)
        vvSavedVideo.setVideoPath(videoPath)

//        Glide.with(this)
//            .asGif()
//            .load(gifUri)
//            .into(ivSavedVideo)
//
//        Log.d("lifecycle gifuri33", gifUri!!.toString())
//
        // Set the MediaController for the VideoView
        val mediaController = MediaController(this)
        mediaController.setAnchorView(vvSavedVideo)
        vvSavedVideo.setMediaController(mediaController)

        // Start the video
        vvSavedVideo.start()

        val tvVideoWords:TextView = findViewById(R.id.tvVideoWords)
        tvVideoWords.text=intent.getStringExtra("VIDEO_WORDS_EXTRA")
    }


}