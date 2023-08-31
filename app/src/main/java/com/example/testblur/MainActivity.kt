package com.example.testblur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val blurryBackgroundView = findViewById<BlurryBackgroundView>(R.id.blurryBackgroundView)
        blurryBackgroundView.updateBackgroundFromView()
    }

}