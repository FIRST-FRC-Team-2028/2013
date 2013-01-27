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
    GameMech game;
    TankDrive drive;
    Indexer indexer;
    Shooter shooter;
    SmartDashboard dash;
    AimingSystem camera;
    private int DiscsRemaing = indexer.discCountCurrent;
    private NamedSendable DiscsRemaining;
    private double distance = camera.getDistanceToTarget();
    
public void initDashboard()
    {
        dash.putData("Discs Remaining", DiscsRemaining);
        dash.putBoolean("Shooter Ready", shooter.isShooterCocked());
        dash.putNumber("Distance to target", distance);
    }
           
    
}
