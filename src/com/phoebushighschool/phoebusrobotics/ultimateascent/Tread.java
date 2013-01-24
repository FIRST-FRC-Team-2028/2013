package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CANJaguar;

/*
 * 
 */
public class Tread {

    protected TankDrive drive;
      protected CANJaguar motor;
    protected Solenoid gearShifter;
public static class Gear {

  private static final int kLowValue = 1;

  private static final int kHighValue = 2;

  private final int value;

  public static final Gear kLow = new Gear(kLowValue);

  public static final Gear kHigh = new Gear(kHighValue);

  public Gear(int gear) {
      this.value = gear;
  }

}
  /** 
   *  this method sets the direction and speed of the tread.
   */
  public void drive(double percentSpeed) {
  }

  public void setGear(Tread.Gear gear) {
  }

}