package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.SimpleRobot;
import java.util.Vector;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Compressor;

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
    public FRCMath  myFRCMath;
    public PIDController aimController;
    protected Compressor compressor;
    
  public UltimateAscentBot()
  {
      param = new Parameters();
      compressor = new Compressor(param.CompressorSwitch, param.AnalogModule,
              param.CompressorRelayChannel, param.RelayModule);
      visionSystem = new AimingSystem();
      drive = new TankDrive();
      gameMech = new GameMech();
      myFRCMath = new FRCMath();
  }

  public void operatorControl() 
  {
      while (isEnabled() && isOperatorControl())
      {
        compressor.start();  
      }
  }

  public void autonomous() 
  {
      while (isEnabled() && isAutonomous())
      {
          
      }
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