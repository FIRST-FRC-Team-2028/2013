package com.phoebushighschool.phoebusrobotics.ultimateascent;

public class Parameters
{

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
    public final static double MaxMotorOutputCurrent = 18.0;
    // Digital Sidecar 12V Relay channels
    public static final int RelayModule = 1;
    public static final int CompressorRelayChannel = 3;
    public static final int PushDiscIntoShooterRelayChannel = 1;
    //Digital Sidecar PWM channels
    public static final int cameraServoPWMChannel = 1;
    // cRIO 9201 Analog Module Channels
    public static final int AnalogModule = 1;
    public static final int UltrasonicAnalogChannel = 1;
    public static final int gyroAnalogChannel = 2;
    // cRIO 9472 24V Solenoid Module
    public static final int crioRelayModule = 1;
    public static final int leftGearSolenoidChannel = 2;
    public static final int rightGearSolenoidChannel = 3;
    public final static int pushDiscOutSolenoidChannel = 4;
    public final static int pullDiscInSolenoidChannel = 5;
    // Aiming PID Constants
    public static final double kRobotProportional = 0.038508249651944666;
    public static final double kRobotDifferential = 0.0;
    public static final double kRobotIntegral = 0.001481086525074795;
    public static final double TIMER_DELAY = 0.1;
    public static final double MAX_CAMERA_INPUT = 25.0;
    public static final double MIN_CAMERA_INPUT = -25.0;
    public static final double MAX_GYRO_INPUT = 360.0;
    public static final double MIN_GYRO_INPUT = -360.0;
    public static final double MAX_OUTPUT = 1.0;
    public static final double MIN_OUTPUT = -1.0;
    public static final double MIN_SHOOT_DISTANCE = 168.0;
    public static final double MAX_SHOOT_DISTANCE = 192.0;
    public static final double IDEAL_SHOOT_DISTANCE = 180.0;
    public static final double CAMERA_TOLERANCE = 1.0;
    public static final double GYRO_TOLERANCE = 20.0;
    public static final int discCountInit = 3;
    public static boolean GO_FOR_MIDDLE_TARGET = true;
    public static double kJoystickDeadband = 0.05;
    public static double TURN_TO_TARGET_ANGLE = -20.0;
    public final static boolean kDriveExists = true;
    public final static boolean kClimberExists = true;
    public final static boolean kGameMechExists = true;
    public final static boolean kCameraExists = true;
    //driver controller buttons
    public static int kLowGearButton = 3;
    public static int kHighGearButton = 5;
    public static int kCameraClimbingButton = 4;
    public static int kCameraShootingButton = 6;
    //shooter controller buttons right arm
    public static int kShootButton = 1;
    public static int kCameraAimButton = 5;
    public static int kReloadButton = 6;
    //shooter controller buttons left arm
    public final static int kMoveArmsTogetherButton = 1;
    public static int kIndexerPistonButton = 3;
    public static int kTurnShooterReverseButton = 4;
    public static int kTurnShooterForwardButton = 5;
    public static int kManualCockButton = 8;
    public static int kManualShootButton = 9;
    //Driver Station switches
    public final static int kShootingSwitch = 1;
    public final static int kShootMiddleSwitch = 2;
    public final static int kGearShiftSwitch = 3;
    
    public static double kCameraShooterPosition = 45.0;
    public static double kCameraClimbPosition = 105.0;
    public static final double AUTONOMOUS_DRIVE_FORWARD_SPEED = 0.1;
    public static double kShooterMotorSpeed = 1.0;
}