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
    protected DigitalInput shooterPosition;

  /** 
   *  This method will push the disc into the spinning wheels.
   */
  public void shoot() {
  }

  public boolean isShooterUpToSpeed() 
  {
    return false;
  }

  public boolean isDiscLoaded() 
  {
    return false;
  }

    public boolean isShooterCocked() 
    {
        return shooterPosition.get();
    }
}