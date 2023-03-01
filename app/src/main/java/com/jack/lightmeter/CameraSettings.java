package com.jack.lightmeter;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CameraSettings implements Serializable {
    private Float ShutterSpeed;
    private transient Context context;
    private Float Aperture;
    private Integer ISO;
    private Integer CalibrationConstant;
    private double EV;
    // This HashMap and the subsequent two ArrayLists store the valid predefined Shutter Speeds, ISOs and Apertures
    private HashMap<String, Float> ValidShutterSpeeds;
    private ArrayList<Float> ValidApertures;
    private ArrayList<Integer> ValidISOs;
    // This HashMap and the subsequent two ArrayLists store the user defined Shutter Speeds, Apertures and ISOs
    private HashMap<String, Float> UserDefinedShutterSpeeds;
    private ArrayList<Float> UserDefinedApertures;
    private ArrayList<Integer> UserDefinedISOs;
    public Integer getCalibrationConstant() {
        return CalibrationConstant;
    }

    public Float getShutterSpeed() {
        return ShutterSpeed;
    }

    public void setCalibrationConstant(Integer calibrationConstant) {
        CalibrationConstant = calibrationConstant;
    }

    public double getEV() {
        return Math.round(EV);
    }

    public void updateEV(){
        this.EV = (Math.log((this.Aperture * this.Aperture) / this.ShutterSpeed) / Math.log(2));
    }

    public CameraSettings(Float shutterSpeed, Float aperture, Integer ISO) {
        this.ShutterSpeed = shutterSpeed;
        this.Aperture = aperture;
        this.ISO = ISO;
        //We use 150 as during early testing it appeared to be the most accurate, will allow user to change the value in settings for this
        this.CalibrationConstant = 150;
        //List of known Valid Shutter Speeds, use this for rounding to closest. Allow user to define custom values by allowing them to add to this list.
        this.ValidApertures = new ArrayList<>(Arrays.asList(1.0f,1.2f, 1.4f, 2.0f, 2.8f, 3.2f, 3.5f, 4.0f, 4.5f, 5.0f, 5.6f, 6.3f, 7.1f, 8.0f, 9.0f, 10.0f, 11.0f, 13.0f, 14.0f, 16.0f, 18.0f, 20.0f, 22.0f,32.0f));
        this.ValidISOs = new ArrayList<>(Arrays.asList(50, 100,200, 400, 800, 1600, 3200, 6400, 12800, 25600));
         this.ValidShutterSpeeds = new HashMap<String, Float>() {{
            put("8", 8f);
            put("4", 4f);
            put("2", 2f);
            put("1", 1f);
            put("1/2", 0.5f);
            put("1/4", 0.25f);
            put("1/8", 0.125f);
            put("1/15", 0.066f);
            put("1/30", 0.033f);
            put("1/60", 0.017f);
            put("1/125", 0.008f);
            put("1/250", 0.004f);
            put("1/500", 0.002f);
            put("1/1000", 0.001f);
            put("1/2000", 0.0005f);
            put("1/4000", 0.00025f);
            put("1/8000", 0.000125f);
        }};
         this.UserDefinedApertures = new ArrayList<>();
         this.ReadStoredUserValues();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Integer> getValidISOs() {
        return ValidISOs;
    }

    public void setValidISOs(ArrayList<Integer> validISOs) {
        ValidISOs = validISOs;
    }

    public void setShutterSpeed(String shutterSpeed) {
        this.ShutterSpeed = this.ValidShutterSpeeds.get(shutterSpeed);
    }

    public Float getAperture() {
        return Aperture;
    }

    public void setAperture(Float aperture) {
        Aperture = aperture;
    }

    public Integer getISO(){
        return this.ISO;
    }

    public void setISO(Integer ISO) {
        this.ISO = ISO;
    }

    public String getFormattedShutterSpeed(){
        if (this.ShutterSpeed >= 0.5f) {
            return this.ShutterSpeed.toString() + " Seconds";
        }else{
            return "1/" + Math.round(1 / this.ShutterSpeed) + "th of a Second";
        }
    }

    public ArrayList<Float> getValidApertures() {
        return ValidApertures;
    }

    public ArrayList<String> getReadableShutterSpeeds(){
        ArrayList<String> readableShutterSpeeds = new ArrayList<String>(this.ValidShutterSpeeds.keySet());
        Collections.sort(readableShutterSpeeds, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                float shutterSpeed1 = convertShutterSpeedToFloat(s1);
                float shutterSpeed2 = convertShutterSpeedToFloat(s2);
                if (shutterSpeed1 < shutterSpeed2) {
                    return 1;
                } else if (shutterSpeed1 > shutterSpeed2) {
                    return -1;
                }
                return 0;
            }
        });
        return readableShutterSpeeds;
    }
    private float convertShutterSpeedToFloat(String shutterSpeed){
        if (shutterSpeed.contains("/")) {
            String[] parts = shutterSpeed.split("/");
            float numerator = Float.parseFloat(parts[0]);
            float denominator = Float.parseFloat(parts[1]);
            return numerator / denominator;
        } else {
            return Float.parseFloat(shutterSpeed);
        }
    }
    public void setValidApertures(ArrayList<Float> validApertures) {
        ValidApertures = validApertures;
    }

    // Update the currently stored Shutter Speed in Aperture Priority Mode
    public void updateShutterSpeedInAP(Float Lux){
        // As prescribed by Equations for relation from camera settings to EV
        Float ts;
        ts = (((this.Aperture * this.Aperture) * this.CalibrationConstant)) / (Lux * this.ISO);
        // Update local shutter speed & Get the closest valid shutter speed, divide it into one for the fractional form
        this.ShutterSpeed = this.getClosestValue(new ArrayList<Float>(this.ValidShutterSpeeds.values()), ts);
        // Update local EV
        this.updateEV();
    }

    // Update the currently stored Aperture in Shutter Priority Mode
    public void updateApertureInSP(Float lux){
        // As prescribed by Equations for relation from camera settings to EV
        Double aper;
        aper = Math.sqrt(this.ShutterSpeed * (lux * this.ISO / this.CalibrationConstant));
        // Update local Aperture & get closest valid aperture
        this.Aperture = this.getClosestValue(this.ValidApertures, aper.floatValue());
        // Update local EV
        this.updateEV();
    }

    private Float getClosestValue(ArrayList<Float> list, Float val){
        Float firstValue = list.get(0);
        Float currentDifference = Math.abs(firstValue - val);
        int index = 0;
        for(int i = 1; i < list.size(); i++){
            Float delta = Math.abs(list.get(i) - val);
            if(delta < currentDifference){
                currentDifference = delta;
                index = i;
            }
        }

        return list.get(index);
    }

    //This function reads in from the disk the user defined shutter speed, apertures and isos
    // Returns 0 on success, 1 on error
    private int ReadStoredUserValues(){
        //TODO: ReadFrom Disk the UserDefined values
        return 0;
    }
    // This function writes the use defined values to the disk
    // Returns 0 on success, 1 on error 
    private int WriteUserValues(){
        //TODO: Write to the Disk the UserDefined values
        return 0;
    }
}
