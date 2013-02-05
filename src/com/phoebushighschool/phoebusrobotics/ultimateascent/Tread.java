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
    protected Solenoid gearShifter;

    //constructors
    public Tread(TankDrive parent, int canID, int gearChannel) throws CANTimeoutException 
    {
        motor = new CANJaguar(canID, CANJaguar.ControlMode.kPercentVbus);
        drive = parent;
        gearShifter = new Solenoid(Parameters.crioSolenoidModule, gearChannel);
    }

    /**
     * this method sets the direction and speed of the tread.
     * 
     * @param percentSpeed - This is the number in the range of -1.0 .. 0.0 .. 1.0
     *                       where 0.0 is not driving and 1.0 is driving full speed forward
     *                       and -1.0 is driving full speed in reverse 
     */
    public void drive(double percentSpeed) throws CANTimeoutException
    {
        motor.setX(percentSpeed);
    }
    /**
     * This method sets gear to low or high based on a boolean value (true/false) 
     * 
     * @param gear - true equals shifting to low gear and false equals shifting 
     *               to high gear
     */
    public void setGear (Gear gear)
    {
        if (gear == Gear.kLow)
        {
            gearShifter.set(false);     // FIX ME!!! Verify false is really low gear
        }
        else
        {
            gearShifter.set(true);      //FIX ME!!! Verify true is really high gear
        }
    }
    
    /**
     * 
     * @return double - ...
     */
    public double getSpeed() throws CANTimeoutException
    {
        return motor.getX();
    }
    
    /**
     *
     * @author Dunn
     */
    public static class Gear 
    {

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