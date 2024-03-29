package com.jack.lightmeter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class AperturePriorityActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager: SensorManager?= null
    var sensor: Sensor?=null
    var CameraObject:CameraSettings?=null
    // Shutter Speed Text Box
    var ShutterSpeedText: MaterialTextView?=null
    // Lux Text Representation
    var LuxText: MaterialTextView?=null
    // EV Text
    var EVText: MaterialTextView?=null
    var AperturePopup:PopupMenu?=null
    var ISOpopup:PopupMenu?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aperture_priority)
        //Get Camera Settings object from other activity
        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50, this);
        }
        CameraObject?.context = this
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        ShutterSpeedText = findViewById(R.id.ShutterSpeedReading)
        LuxText = findViewById(R.id.text_lux)
        EVText = findViewById(R.id.text_ev)
        val exposeISODropdown = findViewById<MaterialButton>(R.id.expose_iso_dropdown_button)
        ISOpopup = PopupMenu(this, exposeISODropdown )

        exposeISODropdown.setOnClickListener {
            ISOpopup?.show()
        }
        ISOpopup?.setOnMenuItemClickListener { item ->
            exposeISODropdown.text = "ISO " + item.title
            CameraObject?.iso = item.title.toString().toInt()
            true
        }


        val exposeApertureDropdown = findViewById<MaterialButton>(R.id.expose_aperture_dropdown_button)
        AperturePopup = PopupMenu(this, exposeApertureDropdown)

        exposeApertureDropdown.setOnClickListener {
            AperturePopup?.show()
        }
        AperturePopup?.setOnMenuItemClickListener { item ->
            exposeApertureDropdown.text = "f/" + item.title
            CameraObject?.aperture = item.title.toString().toFloat()
            true
        }
        CameraObject!!.getValidApertures().forEach{
            AperturePopup?.menu?.add(it.toString())
        }
        CameraObject!!.getValidISOs().forEach{
            ISOpopup?.menu?.add(it.toString())
        }


        val button: MaterialButton = findViewById(R.id.ChangeSwitch)
        button.setOnClickListener{
            val intent = Intent(this@AperturePriorityActivity, ShutterPriorityActivity::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }
        val settingsButton: MaterialButton = findViewById(R.id.SettingsSwitch)
        settingsButton.setOnClickListener {
            val intent = Intent(this@AperturePriorityActivity, SettingsActivity::class.java)
            intent.putExtra("Camera", CameraObject)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this,sensor, SensorManager.SENSOR_DELAY_FASTEST)
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
            ShutterSpeedText?.text = CameraObject?.formattedShutterSpeed
            // Show the lux reading
            LuxText?.text = lx.toString() + "lx"
            // Show the measured EV as calculated by the CameraSettings object
            EVText?.text = "EV " + CameraObject?.ev
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}