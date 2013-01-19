package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Vector;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    protected DigitalInput outLimitSwitch;
    protected DigitalInput inLimitSwitch;
    public Arm arm;
    /**
   * 
   * @element-type climbWheel
   */
  public Vector  wheel;

  /** 
   *  This method will check the limit switch to see if the arm is extended.
   */
  public boolean isExtended() {
  return false;
  }

  /** 
   *  This method will check to see if the arms are fully retracted.
   */
  public boolean isRetracted() {
  return false;
  }

  /** 
   *  This method will extend the arms far enough to reach the first level
   */
  public void extend() {
  }

  /** 
   *  This method will retract the arms to within the frame perimeter
   */
  public void retract() {
  }

}