package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    public Arm forwardArm;
    public Arm backArm;

    public ClimbingSystem() {
        forwardArm = new Arm(Parameters.forwardArmMovementCANID);
        backArm = new Arm(Parameters.backArmMovementCANID);
    }

    /**
     * This method will extend the arms far enough to reach the next level
     */
    public boolean extendToLatch() throws CANTimeoutException {
        if (forwardArm.extend() && backArm.extend()) {
            return true;
        }
        return false;
    }

    /**
     * This method will retract the arms to within the frame perimeter
     */
    public void retract() throws CANTimeoutException {
            forwardArm.retract();
            backArm.retract();
        
    }
    
    /**
     * moveForwardArmByJoystick()
     * 
     * This method will take a joystick value and move the forward arm.
     * 
     * @param forwardJoystickValue
     * @throws CANTimeoutException 
     */
    public void moveForwardArmByJoystick(double forwardJoystickValue) throws CANTimeoutException
    {
        forwardArm.moveByValue(forwardJoystickValue);
    }
    /**
     * moveBackArmByJoystick()
     * 
     * This method will take a joystick value and move the back arm.
     * 
     * @param backJoystickValue
     * @throws CANTimeoutException 
     */
    public void moveBackArmByJoystick(double backJoystickValue) throws CANTimeoutException     
    {
        backArm.moveByValue(backJoystickValue);
    }
    
    /**
     *getArmState()
     * 
     * This method will return the state of the forward arm, extended or retracted.
     * 
     * @return 
     */
    public String getArmState()
    {
        return forwardArm.armState;
    }
}