package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

public class Arm {

  protected boolean catchState;

    protected Relay motor;
      protected DigitalInput latched;

  public void start(Direction direction) {
  }

  public void stop() {
  }

  public boolean isLatched() {
  return false;
  }

}