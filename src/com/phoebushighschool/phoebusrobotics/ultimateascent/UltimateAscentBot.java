package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.SimpleRobot;
import java.util.Vector;
import edu.wpi.first.wpilibj.PIDController;

/*
 */
/**
 *
 * @author jmiller015
 */
public class UltimateAscentBot extends SimpleRobot 
{

    protected AimingSystem visionSystem;
    protected TankDrive drive;
    public GameMech gameMech;
    public Parameters param;
    public Vector  myFRCMath;
    public PIDController aimControlelr;

  public void operatorControl() {
  }

  public void autonomous() {
  }

  /** 
   *  This method will align the robot with the target +/- one degree
   */
  public void aim() {
  }

  /** 
   *  This method will check to see if the target is within +/- one degree of the center.
   */
  public boolean isAimedAtTarget() 
  {
      return false;
  }

}