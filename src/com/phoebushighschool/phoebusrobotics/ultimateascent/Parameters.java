package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import java.util.Vector;

public class Parameters {

  public static final String cameraIP = "10.20.28.11";

  // CAN ID info
  public static final int leftTreadCanID = 6;

  public static final int rightTreadCanID = 7;
 
  public static final int WheelOneCANJaguarCANID = 8;

  public static final int WheelTwoCANJaguarCANID = 9;
  
  public static final int ArmMovementSomething = 10;

// basic necessities
  public static final int MaxMotorOutputVoltage = 12;

  // Relay channels
  public static final int DiscIndexerRelayChannel = 2;

  public static final int CompressorRelayChannel = 3;
  
  public static final int PushDiscIntoWheelsSolenoid = 1;     // FIX ME!!!
 
  //sensors on analog
  public static final int GyrometerAnalogChannel = 1;
  
  public static final int CompressorSwitch = 2;
  
// cRIO module numbers, all the same
  public static final int AnalogModule = 1;
  
  public static final int RelayModule = 1;

  //GPIO inputs
  public static final int latchLimitSwitchGPIOChannel = 3;
  
  public static final int handOffLimitSwitchGPIOChannel = 4;

}