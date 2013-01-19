package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class Arm {

  protected boolean catchState;

  protected CANJaguar motor;
  protected DigitalInput latchSwitch;
  protected DigitalInput outLimitSwitch;
  protected DigitalInput inLimitSwitch;
    
  public Arm()
  {
      latchSwitch = new DigitalInput(1, Parameters.latchLimitSwitch);
        try {
            motor = new CANJaguar(Parameters.ArmMovementSomething);
            motor.configMaxOutputVoltage(Parameters.MaxMotorOutputVoltage);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
  }

  public void extend() 
  {
      
  }

  public void retract() 
  {

  }

  public boolean isLatched() 
  {
      if(latchSwitch.get())
        catchState = true;
      else catchState = false;
  return catchState;
  }
  
  public boolean isFullyExtended()
  {
      return outLimitSwitch.get();
  }
  
  public boolean isFullyRetracted()
  {
      return inLimitSwitch.get();
  }

  public void stop()
  {
        try {
            motor.setX(0.0);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
  }

}