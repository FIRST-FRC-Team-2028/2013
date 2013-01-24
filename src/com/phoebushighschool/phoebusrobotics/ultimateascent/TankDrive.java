package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDOutput;

/*
 */
public class TankDrive implements PIDOutput {

    protected UltimateAscentBot robot;
    protected ClimbingSystem leftArm;
    protected Tread rightTread;
    protected Tread leftTread;

  /** 
   *  This method will take a setting in degrees, and turn the robot to face that heading.
   */
  public void turn(double percentSpeed) {
  }

  /** 
   *  This method will set the percent speed and direction of both treads.
   */
  public void drive(double percent) {
  }

  public void pidWrite(double percentTurnSpeed) {
  }

  public void setGear(Tread.Gear gear) {
  }

}