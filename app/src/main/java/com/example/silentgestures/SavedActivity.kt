package com.example.silentgestures

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.FileProvider
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

        Log.d("lifecycle oncreate", "oncreate")
        Glide.with(this)
        getVideoFiles()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0);
    }

    private fun getVideoFiles() {
        Log.d("lifecycle savedfragment", "getVideoFiles")
        val mediaDir = externalMediaDirs?.firstOrNull()?.let{
            File(it,resources.getString(R.string.app_name)).apply{mkdirs()}
        }

        // sort in descending order (most recent recording at the top)
        val gifFiles = mediaDir?.listFiles{file->
            file.isFile&& file.name.endsWith(".gif")
        }?.sortedArrayWith(compareBy{ it.name.toLowerCase()})?.reversed()

        val listView = findViewById<ListView>(R.id.lvSavedVideo)
        val data =mutableListOf<File>()
        Log.d("lifecycle data", data.toString())

        if (gifFiles?.size != 0) {
            gifFiles?.forEach{file->
                Log.d("lifecycle filepath", file.absolutePath)
                data.add(file)
            }
            Log.d("lifecycle data", data.toString())

//            val adapter = ArrayAdapter<File>(requireContext(), android.R.layout.simple_list_item_1, data)

            val adapter = object : ArrayAdapter<File>(this, R.layout.item_layout, data) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)

                    val file = getItem(position)
                    if (file != null) {
                        //                    val thumbnail = ThumbnailUtils.createVideoThumbnail(file!!.path, MediaStore.Images.Thumbnails.MINI_KIND)
                        val imageView = view.findViewById<ImageView>(R.id.imageView)
                        //                    imageView.setImageBitmap(thumbnail)

                        Glide.with(this@SavedActivity)
                            .load(file)
                            .thumbnail(0.1f)
                            .into(imageView)

                        val textView = view.findViewById<TextView>(R.id.textView)

                        Log.d("lifecycle file.name3", file.name)
                        val txtFile =
                            File(context.filesDir, file.name.substringBeforeLast(".") + ".txt")
                        var wordsString = ""
                        if (txtFile.exists()) {
                            val inputStream = FileInputStream(txtFile)

                            // it.readLines() will return List<String>
                            val strings = inputStream.bufferedReader().use { it.readLines() }
                            inputStream.close()
                            for (string in strings) {
                                wordsString += "$string "
                            }
                            textView.text = wordsString

                        }

                        view.setOnClickListener {

                            val gifUri: Uri = FileProvider.getUriForFile(
                                this@SavedActivity,
                                "${this@SavedActivity.packageName}.provider",
                                file
                            )

                            // Start the VideoPlayerActivity
                            val intent = Intent(context, VideoPlayerActivity::class.java)
                            intent.putExtra("VIDEO_PATH_EXTRA", file.absolutePath.substringBeforeLast(".") + ".mp4")
                            Log.d("video gif", file.absolutePath)
                            intent.putExtra("VIDEO_WORDS_EXTRA", wordsString)
                            intent.putExtra("GIF_URI_EXTRA", gifUri.toString())
                            Log.d("lifecycle gifuri5", gifUri.toString()!!)

                            startActivity(intent)
                        }

                    }
                    return view
                }

            }

            listView.adapter = adapter

        }

    }
}