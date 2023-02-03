package com.jack.lightmeter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CustomValuesActivity : AppCompatActivity() {
    var CameraObject:CameraSettings?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_values)
        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50);
        }

        val ValuesToEdit = if(intent.getStringExtra("Edit") != null){
            intent.getStringExtra("Edit")
        }else {
            "Something Went Wrong"
        }
        if(ValuesToEdit != "Calibration"){
            setTitle("Editing Custom "+ ValuesToEdit!! + " Values")
        }


    }
}