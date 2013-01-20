package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class Arm {

    protected boolean catchState = false;
    protected CANJaguar motor;
    protected DigitalInput latchSwitch;
    protected DigitalInput handOffSwitch;

    public Arm() 
    {
        latchSwitch = new DigitalInput(1,
                Parameters.latchLimitSwitchGPIOChannel);
        handOffSwitch = new DigitalInput(1
                , Parameters.handOffLimitSwitchGPIOChannel);
        try {
            motor = new CANJaguar(Parameters.ArmMovementSomething);
            motor.configMaxOutputVoltage(Parameters.MaxMotorOutputVoltage);
            motor.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public boolean extend()
    {
        try {
            motor.setX(12.0);
            if (!motor.getForwardLimitOK())
                motor.setX(0.0);
                System.out.println("All the way out!");
                return true;
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void retract() 
    {
        
    }
//
//    public boolean isLatched() {
//        if (latchSwitch.get()) {
//            catchState = true;
//        }
//        return catchState;
//    }

    public boolean isFullyExtended() throws CANTimeoutException {
        return motor.getForwardLimitOK();
    }

    public boolean isFullyRetracted() throws CANTimeoutException {
        return motor.getReverseLimitOK();
    }

    public void stop() {
        try {
            motor.setX(0.0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }
}