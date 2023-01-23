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
    val ISOs = listOf<String>("50", "100","200", "400", "800", "1600", "3200", "6400", "12800", "25600")
    val Apertures = listOf<String>("1.0","1.2", "1.4", "2.0", "2.8", "3.2", "3.5", "4.0", "4.5", "5.0", "5.6", "6.3", "7.1", "8.0", "9.0", "10.0", "11.0", "13.0", "14.0", "16.0", "18.0", "20.0", "22.0","32.0")
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
        val exposedDropdownButton = findViewById<MaterialButton>(R.id.exposed_dropdown_button)
        val popup = PopupMenu(this, exposedDropdownButton )
        for(iso in ISOs){
            popup.menu.add(iso)
        }
        exposedDropdownButton.setOnClickListener {
            popup.show()
        }
        popup.setOnMenuItemClickListener { item ->
            exposedDropdownButton.text = item.title
            CameraObject?.iso = item.title.toString().toInt()
            true
        }


//        val spinnerISO = findViewById<Spinner>(id.ISO)
        val spinnerAperture = findViewById<Spinner>(id.APERTURE)
//        if(spinnerISO != null){
//            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,ISOs)
//            spinnerISO.adapter = adapter
//            spinnerISO.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                    CameraObject?.iso = ISOs[position].toInt()
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                    // write code to perform some action
//                }
//            }
//        }

        if(spinnerAperture != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Apertures)
            spinnerAperture.adapter = adapter
            spinnerAperture.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    CameraObject?.aperture = Apertures[position].toFloat()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
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


