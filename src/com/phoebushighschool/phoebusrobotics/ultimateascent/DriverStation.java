/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 *
 * @author jmiller015
 */
public class DriverStation
{
    UltimateAscentBot robot;
    SmartDashboard dash;
    private int DiscsRemaing = robot.getDiscCount();
    private NamedSendable DiscsRemaining;
    private double distance = robot.getDistanceToTarget();
    private double degrees = robot.getDegreesToTarget();
    private boolean shooterReady = robot.isShooterCocked();
    
public void initDashboard()
    {
        dash.putData("Discs Remaining", DiscsRemaining);
        dash.putBoolean("Shooter Ready", shooterReady);
        dash.putNumber("Distance to target", distance);
        dash.putNumber("Degrees to target", degrees);
    }
           
    
}
