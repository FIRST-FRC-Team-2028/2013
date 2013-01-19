package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDSource;
import java.util.Vector;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.Gyro;

/*
 */
public class AimingSystem implements PIDSource {

    protected UltimateAscentBot robot;
    public AxisCamera camera;
    public Gyro gyro;
    public Vector  myUltrasonic;

  /** 
   *  This method will find the target we are aiming at, and it's center of mass in the x axis.
   */
  public void processImage() {
  }

  /** 
   *  This method returns true if the target is +/- x degree of the camera's center, and false otherwise.
   */
  public boolean isAimedAtTarget() {
  return false;
  }

  public double pidGet() {
  return 0.0;
  }

  public double getDegreesToTarget() {
  return 0.0;
  }

}