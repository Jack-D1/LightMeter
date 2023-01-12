package com.jack.lightmeter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.log

class ShutterPriority : AppCompatActivity(), SensorEventListener {
    var sensorManager: SensorManager?= null
    var sensor: Sensor?=null
    var CameraObject:CameraSettings?=null
    var tv1: TextView?=null
    var tv2: TextView?=null
    var tv3: TextView?=null
    val ShutterSpeeds = listOf<Float>(8f,4f,2f,1f,0.5f,0.25f,0.125f,0.066f,0.033f,0.0166f,0.008f,0.004f, 0.002f, 0.001f,0.0005f,0.00025f,0.000125f)
    val ReadableShutterSpeeds = listOf<String>("8","4","2", "1", "0.5", "1/4", "1/8", "1/15", "1/30", "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000","1/8000")
    val ISOs = listOf<String>("50", "100","200", "400", "800", "1600", "3200", "6400", "12800", "25600")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shutter_priority)
        //Get Camera Settings object from Aperture Priority activity
        CameraObject = if(savedInstanceState?.get("Camera") != null){
            savedInstanceState?.get("Camera") as CameraSettings?
        }else {
            CameraSettings(8f,0f,50)
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        tv1 = findViewById(R.id.text)
        tv2 = findViewById(R.id.text_lux)
        tv3 = findViewById(R.id.text_ev)
        val spinnerISO = findViewById<Spinner>(R.id.ISO)
        val spinnerShutter = findViewById<Spinner>(R.id.SHUTTER)
        if(spinnerISO != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,ISOs)
            spinnerISO.adapter = adapter
            spinnerISO.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    CameraObject?.iso = ISOs[position].toInt()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        if(spinnerShutter != null){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ReadableShutterSpeeds)
            spinnerShutter.adapter = adapter
            spinnerShutter.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    CameraObject?.shutterSpeed = ShutterSpeeds[position].toFloat()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
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
            lx = event!!.values[0]
            CameraObject?.updateApertureInSP(lx)
            tv1?.text = "f/" + CameraObject?.aperture.toString()
            tv2?.text = lx.toString() + "lx"
            tv3?.text = "EV " + CameraObject?.ev.toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    // Find the shutter speed closest to the one we meter to
    fun List<Float>.closestValue(value: Float) = minByOrNull { abs(value - it) }
}