package com.jack.lightmeter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ShutterPriority : AppCompatActivity(), SensorEventListener {
    var sensorManager: SensorManager?= null
    var sensor: Sensor?=null
    var CameraObject:CameraSettings?=null
    // Aperture Text Box
    var ApertureText: MaterialTextView?=null
    // Lux text representation
    var LuxText: MaterialTextView?=null
    //EV text
    var EVText: MaterialTextView?=null
    val ShutterSpeeds = listOf<Float>(8f,4f,2f,1f,0.5f,0.25f,0.125f,0.066f,0.033f,0.0166f,0.008f,0.004f, 0.002f, 0.001f,0.0005f,0.00025f,0.000125f)
    val ReadableShutterSpeeds = listOf<String>("8","4","2", "1", "0.5", "1/4", "1/8", "1/15", "1/30", "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000","1/8000")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shutter_priority)
        //Get Camera Settings object from Aperture Priority activity
        CameraObject = if(savedInstanceState?.get("Camera") != null){
            savedInstanceState.get("Camera") as CameraSettings?
        }else {
            CameraSettings(8f,0f,50)
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        ApertureText = findViewById(R.id.ApertureReading)
        LuxText = findViewById(R.id.text_lux)
        EVText = findViewById(R.id.text_ev)
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



        val button:Button = findViewById(R.id.ChangeSwitch)
        button.setOnClickListener{
            val intent = Intent(this@ShutterPriority, MainActivity::class.java)
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
            var lx:Float
            lx = event.values[0]
            CameraObject?.updateApertureInSP(lx)
            ApertureText?.text = "f/" + CameraObject?.aperture.toString()
            LuxText?.text = lx.toString() + "lx"
            EVText?.text = "EV " + CameraObject?.ev.toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}