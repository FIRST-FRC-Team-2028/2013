package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class Arm {

    protected boolean catchState = false;
    protected CANJaguar motor;
//    protected DigitalInput latchSwitch;
//    protected DigitalInput handOffSwitch;

    public Arm(int armCANID) {
//        latchSwitch = new DigitalInput(1,
//                Parameters.latchLimitSwitchGPIOChannel);
//        handOffSwitch = new DigitalInput(1
//                , Parameters.handOffLimitSwitchGPIOChannel);
        try {
            motor = new CANJaguar(armCANID);
            motor.configMaxOutputVoltage(Parameters.MaxMotorOutputVoltage);
            motor.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
            motor.configNeutralMode(CANJaguar.NeutralMode.kBrake);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public boolean extend() throws CANTimeoutException {
        motor.setX(1.0);
        if (isFullyExtended()) {
            motor.setX(0.0);
            System.out.println("All the way out!");
            return true;
        }
        return false;
    }

    public boolean retract() throws CANTimeoutException {
        if (isFullyRetracted()) {
            motor.setX(0.0);
            return true;
            }   
        else {
            motor.setX(-1.0);
        }
        return false;
    }
//       too narrow
//    public boolean isLatched() {
//        if (latchSwitch.get()) {
//            catchState = true;
//        }
//        return catchState;
//    }


    public boolean isFullyExtended() throws CANTimeoutException {
        return !motor.getForwardLimitOK();
    }

    public boolean isFullyRetracted() throws CANTimeoutException {
        return !motor.getReverseLimitOK();
    }

    public void stop() {
        try {
            motor.setX(0.0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();

        }
    }
}
