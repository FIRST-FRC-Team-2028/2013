package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.AnalogModule;
/*
 * 
 */

public class Ultrasonic {

    AnalogModule ultrasonic;
    protected int channel;

    public Ultrasonic(int analogChannel) {
        channel = analogChannel;
        ultrasonic = AnalogModule.getInstance(1);
    }

    /**
     * This method will find the distance to the closest thing in the cone of
     * view.
     */
    public double getDistance() {
        double value = ultrasonic.getVoltage(channel);
        return (value / 512.0);
    }

    public double pidGet() {
        return getDistance();
    }
    public double getVoltage() {
        return ultrasonic.getVoltage(channel);
    }
}