package com.jack.lightmeter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    var CameraObject:CameraSettings?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50);
        }


        val ISOButton: MaterialButton = findViewById(R.id.manage_iso_button)
        ISOButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, CustomValuesActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            intent.putExtra("Edit", "ISO")
            startActivity(intent)
        }

        val ShutterButton: MaterialButton = findViewById(R.id.manage_shutter_button)
        ShutterButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, CustomValuesActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            intent.putExtra("Edit", "Shutter Speed")
            startActivity(intent)
        }

        val ApertureButton: MaterialButton = findViewById(R.id.manage_aperture_button)
        ApertureButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, CustomValuesActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            intent.putExtra("Edit", "Aperture")
            startActivity(intent)
        }




        val returnButton: MaterialButton = findViewById(R.id.return_to_meter_button)
        returnButton.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }



}