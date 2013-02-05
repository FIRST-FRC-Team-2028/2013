/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 *
 * @author jmiller015
 */
public class SmartDashBoard
{
    UltimateAscentBot robot;
    SmartDashboard dash; 
    
    /**
     * SmartDashBoard
     * 
     * This constructor initializes the SmartDashBoard and gives it an
     * UltimateAscentBot
     * 
     * @param bot 
     */
    public SmartDashBoard(UltimateAscentBot bot)
    {
        robot = bot;
        dash = new SmartDashboard();
    }
    
    
    /**
     * updateDashboard()
     * 
     * This method will update the Driver Station to the current values for 
     * the # of discs, true/false shooter ready, distance to target, 
     * degrees to target.
     */
    public void updateDashboard()
    {
        dash.putNumber("Discs Remaining", robot.getDiscCount());
        dash.putBoolean("Shooter Ready", robot.isShooterCocked());
        dash.putBoolean("Shooter Loaded", robot.isShooterLoaded());
        dash.putNumber("Distance to target", robot.getDistanceToTarget());
        dash.putNumber("Degrees to target", robot.getDegreesToTarget());
        dash.putString("Arm:", robot.getArmState());
        dash.putString("We are doing this:", robot.autonState);
    }
           
    
}
