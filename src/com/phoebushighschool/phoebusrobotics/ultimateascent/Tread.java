package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/**
 * Tread
 * 
 * This class controls all the hardware for one side of the drive system
 * 
 * @author Anna
 */
public class Tread {

    protected TankDrive drive;
    protected Solenoid gearShifter;
    CANJaguar motor;

    /**
     * Tread 
     * 
     * Constructor 
     * 
     * @param parent - TankDrive that this Tread is part of
     * @param canID - The channel number for the CANJaguar
     * @param gearChannel - The solenoid channel for the gear selector 
     * @throws CANTimeoutException - when communication with the jaguar fails over the CAN bus
     */ 
    public Tread(TankDrive parent, int canID, int lowGearChannel, int highGearChannel) throws CANTimeoutException 
    {
        motor = new CANJaguar(canID, CANJaguar.ControlMode.kPercentVbus);
        motor.configMaxOutputVoltage(Parameters.MaxMotorOutputVoltage);
        motor.configNeutralMode(CANJaguar.NeutralMode.kBrake);
        drive = parent;
        gearShifter = new Solenoid(Parameters.crioRelayModule); 
        setGear(Gear.kLow); 
    }

    /**
     * drive
     * 
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
     * setGear
     * 
     * This method sets gear to low or high based on a boolean value (true/false) 
     * 
     * @param gear - true equals shifting to low gear and false equals shifting 
     *               to high gear
     */
    public void setGear (Gear gear)
    {
        if (gear == Gear.kLow)
        {
            gearShifter.set(false);
        }
        else
        {
            gearShifter.set(true); 
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
     *  isLowGear
     * 
     * This method returns true if in low gear or false if in high gear
     * 
     * @return 
     */
    public boolean isLowGear() //FIX ME!!! 
    {
        if (gearShifter.get() == false)     //FIX ME!!! Verify true is really high gear
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * isHighGear
     * 
     * This method returns true if in high gear or false if in low gear
     * 
     * @return
     */
    public boolean isHighGear()
    {
        if (gearShifter.get() == true)     //FIX ME!!! Verify true is really high gear
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 
     * @return 
     */
    public Gear getGear() 
    {
        if (isHighGear())
        {
            return Gear.kHigh;
        }
        return Gear.kLow;
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
