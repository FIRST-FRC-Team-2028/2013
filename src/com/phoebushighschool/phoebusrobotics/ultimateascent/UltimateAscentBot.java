package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.Compressor;

/*
 */
/**
 *
 * @author jmiller015
 */
public class UltimateAscentBot extends SimpleRobot implements PIDSource, PIDOutput {

    protected AimingSystem visionSystem = null;
    protected TankDrive drive = null;
    public GameMech gameMech = null;
    public Compressor compressor;
    public Parameters param;
    public FRCMath math;
    public PIDController aimController = null;
    public PIDController driveController = null;
    public PIDController turnController = null;
    private SmartDashBoard dash;
    public ClimbingSystem climber = null;
    DriverStation ds;
    boolean turning = false;
    boolean driving = false;
    protected Joystick driveStick;
    protected Joystick leftStick;
    protected Joystick rightStick;
    protected String currentRobotActivity = "nothing";
    protected String currentGear;
    int switcher = 0;
    double trueSetpoint = 1440.0;
    public final static boolean test = true;

    public UltimateAscentBot() {
        try {
            if (Parameters.kDriveExists) {
                drive = new TankDrive();
            } else {
                System.out.println("No drive");
            }
            if (Parameters.kClimberExists) {
                climber = new ClimbingSystem();
            } else {
                System.out.println("No climber");
            }
            if (Parameters.kGameMechExists) {
                gameMech = new GameMech(this);
            } else {
                System.out.println("No gamemech");
            }
            if (Parameters.kCameraExists) {
                visionSystem = new AimingSystem();
            } else {
                System.out.println("No camera");
            }
        } catch (CANTimeoutException ex) {
            System.out.println(ex);
        }
        if (visionSystem != null && drive != null) {
            aimController = new PIDController(Parameters.kRobotProportional,
                    Parameters.kRobotIntegral,
                    Parameters.kRobotDifferential,
                    visionSystem,
                    drive);
            aimController.setOutputRange(Parameters.MIN_OUTPUT, Parameters.MAX_OUTPUT);
        }
        if (drive != null) {
            if (drive.isGyroPresent()) {
                turnController = new PIDController(Parameters.kRobotProportional,
                        Parameters.kRobotIntegral,
                        Parameters.kRobotDifferential,
                        drive,
                        drive);
                turnController.setOutputRange(Parameters.MIN_OUTPUT, Parameters.MAX_OUTPUT);
            }
        }
        if (drive != null && visionSystem != null) {
            driveController = new PIDController(Parameters.kRobotProportional,
                    Parameters.kRobotIntegral,
                    Parameters.kRobotDifferential,
                    this,
                    this);
        }
//        dash = new SmartDashBoard(this);
        ds = DriverStation.getInstance();
        driveStick = new Joystick(1);
        leftStick = new Joystick(2);
        rightStick = new Joystick(3);
        compressor = new Compressor(Parameters.CompressorPressureSwitchGPIOChannel, Parameters.CompressorRelayChannel);
    }

    public void autonomous() {
        try {
            RobotState state = new RobotState(true);
            int i = 0;
            if (visionSystem != null) {
                visionSystem.DisableAimingSystem();
            }
            while (isAutonomous() && isEnabled()) {
                Parameters.GO_FOR_MIDDLE_TARGET = ds.getDigitalIn(Parameters.kShootMiddleSwitch);
                compressor.start();
                if (dash != null) {
                    dash.updateDashboard();
                }
                switch (state.getState()) {
                    case RobotState.drive:
                        if (driveController != null) {
                            try {
                                if (setDistance(Parameters.GO_FOR_MIDDLE_TARGET)) {
                                    state.nextState();
                                }
                            } catch (NoTargetFoundException e) {
                            }
                            currentRobotActivity = "Driving";
                        } else {
                            state.nextState();
                        }
                        break;
                    case RobotState.turnTowardsTarget:
                        if (turnController != null) {
                            if (setAngle(Parameters.TURN_TO_TARGET_ANGLE)) {
                                state.nextState();
                            }
                            currentRobotActivity = "Turning to face target";
                        } else {
                            state.nextState();
                        }
                        break;
                    case RobotState.turnToTarget:
                        if (aimController != null) {
                            boolean aimed;
                            try {
                                aimed = turnToTarget();
                            } catch (NoTargetFoundException e) {
                                break;
                            }
                            if (aimed) {
                                state.nextState();
                                DisableAimController();
                                visionSystem.DisableAimingSystem();
                            }
                            currentRobotActivity = "Lining up with target";
                        } else {
                            state.nextState();
                        }
                        break;
                    case RobotState.cockShooter:
                        if (gameMech != null) {
                            if (gameMech.cockShooter()) {
                                state.nextState();
                                currentRobotActivity = "preparing to shoot";
                            }
                        } else {
                            state.nextState();
                        }
                        break;
                    case RobotState.loadShooter:
                        if (gameMech != null) {
                            if (gameMech.reload()) {
                                state.nextState();
                                i = 0;
                                currentRobotActivity = "Reloading";
                            }
                            gameMech.processGameMech();
                        } else {
                            state.nextState();
                        }
                        break;
                    case RobotState.shootShooter:
                        if (gameMech != null) {
                            if (i >= 3) {
                                if (gameMech.shoot()) {
                                    state.nextState();
                                    currentRobotActivity = "shooting. Faint Wheeeeeee is heard";
                                }
                                gameMech.processGameMech();
                            }
                            i++;
                        } else {
                            state.nextState();
                        }
                        break;
                }
                Timer.delay(Parameters.TIMER_DELAY);
                getWatchdog().feed();
            }
            if (aimController != null) {
                DisableAimController();
            }
            if (visionSystem != null) {
                visionSystem.DisableAimingSystem();
            }
            compressor.stop();
        } catch (CANTimeoutException e) {
        }
    }

    /**
     *
     */
    public void operatorControl() {
        double kDamp = 20.0;
        int i = 0;
        boolean shootingMemory = true;
        while (isOperatorControl() && isEnabled()) {
            compressor.start();
            if (dash != null) {
                dash.updateDashboard();
            }

            boolean shooting = ds.getDigitalIn(Parameters.kShootingSwitch);
            Parameters.GO_FOR_MIDDLE_TARGET = ds.getDigitalIn(Parameters.kShootMiddleSwitch);
            boolean highGear = ds.getDigitalIn(Parameters.kGearShiftSwitch);
            // Driver Controls
            //
            if (drive != null) {
                try {
                    double drivePercent = driveStick.getY() * -1.0;
                    if (drivePercent < Parameters.kJoystickDeadband
                            && drivePercent > (-1.0 * Parameters.kJoystickDeadband)) {
                        drivePercent = 0.0;
                    }
                    double turnPercent = driveStick.getX();
                    if (turnPercent < Parameters.kJoystickDeadband
                            && turnPercent > (-1.0 * Parameters.kJoystickDeadband)) {
                        turnPercent = 0.0;
                    }
                    drive.drive(drivePercent, turnPercent, kDamp);
                } catch (CANTimeoutException e) {
                    System.out.println(e);
                }

                Tread.Gear presentGear = drive.getGear();
                Tread.Gear newGear = presentGear;
                if (highGear) {
                    newGear = Tread.Gear.kHigh;
                    currentGear = "High Gear";
                }
                if (!highGear) {
                    newGear = Tread.Gear.kLow;
                    currentGear = "Low Gear";
                }
                if (presentGear != newGear) {
                    drive.setGear(newGear);
                }
            }

            if (visionSystem != null) {
                boolean climbPosition = driveStick.getRawButton(Parameters.kCameraClimbingButton);
                boolean shootPosition = driveStick.getRawButton(Parameters.kCameraShootingButton);
                boolean aiming = driveStick.getRawButton(Parameters.kCameraAimButton);
                if (shootingMemory != shooting) {
                    climbPosition = !shooting;
                    shootPosition = shooting;
                }

                if (climbPosition) {
                    visionSystem.setClimbPosition();
                }
                if (shootPosition) {
                    visionSystem.setShootPosition();
                }
                if (aiming) {
                    try {
                        aim();
                    } catch (NoTargetFoundException e) {
                        System.out.println(e);
                    }
                }
            }
            //
            // Shooter Controls
            //
            if (gameMech != null && shooting) {
                try {
                    boolean turnShooterButton = false;
                    boolean turnShooterReverseButton = false;
                    boolean manualShoot = false;
                    boolean manualCock = false;
                    if (test) {
                        turnShooterButton = leftStick.getRawButton(Parameters.kTurnShooterForwardButton);  //5
                        turnShooterReverseButton = leftStick.getRawButton(Parameters.kTurnShooterReverseButton);  //4                    
                        manualShoot = leftStick.getRawButton(Parameters.kManualShootButton);
                        manualCock = leftStick.getRawButton(Parameters.kManualCockButton);
                    }
                    boolean indexerButton = leftStick.getRawButton(Parameters.kIndexerPistonButton);  //3
                    if (turnShooterButton || turnShooterReverseButton || indexerButton || manualShoot || manualCock) {
                        gameMech.setManualState();
                    }

                    GameMech.GameMechState state = gameMech.getDesiredState();

                    //Game mech is controlled by operator
                    if (state == GameMech.GameMechState.kManualControl) {
                        if (turnShooterButton || turnShooterReverseButton) {
                            gameMech.moveShooterManual(turnShooterButton || turnShooterReverseButton, turnShooterButton);
                        } else if (manualShoot) {
                            gameMech.manualShoot();
                        } else if (manualCock) {
                            gameMech.manualCock();
                        } else if (indexerButton) {
                            gameMech.setIndexerPiston(indexerButton);
                        } else {
                            gameMech.moveShooterManual(false, false);
                        }
                        currentRobotActivity = "You have control over"
                                + " the Game Mechanism";
                    }

//                     Game Mech is controlled autonomously
                    if (rightStick.getRawButton(Parameters.kShootButton)) {
                        gameMech.shoot();
                        currentRobotActivity = "Shooting";
                    }
                    if (rightStick.getRawButton(Parameters.kReloadButton)) {
                        gameMech.reload();
                        currentRobotActivity = "Reloading Shooter";
                    }
                    if (state != GameMech.GameMechState.kManualControl) {
                        gameMech.processGameMech();

                    }
                    if (gameMech.isShooterLoaded()) {
                        ds.setDigitalOut(1, true);
                    } else {
                        ds.setDigitalOut(1, false);
                    }
                } catch (CANTimeoutException e) {
                    System.out.println(e);
                }

            }
            if (climber != null && !shooting) {
                double leftArmValue = leftStick.getY();
                double rightArmValue = rightStick.getY();
                boolean moveArmsTogether = leftStick.getRawButton(Parameters.kMoveArmsTogetherButton);
                try {
                    if (leftArmValue < Parameters.kJoystickDeadband
                            && leftArmValue > (-1.0 * Parameters.kJoystickDeadband)) {
                        leftArmValue = 0.0;
                    }
                    if (rightArmValue < Parameters.kJoystickDeadband
                            && rightArmValue > (-1.0 * Parameters.kJoystickDeadband)) {
                        rightArmValue = 0.0;
                    }
                    if (moveArmsTogether) {
                        rightArmValue = leftArmValue;
                    }

                    climber.moveForwardArmByJoystick(leftArmValue);
                    climber.moveBackArmByJoystick(rightArmValue);
                } catch (CANTimeoutException ex) {
                }
            }
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
        compressor.stop();
    }

    public void test() {
        while (isTest() && isEnabled()) {
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    /**
     * turnToTarget()
     *
     * This method will align the robot with the target +/- one degree
     */
    public boolean turnToTarget() throws NoTargetFoundException {
        if (turning && isAimedAtTarget()) {
            DisableAimController();
            System.out.println("Done turning");
            currentRobotActivity = "Finished aiming";
            return true;
        }
        if (!turning && !isAimedAtTarget()) {
            EnableAimController();
            aimController.setSetpoint(0.0);
            System.out.println("Turning");
            currentRobotActivity = "Aiming";
//            System.out.println("Angle to Target: " + getDegreesToTarget());
        }
        if (isAimedAtTarget()) {
            return true;
        }
        return false;
    }

    public boolean setDistance(boolean middle) throws NoTargetFoundException {
        double setpoint = Parameters.IDEAL_SHOOT_DISTANCE;
        if (driving && visionSystem.isCorrectDistance(middle)) {
            DisableDriveController();
            System.out.println("Done moving");
            currentRobotActivity = "Finished moving";
            return true;
        }
        if (!driving && !visionSystem.isCorrectDistance(middle)) {
            if (!middle) {
                setpoint += 36.0;
            }
            EnableDriveController();
            driveController.setSetpoint(setpoint);
            System.out.println("Moving");
            currentRobotActivity = "Moving";
        }
        if (visionSystem.isCorrectDistance(middle)) {
            return true;
        }
        return false;
    }

    /**
     * setAngle()
     *
     * This method takes a setpoint and turns the robot until we are at that
     * setpoint.
     *
     * @param setpoint an angle in degrees, from -360.0 - 0.0 - 360.0
     * @return true - we are at the setpoint false - we are not at the setpoint
     */
    public boolean setAngle(double setpoint) {
        if (drive.isGyroPresent()) {
            if (turning && drive.atTargetAngle(trueSetpoint)) {
                DisableTurnController();
                return true;
            }
            if (!turning && !drive.atTargetAngle(trueSetpoint)) {
                setpoint += drive.readAngle();
                trueSetpoint = setpoint;
                EnableTurnController();
                turnController.setSetpoint(trueSetpoint);
            }
            if (drive.atTargetAngle(trueSetpoint)) {
                return true;
            }
        }
        return false;
    }

    public boolean aim() throws NoTargetFoundException {
        switch (switcher) {
            case 0:
                if (turnToTarget()) {
                    switcher = 2;
                    break;
                }
                break;
            case 1:
                if (setDistance(Parameters.GO_FOR_MIDDLE_TARGET)) {
                    switcher++;
                    break;
                }
                break;
            case 2:
                if (setAngle(Parameters.TURN_TO_TARGET_ANGLE)) {
                    switcher = 0;
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * isAimedAtTarget()
     *
     * This method will check to see if the target is within +/- one degree of
     * the center.
     */
    public boolean isAimedAtTarget() throws NoTargetFoundException {
        if (visionSystem != null) {
            return visionSystem.isAimedAtTarget();
        } else {
            return false;
        }
    }

    public void DisableAimController() {
        if (turning) {
            aimController.disable();
            if (visionSystem != null) {
                visionSystem.DisableAimingSystem();
            }
        }
        turning = false;
    }

    public void EnableAimController() {
        if (!turning) {
            aimController.enable();
        }
        turning = true;
    }

    public void DisableTurnController() {
        if (drive.isGyroPresent()) {
            if (turning) {
                turnController.disable();
            }
            turning = false;
        }
    }

    public void EnableTurnController() {
        if (drive.isGyroPresent()) {
            if (!turning) {
                turnController.enable();
            }
            turning = true;
        }
    }

    public void DisableDriveController() {
        if (driveController != null) {
            if (driving) {
                driveController.disable();
            }
            driving = false;
        }
    }

    public void EnableDriveController() {
        if (driveController != null) {
            if (!driving) {
                driveController.enable();
            }
            driving = true;
        }
    }

    /**
     * getDistanceToTarget()
     *
     * This method will return the distance to the target according to the
     * AimingSystem for display on the dashboard.
     *
     * @return
     */
    public double getDistanceToTarget() {
        double temp;
        try {
            temp = visionSystem.getDistanceToTarget();
        } catch (NoTargetFoundException e) {
            return 0.0;
        }
        return temp;
    }

    /**
     * getDiscCount()
     *
     * This method will return the remaining number of discs for display on the
     * dashboard
     *
     * @return
     */
    public int getDiscCount() {
        if (gameMech != null) {
            return gameMech.getDiscCount();
        } else {
            return -1;
        }
    }

    /**
     * isShooterCocked()
     *
     * This method will return if the shooter is ready to be loaded and shot for
     * display on the dashboard.
     *
     * @return
     */
    public boolean isShooterCocked() {
        if (gameMech != null) {
            return gameMech.isShooterCocked();
        } else {
            return false;
        }
    }

    /**
     * getDegreesToTarget()
     *
     * This method will return the offset in degrees to the target for display
     * on the dashboard.
     *
     * @return
     */
    public double getDegreesToTarget() {
        double temp = 0.0;
        if (visionSystem != null) {
            try {
                temp = visionSystem.getDegreesToTarget();
            } catch (NoTargetFoundException e) {
            }
        }
        return temp;
    }

    /**
     * getArmState()
     *
     * This method will tell us whether the forward arm is extended or retracted
     * for display on the dashboard.
     *
     * @return
     */
    public String getArmState() {
        if (climber != null) {
            return climber.getArmState();
        } else {
            return "Arm is null";
        }
    }

    /**
     * isShooterLoaded()
     *
     * This method will return a boolean to tell whether or not a disc is in the
     * shooter for display on the dashboard.
     *
     * @return
     */
    public boolean isShooterLoaded() {
        return gameMech.isShooterLoaded();
    }

    /**
     * getAutonState()
     *
     * This method returns currentRobotActivity for display on the dashboard.
     *
     * @return String to indicate where we are in the autonomous loop/
     */
    public String getCurrentRobotActivity() {
        return currentRobotActivity;
    }

    public double pidGet() {
        return getDistanceToTarget();
    }

    public void pidWrite(double output) {
        try {
            drive.drive(output, 0.0, 0.0);
        } catch (CANTimeoutException e) {
        }
    }
}
