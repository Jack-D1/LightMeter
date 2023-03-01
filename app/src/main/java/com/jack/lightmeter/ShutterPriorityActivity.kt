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

class ShutterPriorityActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager: SensorManager?= null
    var sensor: Sensor?=null
    var CameraObject:CameraSettings?=null
    // Aperture Text Box
    var ApertureText: MaterialTextView?=null
    // Lux text representation
    var LuxText: MaterialTextView?=null
    //EV text
    var EVText: MaterialTextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shutter_priority)
        //Get Camera Settings object from Aperture Priority activity
        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable ("Camera") as CameraSettings?
        }else {
            CameraSettings(8f,0f,50, this)
        }
        CameraObject?.context = this
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        ApertureText = findViewById(R.id.ApertureReading)
        LuxText = findViewById(R.id.text_lux)
        EVText = findViewById(R.id.text_ev)
        val exposeISODropdown = findViewById<MaterialButton>(R.id.expose_iso_dropdown_button)
        val ISOpopup = PopupMenu(this, exposeISODropdown )
        CameraObject!!.getValidISOs().forEach{
            ISOpopup?.menu?.add(it.toString())
        }
        exposeISODropdown.setOnClickListener {
            ISOpopup.show()
        }
        ISOpopup.setOnMenuItemClickListener { item ->
            exposeISODropdown.text = "ISO " + item.title
            CameraObject?.iso = item.title.toString().toInt()
            true
        }

        val exposeShutterDropdown = findViewById<MaterialButton>(R.id.expose_shutter_dropdown_button)
        val ShutterPopup = PopupMenu(this,exposeShutterDropdown)
        CameraObject!!.readableShutterSpeeds.forEach{
            ShutterPopup.menu.add(it)
        }
        exposeShutterDropdown.setOnClickListener {
            ShutterPopup.show()
        }
        ShutterPopup.setOnMenuItemClickListener { item ->
            exposeShutterDropdown.text = item.title
            CameraObject?.setShutterSpeed(item.title.toString())
            true
        }

        val button:Button = findViewById(R.id.ChangeSwitch)
        button.setOnClickListener{
            val intent = Intent(this@ShutterPriorityActivity, AperturePriorityActivity::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }

        val settingsButton:MaterialButton = findViewById(R.id.SettingsSwitch)
        settingsButton.setOnClickListener {
            val intent = Intent(this@ShutterPriorityActivity, SettingsActivity::class.java)
            intent.putExtra("Camera", CameraObject)
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