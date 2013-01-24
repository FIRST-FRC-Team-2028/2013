package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Timer;
import edu.wpi.first.wpilibj.CANJaguar;

/*
 */
/** 
 *  This class is responsible for controlling the hardware that sends one disc at a time on its way towards the target
 */
public class Shooter {

  protected Timer speedTimer;

    protected GameMech gameMech;
    protected DigitalInput discSensor;
    protected CANJaguar motor;

  /** 
   *  This method will turn the cam the small amount necesary for the arm to release from the cam and shoot the frisbee.
   */
  public void shoot() {
  }

  public boolean isShooterCocked() {
  return false;
  }

  public boolean isDiscLoaded() {
  return false;
  }

  /** 
   *  This method rotates the cam until the arm is ready to fire.
   */
  public void cock() {
  }

}