package com.jack.lightmeter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class CameraSettings implements Serializable {
    private Float ShutterSpeed;
    private Float Aperture;
    private Integer ISO;
    private Integer CalibrationConstant;
    private double EV;
    private ArrayList<Float> ValidShutterSpeeds;
    private ArrayList<Float> ValidApertures;

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
        return EV;
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
        this.ValidShutterSpeeds = new ArrayList<>(Arrays.asList(8f,4f,2f,1f,0.5f,0.25f,0.125f,0.066f,0.033f,0.0166f,0.008f,0.004f, 0.002f, 0.001f,0.0005f,0.00025f,0.000125f));
        this.ValidApertures = new ArrayList<>(Arrays.asList(1.0f,1.2f, 1.4f, 2.0f, 2.8f, 3.2f, 3.5f, 4.0f, 4.5f, 5.0f, 5.6f, 6.3f, 7.1f, 8.0f, 9.0f, 10.0f, 11.0f, 13.0f, 14.0f, 16.0f, 18.0f, 20.0f, 22.0f,32.0f));
    }


    public void setShutterSpeed(Float shutterSpeed) {
        ShutterSpeed = shutterSpeed;
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
            return "1/" + this.ShutterSpeed.toString() + "th of a Second";
        }
    }

    // Update the currently stored Shutter Speed in Aperture Priority Mode
    public void updateShutterSpeedInAP(Float Lux){
        // As prescribed by Equations for relation from camera settings to EV
        Float ts;
        ts = (((this.Aperture * this.Aperture) * this.CalibrationConstant)) / (Lux * this.ISO);
        // Get the closest valid shutter speed
        //TODO: Write own minByOrNull to get closest valid shutter speed
        ts = ts;
        // Update local shutter speed
        this.ShutterSpeed = ts;
        // Update local EV
        this.updateEV();
    }

    // Update the currently stored Aperture in Shutter Priority Mode
    public void updateApertureInSP(Float lux){
        // As prescribed by Equations for relation from camera settings to EV
        Double aper;
        aper = Math.sqrt(this.ShutterSpeed * (lux * this.ISO / this.CalibrationConstant));
        //Get the closest valid Aperture
        //TODO: Write own minByOrNull to get closest valid Aperture
        aper = aper;
        // Update local Aperture
        this.Aperture = aper.floatValue();
        // Update local EV
        this.updateEV();
    }


}
