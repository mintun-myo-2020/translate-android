package com.example.silentgestures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileInputStream

class SavedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)

        // set navigation listener
        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeButton -> {
                    // Handle click on Home button
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.savedButton -> {
                    // Handle click on Saved button
                    false
                }
                else -> false
            }
        }

        Glide.with(this)
        getVideoFiles()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0);
    }

    private fun getVideoFiles() {
        val mediaDir = externalMediaDirs?.firstOrNull()?.let{
            File(it,resources.getString(R.string.app_name)).apply{mkdirs()}
        }

        // sort videos in descending order (most recent recording at the top)
        val gifFiles = mediaDir?.listFiles{file->
            file.isFile&& file.name.endsWith(".gif")
        }?.sortedArrayWith(compareBy{ it.name.toLowerCase()})?.reversed()

        val listView = findViewById<ListView>(R.id.lvSavedVideo)
        val data =mutableListOf<File>()

        if (gifFiles?.size != 0) {
            gifFiles?.forEach{file->
                data.add(file)
            }

            val adapter = object : ArrayAdapter<File>(this, R.layout.item_layout, data) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)

                    val file = getItem(position)
                    if (file != null) {
                        val imageView = view.findViewById<ImageView>(R.id.imageView)

                        Glide.with(this@SavedActivity)
                            .load(file)
                            .thumbnail(0.1f)
                            .into(imageView)

                        val textView = view.findViewById<TextView>(R.id.textView)

                        val txtFile =
                            File(context.filesDir, file.name.substringBeforeLast(".") + ".txt")
                        var wordsString = ""
                        if (txtFile.exists()) {
                            val inputStream = FileInputStream(txtFile)

                            // read the translated words from the specific .txt file
                            val strings = inputStream.bufferedReader().use { it.readLines() }
                            inputStream.close()
                            for (string in strings) {
                                wordsString += "$string "
                            }

                            // display the translated words on TextView
                            textView.text = wordsString

                        }

                        view.setOnClickListener {
                            // play the individual saved video when it is clicked
                            val intent = Intent(context, VideoPlayerActivity::class.java)
                            intent.putExtra("VIDEO_PATH_EXTRA", file.absolutePath.substringBeforeLast(".") + ".mp4")
                            intent.putExtra("VIDEO_WORDS_EXTRA", wordsString)
                            startActivity(intent)
                        }
                    }
                    return view
                }

            }

            listView.adapter = adapter

        }

    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0);
    }
}