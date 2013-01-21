package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 * 
 */
public class Tread {

    protected CANJaguar motor;
    protected TankDrive drive;
  
    //constructors
    public Tread(TankDrive parent, int canID, int gearChannel) throws CANTimeoutException
    {
        motor = new CANJaguar(canID);
        drive = parent;
        gearShifter = new Solenoid( Parameters.crioRelayModule, gearChannel);
        
    }
    
  /** 
   *  this method sets the direction and speed of the tread.
   */
  public void drive(double percentSpeed) 
  {
      drive (percentSpeed);
  }

    /**
     *
     * @author Dunn
     */
    public class Gear {

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