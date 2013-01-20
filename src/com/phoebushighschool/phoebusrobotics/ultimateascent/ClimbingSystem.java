package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

/*
 */
public class ClimbingSystem {

    protected TankDrive drive;
    public Arm arm;
    /**
   * 
   * @element-type climbWheel
   */
  public climbWheel  wheel;
  public ClimbingSystem()
  {
      arm = new Arm();
  }

  /** 
   *  This method will extend the arms far enough to reach the first level
   */
  public boolean extendToLatch() 
  {
          if(arm.extend())
          {
            return true;
          }
          return false
                  ;
  }

  /** 
   *  This method will retract the arms to within the frame perimeter
   */
  public void retract() 
  {
      if (extendToLatch())
      {
          arm.retract();
      }
  }

}