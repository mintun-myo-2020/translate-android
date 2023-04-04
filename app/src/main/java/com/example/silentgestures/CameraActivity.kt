package com.example.silentgestures

//import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class CameraActivity : AppCompatActivity() {
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
}