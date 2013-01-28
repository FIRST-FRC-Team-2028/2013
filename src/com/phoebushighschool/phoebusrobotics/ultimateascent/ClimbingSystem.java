package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    public Arm forwardArm;
    public Arm backArm;

    public ClimbingSystem() {
        forwardArm = new Arm();
        backArm = new Arm();
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
}