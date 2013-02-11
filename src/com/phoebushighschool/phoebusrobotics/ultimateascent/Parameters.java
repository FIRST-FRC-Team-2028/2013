package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

public class Parameters {

  public static final String cameraIP = "10.20.28.11";

  // CAN ID info
  public static final int leftTreadCanID = 6;
  public static final int rightTreadCanID = 7;
  public static final int WheelOneCANJaguarCANID = 8;
  public static final int forwardArmMovementCANID = 9;
  public static final int backArmMovementCANID = 10;
  
  //Digital Sidecar GPIO Channels
  public static final int DiscIsLoadedInputGPIOChannel = 1;
  public static final int latchLimitSwitchGPIOChannel = 3;
  public static final int handOffLimitSwitchGPIOChannel = 4;
  public static final int CompressorPressureSwitchGPIOChannel = 2;
  public static final int DiscInShooterGPIOChannel = 5;
  public static final int ShooterIsCockedGPIOChannel = 6;
  public static final int ShooterIsRetractedGPIOChannel = 7; 

  public static final double MaxMotorOutputVoltage = 12.0;

  // Digital Sidecar 12V Relay channels
  public static final int RelayModule = 1;  
  public static final int CompressorRelayChannel = 3;
  
  //Digital Sidecar PWM channels
  public static final int cameraServoPWMChannel = 1;
  
  // cRIO 9201 Analog Module Channels
  public static final int AnalogModule = 1;  
  public static final int UltrasonicAnalogChannel = 1;
    public static final int gyroAnalogChannel = 2;

  // cRIO 9472 24V Solenoid Module
  public static final int crioRelayModule = 1;
  public static final int PushDiscIntoShooterSolenoid = 1;
  public static final int leftGearLowSolenoidChannel = 2;
  public static final int rightGearLowSolenoidChannel = 3;
  public static final int DiscIndexerSolenoidChannel = 4;
  public static final int leftGearHighSolenoidChannel = 5;
  public static final int rightGearHighSolenoidChannel = 6;
  

  // Aiming PID Constants
  public static final double kRobotProportional = 0.0;
  public static final double kRobotDifferential = 0.0;
  public static final double kRobotIntegral = 0.0;
  
  public static final double TIMER_DELAY = 0.1;
    
  public static final double MAX_CAMERA_INPUT = 25.0;
  
  public static final double MIN_CAMERA_INPUT = -25.0;
  
  public static final double MAX_GYRO_INPUT = 360.0;
  
  public static final double MIN_GYRO_INPUT = -360.0;
  
  public static final double MAX_OUTPUT = 1.0;
  
  public static final double MIN_OUTPUT = -1.0;
  
  public static final double PIDController_TOLERANCE = 1.0;
  
  public static final int discCountInit = 3;
  
  public static boolean GO_FOR_MIDDLE_TARGET = true;
  
  public static double kJoystickDeadband = 0.05; 
  
  //driver controller buttons
  public static int kLowGearButton = 5;
  public static int kHighGearButton = 3;
  public static int kCameraClimbingButton = 6;
  public static int kCameraShootingButton = 4;
  
  //shooter controller buttons left arm
  public static int kShootButton = 1;
  public static int kCameraAimButton = 5;
  public static int kReloadButton = 6;
  //shooter controller buttons left arm
  public static int kIndexerPistonButton = 5;
  public static int kTurnShooterButton = 6; 
  
  public static double kCameraShooterPosition = 85.0; 
  public static double kCameraClimbPosition = 160.0;

  public static double kShooterMotorSpeed = 1.0; 
}
