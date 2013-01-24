package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 */
public class TankDrive implements PIDOutput 
{

    protected UltimateAscentBot robot;
    protected ClimbingSystem leftArm;
    protected Tread rightTread;
    protected Tread leftTread;
    double speed;
    
    //constructors
    public TankDrive() throws CANTimeoutException
    {
        rightTread = new Tread(this, Parameters.rightTreadCanID, Parameters.rightGearShifterSolenoidChannel);
        leftTread = new Tread(this, Parameters.leftTreadCanID, Parameters.leftGearShifterSolenoidChannel);
        
    }

  /** 
   *  This method takes a percent value, and turns the robot according to 
   *  the value where positive values are to the right and negative values
   *  are to the left.
   * 
   * when robot is stationary: one tread moves forward while other tread moves 
   *                           in reverse
   * 
   * when robot is moving: 
   * 
   * @param speedToTurn - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   */
  public void turn(double speedToTurn) throws CANTimeoutException
  {
      if (speed == 0.0)
      {
          leftTread.drive(speedToTurn);
          rightTread.drive(speedToTurn * -1.0);
      }
      else
      {
          
      }
  }

  /** 
   *  This method will set the percent speed and direction of both treads.
   */
  public void drive(double percent) throws CANTimeoutException
  {
      speed = percent;
      leftTread.drive (percent); 
      rightTread.drive(percent);
  }

  /**
   * This method takes a joystick value, and turns the robot according to 
   *  the value
   * 
   * @param speedToTurn - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   */
  public void pidWrite(double speedToTurn)
  {
      try
      {
        turn (speedToTurn);  
      }
      catch (CANTimeoutException e)
      {
          throw new RuntimeException(e.getMessage());
      }
  }

}
