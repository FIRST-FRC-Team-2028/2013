package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.CANJaguar;

/*
 */
public class Arm {

  protected boolean catchState;

    protected CANJaguar motor;
  public static class Direction {

  private static final int kForwardValue = 1;

  private static final int kReverseValue = 2;

  private final int value;

  public static final Direction kForward = new Direction(kForwardValue);

  public static final Direction kReverse = new Direction(kReverseValue);

  public Direction(int direction) {
      this.value = direction;
  }

}
  public boolean extend() {
  return false;
  }

  public boolean retract() {
  return false;
  }

  public boolean isLatched() {
  return false;
  }

  public boolean isFullyExtended() {
  return false;
  }

  public boolean isFullyRetracted() {
  return false;
  }

  public void stop() {
  }

}