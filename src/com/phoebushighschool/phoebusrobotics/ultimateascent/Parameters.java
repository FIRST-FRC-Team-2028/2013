package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import java.util.Vector;

public class Parameters {

  public static final String cameraIP = "10.20.28.11";

  public static final int leftTreadCanID = 6;

  public static final int rightTreadCanID = 7;
  
  // cRIO 9472
  public static final int crioRelayModule = 1;
  
  public static final int leftGearShifterSolenoidChannel = 2;
  
  public static final int rightGearShifterSolenoidChannel = 3;
  
  public static final int FullyExtendedLimitGPIOChannel = 1;

  public static final int FullyRetractedLimitGPIOChannel = 2;

  public static final int MaxMotorOutputVoltage = 12;

  public static final int ArmMovementSomething = -1;            // FIX ME!!!

  public static final int DiscIndexerRelayChannel = 2;

  public static final int GyrometerAnalogChannel = 1;

  public static final int AnalogModule = 1;

  public static final int PushDiscIntoWheelsSomething = -1;     // FIX ME!!!

  public static final int WheelOneCANJaguarCANID = 8;

  public static final int WheelTwoCANJaguarCANID = 9;

}