package com.jack.lightmeter

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.jack.lightmeter.R.*
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager:SensorManager?= null
    var sensor:Sensor?=null
    var tv1:TextView?=null
    var tv2:TextView?=null
    var tv3:TextView?=null
    var textConstant:TextView?=null
    var c:Int?=null
    var ISODropdown:Spinner?=null
    val ShutterSpeeds = listOf<Float>(8f,4f,2f,1f,0.5f,0.25f,0.125f,0.066f,0.033f,0.0166f,0.008f,0.004f, 0.002f, 0.001f,0.0005f,0.00025f,0.000125f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        tv1 = findViewById(id.text)
        tv2 = findViewById(id.text_lux)
        tv3 = findViewById(id.text_ev)
        textConstant = findViewById(id.text_constant)
        ISODropdown = findViewById(id.ISO)
        c = 100
        var sb:SeekBar = findViewById(id.constant)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                textConstant?.text = "Constant " + sb.progress
                c = sb.progress
            }
        })
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
            var EV:Float
            lx = event!!.values[0]
            var ts:Float
            ts = (((16f * 16f) * c!!)) / (lx * 100f)
            EV = log(16.0 * 16.0 / ts.toDouble(),2.0).toFloat()
            var cloestTs: Float? = ShutterSpeeds.closestValue(ts)
            if(cloestTs!! >= 0.5f){
                tv1?.text = cloestTs.toString() + " seconds"
            }else{
                var denom:Float = 1f / cloestTs
                tv1?.text = "1/"+denom.toString() + "th of a second"
            }

            tv2?.text = lx.toString() + "lx"
            tv3?.text = "EV " + EV.toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    // Find the shutter speed closest to the one we meter to
    fun List<Float>.closestValue(value: Float) = minByOrNull { abs(value - it) }

}


