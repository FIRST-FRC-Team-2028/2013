package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

public class Arm {

  protected boolean catchState;

    protected Relay motor;
    protected DigitalInput latchSwitch;
  public Arm(int latchSwitchchannel)
  {
      latchSwitch = new DigitalInput(1, latchSwitchchannel);
  }

  public void extend(boolean limitOut) 
  {
      motor.setDirection(Relay.Direction.kForward);
      if (isFullyExtended(limitOut))
          motor.set(Relay.Value.kOff);
      else
          motor.set(Relay.Value.kOn);
  }

  public void retract(boolean limitIn) 
  {
      motor.setDirection(Relay.Direction.kReverse);
      if(isFullyRetracted(limitIn))
      {
          stop();
      }
  }

  public boolean isLatched() 
  {
      if(latchSwitch.get())
        catchState = true;
      else catchState = false;
  return catchState;
  }
  
  public boolean isFullyExtended(boolean limitOut)
  {
      return false;
  }
  
  public boolean isFullyRetracted(boolean limitIn)
  {
      return false;
  }

  public void stop()
  {
      motor.set(Relay.Value.kOff);
  }
}