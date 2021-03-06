package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Timer;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.*;
/*
 */

/**
 * Shooter
 * 
 * This class is responsible for controlling the hardware that sends one disc at
 * a time on its way towards the target
 * 
 * @author Jonathan
 */
public class Shooter {

    protected Timer speedTimer;
    protected GameMech gameMech;
    protected CANJaguar motor;
    protected DigitalInput discSensor;
    protected DigitalInput shooterCockedSensor;
    protected DigitalInput shooterRetractedSensor;

    public Shooter() throws CANTimeoutException {
        motor = new CANJaguar(Parameters.WheelOneCANJaguarCANID, CANJaguar.ControlMode.kPercentVbus);
        motor.configMaxOutputVoltage(Parameters.MaxMotorOutputVoltage);
        motor.configNeutralMode(CANJaguar.NeutralMode.kBrake);
        discSensor = new DigitalInput(Parameters.DiscInShooterGPIOChannel);
        shooterCockedSensor = new DigitalInput(Parameters.ShooterIsCockedGPIOChannel);
        shooterRetractedSensor = new DigitalInput(Parameters.ShooterIsRetractedGPIOChannel);

    }

    /**
     * shoot()
     * 
     * This method will move the cam until the arm is released.  It will stop
     * the cam once the arm reaches the "retracted" position.
     * 
     * @return true - shooter is now in the retracted position
     *         false - shooter is still moving, not yet in retracted position
     * 
     * @exception CANTimeoutException - communication with Jaguar over the
     *                                  CAN bus was lost
     */
    public boolean shoot() throws CANTimeoutException {
       
        //If the shooter is not cocked, then the shooter will not shoot the disc (Herp derp)
        if (isShooterRetracted()) {
            motor.setX(0.0);
            return true;
            
        } else {
            motor.setX(Parameters.kShooterMotorSpeed); 
            return false; 
        }
    }

    /**
     * cockShooter()
     * 
     * This method will move the cam until the arm reaches the "cocked"
     * position.
     * 
     * @return true - shooter is now in the cocked position
     *         false - shooter is still moving, not yet in cocked position
     * 
     * @exception CANTimeoutException - communication with Jaguar over the
     *                                  CAN bus was lost
     */
    public boolean cockShooter() throws CANTimeoutException {
        if (isShooterCocked()) {
            motor.setX(0.0);
            return true;
        }
        motor.setX(Parameters.kShooterMotorSpeed);
        return false;
    }

    /**
     * shooterCockedSensor()
     * 
     * Getter method to return the state of the shooter arming mechanism.
     * 
     * @return true - the cam is in the "cocked" position
     *         false - the cam is not in the "cocked" position
     */
    public boolean isShooterCocked() throws CANTimeoutException {
        return cockShooter();
    }

    /**
     * shooterRetractedSensor() 
     * 
     * Method to return the state of the shooter being retracted.
     * 
     * @return true - the cam is in the "retracted" position
     *         false - the cam is not in the "retracted" position 
     */
    public boolean isShooterRetracted() {
        return shooterRetractedSensor.get();
    }

    /**
     * isDiscLoaded() 
     * 
     * Method to return the state of the disc being loaded.
     * 
     * @return true - the disc is in the "loaded" position
     *         false - the disc is not in the "loaded" position
     */
    public boolean isDiscLoaded() {
        return discSensor.get();
    }
    
    /**
     * 
     * @param value
     * @throws CANTimeoutException 
     */
    public void setShooterMotor(boolean value) throws CANTimeoutException 
    {
        if (value)
        {
            motor.setX(Parameters.kShooterMotorSpeed);
        }
        else
        {
            motor.setX(0.0);
        }
    }
    /**
     *moveShooterManual()
     * 
     * This method will move the cam for the shooter at the speed designated by 
     * Parameters.
     * 
     * @param canMove
     * @throws CANTimeoutException 
     */
    public void moveShooterManual(boolean canMove, boolean forward) throws CANTimeoutException
    {
        if (canMove && forward)
            motor.setX(Parameters.kShooterMotorSpeed);
        else if (canMove)
        {
            motor.setX(Parameters.kShooterMotorSpeed * -1.0);
        }
        else
            motor.setX(0.0);
    }
}