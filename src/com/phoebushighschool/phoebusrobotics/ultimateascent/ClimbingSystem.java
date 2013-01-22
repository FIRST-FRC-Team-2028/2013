package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    public Arm arm;
    /**
     *
     * @element-type climbWheel
     */
    public climbWheel wheel;

    public ClimbingSystem() {
        arm = new Arm();
    }

    /**
     * This method will extend the arms far enough to reach the next level
     */
    public boolean extendToLatch() {
        if (arm.extend()) {
            return true;
        }
        return false;
    }

    /**
     * This method will retract the arms to within the frame perimeter
     */
    public void retract() throws CANTimeoutException {
        if (extendToLatch()) {
            arm.retract();
        }
    }
}