package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

public class Parameters {

  public static final String cameraIP = "10.20.28.11";

  // CAN ID info
  public static final int leftTreadCanID = 6;
  public static final int rightTreadCanID = 7;
  public static final int WheelOneCANJaguarCANID = 8;
  public static final int WheelTwoCANJaguarCANID = 9;
  public static final int ArmMovementCANID = 10;
  
  //Digital Sidecar GPIO Channels
  public static final int latchLimitSwitchGPIOChannel = 3;
  public static final int handOffLimitSwitchGPIOChannel = 4;
  public static final int CompressorPressureSwitchGPIOChannel = 2;  

  public static final int MaxMotorOutputVoltage = 12;

  // Digital Sidecar 12V Relay channels
  public static final int RelayModule = 1;  
  public static final int DiscIndexerRelayChannel = 2;
  public static final int CompressorRelayChannel = 3;
  
  // cRIO 9201 Analog Module Channels
  public static final int AnalogModule = 1;  
  public static final int UltrasonicAnalogChannel = 1;

  // cRIO 9472 24V Solenoid Module
  public static final int crioRelayModule = 1;
  public static final int PushDiscIntoShooterSolenoid = 1;
  public static final int leftGearShifterSolenoidChannel = 2;
  public static final int rightGearShifterSolenoidChannel = 3;

  // Aiming PID Constants
  public static final double kRobotProportional = 0.0;
  public static final double kRobotDifferential = 0.0;
  public static final double kRobotIntegral = 0.0;
  
  public static final double TIMER_DELAY = 0.1;
  
  public static final double MAX_OUTPUT = 1.0;
  
  public static final double MIN_OUTPUT = -1.0;
  
}
