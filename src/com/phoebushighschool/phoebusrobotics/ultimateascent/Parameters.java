package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import java.util.Vector;

public class Parameters {

  public static final String cameraIP = "10.20.28.11";

  public static final int leftTreadCanID = 6;

  public static final int rightTreadCanID = 7;

  public static final int FullyExtendedLimitGPIOChannel = 1;

  public static final int FullyRetractedLimitGPIOChannel = 2;

  public static final int MaxMotorOutputVoltage = 12;

  public static final int ArmMovementSomething = -1;            // FIX ME!!!

  public static final int DiscIndexerRelayChannel = 2;

  public static final int UltrasonicAnalogChannel = 1;

  public static final int AnalogModule = 1;

  public static final int PushDiscIntoWheelsSomething = -1;     // FIX ME!!!

  public static final int WheelOneCANJaguarCANID = 8;

  public static final int WheelTwoCANJaguarCANID = 9;

  public static final double kRobotProportional = 0.0;
  
  public static final double kRobotDifferential = 0.0;
  
  public static final double kRobotIntegral = 0.0;
  
  public static final double TIMER_DELAY = 0.1;
  
  public static final double MAX_OUTPUT = 1.0;
  
  public static final double MIN_OUTPUT = -1.0;
  
}