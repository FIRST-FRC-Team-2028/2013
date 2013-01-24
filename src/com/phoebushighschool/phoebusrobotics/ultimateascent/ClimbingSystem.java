package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import java.util.Vector;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    /**
   * 
   * @element-type ClimbingWheel
   */
  public Vector  wheel;
    protected Arm arm;

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
  public boolean extendToLatch() {
  return false;
  }

  /** 
   *  This method will retract the arms to within the frame perimeter
   */
  public boolean retractToHandoff() {
  return false;
  }

  public boolean extend() {
  return false;
  }

  public boolean retract() {
  return false;
  }

}