package com.jack.lightmeter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.jack.lightmeter.R.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager:SensorManager?= null
    var sensor:Sensor?=null
    var CameraObject:CameraSettings?=null
    // Shutter Speed Text Box
    var tv1:TextView?=null
    // Lux Text Representation
    var tv2:TextView?=null
    // EV Text
    var tv3:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        //Get Camera Settings object from Aperture Priority activity
        CameraObject = if(savedInstanceState?.get("Camera") != null){
            savedInstanceState.get("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50);
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        tv1 = findViewById(id.text)
        tv2 = findViewById(id.text_lux)
        tv3 = findViewById(id.text_ev)
        val exposeISODropdown = findViewById<MaterialButton>(R.id.expose_iso_dropdown_button)
        val ISOpopup = PopupMenu(this, exposeISODropdown )
        for(iso in CameraObject!!.validISOs){
            ISOpopup.menu.add(iso.toString())
        }
        exposeISODropdown.setOnClickListener {
            ISOpopup.show()
        }
        ISOpopup.setOnMenuItemClickListener { item ->
            exposeISODropdown.text = "ISO " + item.title
            CameraObject?.iso = item.title.toString().toInt()
            true
        }


        val exposeApertureDropdown = findViewById<MaterialButton>(R.id.expose_aperture_dropdown_button)
        val AperturePopup = PopupMenu(this, exposeApertureDropdown)
        for (Aperture in CameraObject!!.validApertures){
            AperturePopup.menu.add(Aperture.toString())
        }
        exposeApertureDropdown.setOnClickListener {
            AperturePopup.show()
        }
        AperturePopup.setOnMenuItemClickListener { item ->
            exposeApertureDropdown.text = "f/" + item.title
            CameraObject?.aperture = item.title.toString().toFloat()
            true
        }


        val button:Button = findViewById(id.ChangeSwitch)
        button.setOnClickListener{
            val intent = Intent(this@MainActivity, ShutterPriority::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause(){
        super.onPause();
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.values[0] > 0){
            // Setup the variable lx to hold the currently measured Lux measurement
            var lx:Float
            lx = event.values[0]

            //Tell the camera settings object to update the shutter speed it holds
            CameraObject?.updateShutterSpeedInAP(lx)
            // Get the formatted shutter speed
            tv1?.text = CameraObject?.formattedShutterSpeed
            // Show the lux reading
            tv2?.text = lx.toString() + "lx"
            // Show the measured EV as calculated by the CameraSettings object
            tv3?.text = "EV " + CameraObject?.ev
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


}


