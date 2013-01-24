package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.CANJaguar;

/*
 * 
 */
public class Tread {

    protected CANJaguar motor;
    protected TankDrive drive;
  
  /** 
   *  this method sets the direction and speed of the tread.
   */
  public void drive(double percentSpeed) {
  }

    /**
     *
     * @author Dunn
     */
    public static class Gear {

        private static final int kLowValue = 1;
        private static final int kHighValue = 2;
        private final int value;
        public static final Gear kLow = new Gear(Gear.kLowValue);
        public static final Gear kHigh = new Gear(Gear.kHighValue);

        protected Gear(int gear) 
        {
            this.value = gear;
        }
    }
}