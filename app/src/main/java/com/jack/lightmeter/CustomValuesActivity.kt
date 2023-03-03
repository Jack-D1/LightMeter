package com.jack.lightmeter

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class CustomValuesActivity : AppCompatActivity() {
    var CameraObject:CameraSettings?=null
    var currentValues: MaterialTextView?=null
    var ValuesToEdit:String?=null
    var deleteDropdownButton:MaterialButton?=null
    var popup:PopupMenu?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_values)
        CameraObject = if(intent.extras?.getSerializable("Camera") != null){
            intent.extras?.getSerializable("Camera") as CameraSettings?
        }else {
            CameraSettings(0f, 1.0f, 50, this);
        }
        CameraObject?.context = this
        ValuesToEdit = if(intent.getStringExtra("Edit") != null){
            intent.getStringExtra("Edit")
        }else {
            "Something Went Wrong"
        }

        setTitle("Editing Custom "+ ValuesToEdit!! + " Values")

        currentValues = findViewById(R.id.current_custom_values)
        deleteDropdownButton = findViewById(R.id.delete_values_button)
        popup = PopupMenu(this, deleteDropdownButton)
        updateDropdownAndShown()
        val add_value_button: MaterialButton = findViewById(R.id.add_value_button)
        val enteredTextBox:TextInputLayout = findViewById(R.id.textField)


        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_popup)

        val title = dialog.findViewById<MaterialTextView>(R.id.dialogue_title)
        val closeButton = dialog.findViewById<MaterialButton>(R.id.dialogue_close_button)

        title.text = "Dialog Title"

        closeButton.setOnClickListener {
            dialog.dismiss()
        }






        add_value_button.setOnClickListener {
            val inputText = enteredTextBox.editText?.text.toString()
            addCustomValues(inputText)
            enteredTextBox.editText?.text?.clear()
            dialog.show()
        }





        enteredTextBox.editText?.setHint("Enter aperture value, ISO value or shutter speed")

        enteredTextBox.editText?.filters = arrayOf<InputFilter>(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {

                // Only allow digits, dot, and forward slash
                for (i in start until end) {
                    if (!Character.isDigit(source?.get(i)!!) && source[i] != '.' && source[i] != '/') {
                        return ""
                    }
                }
                return null
            }
        })




        deleteDropdownButton?.setOnClickListener {
            popup?.show()
        }

        popup?.setOnMenuItemClickListener { item ->
            deleteChosenValue(item.title.toString())
            true
        }
        val returnButton: MaterialButton = findViewById(R.id.return_button)
        returnButton.setOnClickListener{
            val intent = Intent(this@CustomValuesActivity, SettingsActivity::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }
    }

    private fun addCustomValues(value:String){
        when(ValuesToEdit){
            "Aperture" -> {
                CameraObject?.AddCustomAperture(value.toFloat())
            }
            "Shutter Speed" -> {
                CameraObject?.AddCustomShutterSpeed(value)
            }
            "ISO" -> {
                CameraObject?.AddCustomISO(value.toInt())
            }
        }
        updateDropdownAndShown()
    }
    private fun updateDropdownAndShown(){
        when(ValuesToEdit){
            "Aperture" -> {
                var ValuesAperture = ""
                CameraObject?.userDefinedApertures?.forEach{
                    ValuesAperture += "f/" + it.toString() + "\n"
                    popup?.menu?.add(it.toString())
                }
                currentValues?.text = if(ValuesAperture != ""){
                    ValuesAperture
                }else{
                    "No Custom Apertures"
                }
            }
            "Shutter Speed" -> {
                var ValuesShutter = ""
                CameraObject?.userDefinedShutterSpeeds?.forEach{
                    ValuesShutter+= it.key + "s\n"
                    popup?.menu?.add(it.key)
                }
                currentValues?.text = if(ValuesShutter != ""){
                    ValuesShutter
                }else{
                    "No Custom Shutter Speeds"
                }
            }
            "ISO" -> {
                var ValuesISO = ""
                CameraObject?.userDefinedISOs?.forEach {
                    ValuesISO += "ISO " + it.toString() + "\n"
                    popup?.menu?.add(it.toString())
                }
                currentValues?.text = if(ValuesISO != ""){
                    ValuesISO
                }else{
                    "No Custom ISO Values"
                }
            }

        }
    }

    private fun deleteChosenValue(value:String){
        when(ValuesToEdit){
            "Aperture" -> {
                CameraObject?.RemoveCustomAperture(value.toFloat())
            }
            "Shutter Speed" -> {
                CameraObject?.RemoveCustomshutterSpeed(value)
            }
            "ISO" -> {
                CameraObject?.RemoveCustomISO(value.toInt())
            }
        }
        updateDropdownAndShown()
    }

}