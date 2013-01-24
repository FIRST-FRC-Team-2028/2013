package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDOutput;

/*
 */
public class TankDrive implements PIDOutput {

    protected UltimateAscentBot robot;
    protected ClimbingSystem leftArm;
    protected Tread rightTread;
    protected Tread leftTread;
    
    public TankDrive()
    {
        leftArm = new ClimbingSystem();
    }

  /** 
   *  This method takes a joystick value, and turns the robot according to the value
   */
  public void turnByJoystick(double joystickInput) {
  }

  /** 
   *  This method will take a setting in degrees, and turn the robot to face that heading.
   */
  public void turnByDegrees(double degrees) {
  }

  /** 
   *  This method will set the percent speed and direction of both treads.
   */
  public void drive(double percent) {
  }

  public void pidWrite(double degreesToTarget) {
  }

}