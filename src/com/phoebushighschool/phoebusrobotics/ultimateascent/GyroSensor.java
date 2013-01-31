/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDSource;
/**
 *
 * @author djennings001
 */
public class GyroSensor extends Gyro implements PIDSource {
    
    public GyroSensor(int channel) {
        super(channel);
    }
    
    /**
     * getAngle()
     * 
     * This method returns the angle the robot has turned from 0.0.
     * 
     * @return double - an angle in degrees, not limited to -360.0 - 0.0 - 360.0
     */
    public double getAngle() {
        return super.getAngle();
    }
    
    /**
     * readAngle()
     * 
     * This method returns the angle the robot has turned from 0.0;
     * @return double - an angle limited to -360.0 - 0.0 - 360.0
     */
    public double readAngle() {
        double temp = getAngle();
        temp = temp % 360.0;
        return temp;
    }
    
    public double pidGet() {
        return readAngle();
    }
}
