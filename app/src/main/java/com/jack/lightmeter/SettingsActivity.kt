package com.jack.lightmeter

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

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
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }



}