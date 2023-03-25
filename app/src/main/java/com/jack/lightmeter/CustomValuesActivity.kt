package com.jack.lightmeter

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        add_value_button.setOnClickListener {
            val inputText = enteredTextBox.editText?.text.toString()
            if(inputText.isNotEmpty()){
                when(addCustomValues(inputText)) {
                    0 -> {
                        title.text = "Value Added Successfully"
                    }
                    1 -> {
                        title.text = "Read/Write Error When Saving Value, please try again"
                    }
                    2 -> {
                        title.text = "Value already exists, nothing added"
                    }
                    3 -> {
                        title.text = "Not a valid value, please try again"
                    }
                }
            }else {
                title.text = "Cannot submit an empty value"
            }
            updateDropdownAndShown()
            enteredTextBox.editText?.text?.clear()
            dialog.show()
        }

        deleteDropdownButton?.setOnClickListener {
            popup?.show()
        }

        popup?.setOnMenuItemClickListener { item ->
            when(deleteChosenValue(item.title.toString())){
                0 -> {
                    title.text = "Value Deleted Successfully"
                }
                1 -> {
                    title.text = "Read/Write Error when saving, please try again"
                }
                2 -> {
                    title.text = "Cannot find the value to delete. Please restart the App and try again"
                }
            }
            updateDropdownAndShown()
            dialog.show()
            true
        }
        val returnButton: MaterialButton = findViewById(R.id.return_button)
        returnButton.setOnClickListener{
            val intent = Intent(this@CustomValuesActivity, SettingsActivity::class.java)
            intent.putExtra("Camera",CameraObject)
            startActivity(intent)
        }
    }

    private fun addCustomValues(value:String): Int{
        when(ValuesToEdit){
            "Aperture" -> {
                if(value.contains("/")){
                    return 3;
                }
                return CameraObject?.AddCustomAperture(value.toFloat())!!
            }
            "Shutter Speed" -> {
                if(value.contains(".")){
                    return 3;
                }
                return CameraObject?.AddCustomShutterSpeed(value)!!
            }
            "ISO" -> {
                if(value.contains("/") || value.contains(".")){
                    return 3;
                }
                return CameraObject?.AddCustomISO(value.toInt())!!
            }
            else -> {
                return 1
            }
        }
    }
    private fun updateDropdownAndShown(){
        popup?.menu?.clear()
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

    private fun deleteChosenValue(value:String): Int{
        when(ValuesToEdit){
            "Aperture" -> {
                return CameraObject?.RemoveCustomAperture(value.toFloat())!!
            }
            "Shutter Speed" -> {
                return CameraObject?.RemoveCustomshutterSpeed(value)!!
            }
            "ISO" -> {
                return CameraObject?.RemoveCustomISO(value.toInt())!!
            }
            else -> {
                return 2
            }
        }
    }

}