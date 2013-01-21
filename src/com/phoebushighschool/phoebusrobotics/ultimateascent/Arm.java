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
  
    /**
     * this method sets the direction and speed of the tread.
     */
    public static class Direction {

        private final int value;
        private static final int kForwardValue = 1;
        private static final int kReverseValue = 2;
        public static final Direction kForward = new Direction(kForwardValue);
        public static final Direction kReverse = new Direction(kReverseValue);

        public Direction(int direction) 
        {
            this.value = direction;
        }
    }
}