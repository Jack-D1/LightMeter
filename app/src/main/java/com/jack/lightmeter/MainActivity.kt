package com.jack.lightmeter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.jack.lightmeter.R.id
import com.jack.lightmeter.R.layout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        var CameraObject:CameraSettings?=null
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        //Get Camera Settings object from other activity
        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50);
        }
        CameraObject?.context = this
        val apertureButton:MaterialButton = findViewById(id.aperture_start_button)
        apertureButton.setOnClickListener{
            val intent = Intent(this@MainActivity, AperturePriorityActivity::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }

        val shutterButton:MaterialButton = findViewById(id.shutter_start_button)
        shutterButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ShutterPriorityActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            startActivity(intent)
        }
        val settingsButton:MaterialButton = findViewById(id.settings_start_button)
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            startActivity(intent)
        }
    }



}


