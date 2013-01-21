package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 */
public class TankDrive implements PIDOutput {

    protected UltimateAscentBot robot;
    protected ClimbingSystem leftArm;
    protected Tread rightTread;
    protected Tread leftTread;
    
    //constructors
    public TankDrive() throws CANTimeoutException
    {
        rightTread = new Tread(this, Parameters.rightTreadCanID, Parameters.rightGearShifterSolenoidChannel);
        leftTread = new Tread(this, Parameters.leftTreadCanID, Parameters.leftGearShifterSolenoidChannel);
        
    }

  /** 
   *  This method takes a joystick value, and turns the robot according to 
   *  the value
   * 
   * 
   * @param speedToTurn - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   */
  public void turn(double speedToTurn) 
  {
      turn (speedToTurn);
  }

  /** 
   *  This method will set the percent speed and direction of both treads.
   */
  public void drive(double percent) 
  {
      drive (percent); 
  }

  /**
   * 
   * 
   * @param speedToTurn - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   */
  public void pidWrite(double speedToTurn) 
  {
    turn (speedToTurn);  
  }

}