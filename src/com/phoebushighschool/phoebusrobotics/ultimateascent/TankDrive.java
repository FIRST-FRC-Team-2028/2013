package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 * TankDrive
 * 
 * This class controls the turning speed and angle side of the drive system
 * 
 * @author Anna
 */
public class TankDrive implements PIDOutput, PIDSource
{

    protected UltimateAscentBot robot;
    protected ClimbingSystem leftArm;
    protected Tread rightTread;
    protected Tread leftTread;
    public GyroSensor gyro;
    double speed;
    
  /**
   * TankDrive
   * 
   * constructor 
   * 
   * @throws CANTimeoutException 
   */
    public TankDrive() throws CANTimeoutException
    {
        rightTread = new Tread(this, Parameters.rightTreadCanID, Parameters.rightGearLowSolenoidChannel, Parameters.rightGearHighSolenoidChannel);
        leftTread = new Tread(this, Parameters.leftTreadCanID, Parameters.leftGearLowSolenoidChannel, Parameters.leftGearHighSolenoidChannel);
        rightTread.setGear(Tread.Gear.kLow);
        leftTread.setGear(Tread.Gear.kLow);
        gyro = new GyroSensor(Parameters.gyroAnalogChannel);
        
    }

  /** 
   * drive
   * 
   *  This method takes a percent value, and turns the robot according to 
   *  the value where positive values are to the right and negative values
   *  are to the left.
   * 
   * when robot is stationary: one tread moves forward while other tread moves 
   *                           in reverse
   * 
   * when robot is moving: ability to turn is greatly limited by speed of robot; 
   *                       the faster the robot is traveling, the ability to turn
   *                       is reduced
   * 
   * @param drivePercentPower - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not moving and 1.0 is moving full speed 
   *                      {left/right} and -1.0 is moving full speed {right/
   *                      left}
   * @param turnPercentPower - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   */
  public void drive(double drivePercentPower, double turnPercentPower) throws CANTimeoutException
  {
      double leftSpeed;
      double rightSpeed;
      
      //when in high gear, adjust drivePercentPower for higher gear ratio 
      
      double adjustedDrivePercentPower = drivePercentPower; 
      if (leftTread.isHighGear())
      {
          adjustedDrivePercentPower = drivePercentPower * 2.27; 
      }
      turnPercentPower = decayTurnPower(adjustedDrivePercentPower, turnPercentPower);
      
      if (Math.abs(turnPercentPower) + Math.abs(drivePercentPower) > 1.0 )
      {
          drivePercentPower = drivePercentPower / (drivePercentPower + turnPercentPower);
          turnPercentPower = turnPercentPower / (turnPercentPower + drivePercentPower);
      }
      
      
      
      leftSpeed = drivePercentPower + turnPercentPower;
      rightSpeed = drivePercentPower - turnPercentPower;
      
      leftTread.drive (leftSpeed); 
      rightTread.drive(rightSpeed);
  }

  /**
   * decayTurnPower
   * 
   * Mathematical logic/equation to determine value of power used to turn at any 
   *given point while robot is moving 
   * 
   * @param forwardPercentPower - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not moving and 1.0 is moving full speed 
   *                      {left/right} and -1.0 is moving full speed {right/
   *                      left}
   * @param turnPercentPower - number in the range of -1.0 .. 0.0 .. 1.0 where
   *                      0.0 is not turning and 1.0 is turning full speed 
   *                      {left/right} and -1.0 is turning full speed {right/
   *                      left}
   * @return 
   */
  public double decayTurnPower(double forwardPercentPower, double turnPercentPower)
  {
      double decayValue; 
      
      decayValue = (-1.0/(20.0 * FRCMath.pow(forwardPercentPower, 2) + 1.0)) + 1.0; 
      
      if (turnPercentPower > 0.0)
      {
          turnPercentPower -= decayValue; 
          if (turnPercentPower < 0.0)
          {
              turnPercentPower = 0.0; 
          }        
      }
      
      if (turnPercentPower < 0.0)
      {
          turnPercentPower += decayValue;
          if (turnPercentPower > 0.0)
          {
              turnPercentPower = 0.0;
          }     
      }
      
      return turnPercentPower;
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
        drive (0.0, speedToTurn);  
      }
      catch (CANTimeoutException e)
      {
          throw new RuntimeException(e.getMessage());
      }
  }
  
  /**
   * 
   * @return 
   */
  public double pidGet() {
      return gyro.pidGet();
  }
  
  /**
   * 
   * @param gear 
   */
  public void setGear(Tread.Gear gear)
  {
      leftTread.setGear( gear);
      rightTread.setGear(gear);
  }
  
  public Tread.Gear getGear()
  {
      return leftTread.getGear(); 
  }

}
