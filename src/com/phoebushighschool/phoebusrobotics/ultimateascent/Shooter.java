package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CANJaguar;
import java.util.Timer;
/*
 */
/** 
 *  This class is responsible for controlling the hardware that sends one disc at a time on its way towards the target
 */
public class Shooter {

  protected Timer speedTimer;

    protected GameMech gameMech;
    protected CANJaguar secondDrive;
    protected CANJaguar firstDrive;
    protected DigitalInput discSensor;

  /** 
   *  This method will push the disc into the spinning wheels.
   */
  public void shoot() {
  }

  /** 
   *  This method will start the wheels so that the disc can be launched.
   */
  public void startWheels() {
  }

  public void stopWheeels() {
  }

  public boolean isShooterUpToSpeed() 
  {
    return false;
  }

  public boolean isDiscLoaded() 
  {
    return false;
  }

}