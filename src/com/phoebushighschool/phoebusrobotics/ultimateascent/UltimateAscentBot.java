package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

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
    public GameMech gameMech = null;
    public Parameters param;
    public FRCMath math;
    public PIDController aimController;
    public PIDController turnController;
    private SmartDashBoard dash;
    public ClimbingSystem climber;
    DriverStation ds;
    boolean turning = false;
    protected Joystick driveStick;
    protected Joystick shooterStick;
    protected Joystick armStick;
    protected String currentRobotActivity;
    protected String currentGear;

    public UltimateAscentBot()
    {
        try
        {
            drive = new TankDrive();
            climber = new ClimbingSystem();
            //gameMech = new GameMech();
        } catch (CANTimeoutException ex)
        {
            System.out.println(ex);
        }
        visionSystem = new AimingSystem();
        aimController = new PIDController(Parameters.kRobotProportional,
                Parameters.kRobotIntegral,
                Parameters.kRobotDifferential,
                visionSystem,
                drive);
        if (drive.isGyroPresent())
        {
            turnController = new PIDController(Parameters.kRobotProportional,
                    Parameters.kRobotIntegral,
                    Parameters.kRobotDifferential,
                    drive,
                    drive);
        }
        dash = new SmartDashBoard(this);
        ds = DriverStation.getInstance();
        aimController.setInputRange(Parameters.MIN_CAMERA_INPUT, Parameters.MAX_CAMERA_INPUT);
        aimController.setOutputRange(Parameters.MIN_OUTPUT, Parameters.MAX_OUTPUT);
        aimController.setAbsoluteTolerance(Parameters.CAMERA_TOLERANCE);
        if (turnController != null)
        {
            turnController.setInputRange(Parameters.MIN_GYRO_INPUT, Parameters.MAX_GYRO_INPUT);
            turnController.setOutputRange(Parameters.MIN_OUTPUT, Parameters.MAX_OUTPUT);
            turnController.setAbsoluteTolerance(Parameters.GYRO_TOLERANCE);
            turnController.setContinuous();
        }
        driveStick = new Joystick(1);
        shooterStick = new Joystick(2);
        armStick = new Joystick(3);
    }

    public void autonomous()
    {
        try
        {
            double _P = (ds.getAnalogIn(1) / 3.3) * 500.0;
            double _I = (ds.getAnalogIn(2) / 3.3);
            double _D = (ds.getAnalogIn(3) / 3.3);
            double kDamp = (ds.getAnalogIn(1) / 3.3) * 50;
            System.out.println("P: " + _P + ", I: " + _I + ", D: " + _D);
            aimController.setPID(_P, _I, _D);
            if (turnController != null)
            {
                turnController.setPID(_P, _I, _D);
            }
            RobotState state = new RobotState();
            while (isAutonomous() && isEnabled())
            {
                dash.updateDashboard();
                double time = Timer.getFPGATimestamp();
                switch (state.getState())
                {
                    case RobotState.drive:
                        drive.drive(Parameters.AUTONOMOUS_DRIVE_FORWARD_SPEED, 0.0, kDamp);
                        if (Timer.getFPGATimestamp() - time < 0.5)
                        {
                            state.nextState();
                        }
                        System.out.println("Driving forward");
                        currentRobotActivity = "Driving";
                        break;
                    case RobotState.turnTowardsTarget:
                        if (drive.isGyroPresent())
                        {
                            if (setAngle(-20.0))
                            {
                                state.nextState();
                            }
                            System.out.println("Turning towards");
                            currentRobotActivity = "Turning to face target";
                            break;
                        } else
                        {
                            state.nextState();
                            System.out.println("No Gyro");
                            break;
                        }
                    case RobotState.turnToTarget:
                        boolean aimed;
                        try
                        {
                            aimed = aim();
                        } catch (NoTargetFoundException e)
                        {
                            break;
                        }
                        if (aimed)
                        {
                            state.nextState();
                        }
//                        System.out.println("Turning to. Angle: " + visionSystem.getDegreesToTarget());
                        currentRobotActivity = "Lining up with target";
                        break;
                    case RobotState.cockShooter:
                        if (gameMech != null)
                        {
                            if (gameMech.cockShooter())
                            {
                                state.nextState();
                                currentRobotActivity = "preparing to shoot";
                                break;
                            }
                        } else
                        {
                            state.nextState();
                        }
                        break;
                    case RobotState.loadShooter:
                        if (gameMech != null)
                        {
                            if (gameMech.reload())
                            {
                                state.nextState();
                            }
                        } else
                        {
                            state.nextState();
                        }
                        break;
                    case RobotState.shootShooter:
                        if (gameMech != null)
                        {
                            if (gameMech.shoot())
                            {
                                state.nextState();
                                currentRobotActivity = "shooting. Faint Wheeeeeee is heard";
                            }
                        } else
                        {
                            state.nextState();
                        }
                        break;
                }
                Timer.delay(Parameters.TIMER_DELAY);
                getWatchdog().feed();
            }
            DisableAimController();
        } catch (CANTimeoutException e)
        {
        }
    }

    /**
     *
     */
    public void operatorControl()
    {
        double kDamp = (ds.getAnalogIn(1) / 3.3) * 50;
//        double _P = (ds.getAnalogIn(1) / 3.3) * 100.0;
//        double _I = (ds.getAnalogIn(2) / 3.3) * 0.01;
//        double _D = (ds.getAnalogIn(3) / 3.3) * 0.01;
//        aimController.setPID(_P, _I, _D);
//        if (turnController != null)
//        {
//            turnController.setPID(_P, _I, _D);
//        }
        int i = 0;
        while (isOperatorControl() && isEnabled())
        {
//            dash.updateDashboard();
            //
            // Driver Controls
            //
            try
            {
                double drivePercent = driveStick.getY() * -1.0;
                if (drivePercent < Parameters.kJoystickDeadband
                        && drivePercent > (-1.0 * Parameters.kJoystickDeadband))
                {
                    drivePercent = 0.0;
                }
                double turnPercent = driveStick.getX();
                if (turnPercent < Parameters.kJoystickDeadband
                        && turnPercent > (-1.0 * Parameters.kJoystickDeadband))
                {
                    turnPercent = 0.0;
                }
                switch (i)
                {
                    default:
                        i++;
                        break;
                    case 3:
                        System.out.println("Drive value: " + drivePercent + ", Turn value: " + turnPercent);
                        i = 0;
                        break;
                }
                drive.drive(drivePercent, turnPercent, kDamp);
            } catch (CANTimeoutException e)
            {
                System.out.println(e);
            }

            boolean lowGear = driveStick.getRawButton(Parameters.kLowGearButton);
            boolean highGear = driveStick.getRawButton(Parameters.kHighGearButton);
            Tread.Gear presentGear = drive.getGear();
            Tread.Gear newGear = presentGear;
            if (highGear)
            {
                newGear = Tread.Gear.kHigh;
                currentGear = "High Gear";
            }
            if (lowGear)
            {
                newGear = Tread.Gear.kLow;
                currentGear = "Low Gear";
            }
            if (presentGear != newGear)
            {
                drive.setGear(newGear);
            }

            boolean climbPosition = driveStick.getRawButton(Parameters.kCameraClimbingButton);
            boolean shootPosition = driveStick.getRawButton(Parameters.kCameraShootingButton);
            double currentPosition = visionSystem.getServoPosition();
            if (climbPosition)
            {
                visionSystem.setClimbPosition();
                currentPosition = Parameters.kCameraClimbPosition;
            }
            if (shootPosition)
            {
                visionSystem.setShootPosition();
                currentPosition = Parameters.kCameraShooterPosition;
            }
            //
            // Shooter Controls
            //
            if (gameMech != null)
            {
                GameMech.GameMechState state = gameMech.getDesiredState();
                try
                {
                    if (state == GameMech.GameMechState.kManualControl)
                    {

                        // Manually exercise indexer          
                        boolean indexerButton = shooterStick.getRawButton(Parameters.kIndexerPistonButton);
                        gameMech.setIndexerPiston(indexerButton);
                        // Manually exercise shooter cam
                        boolean turnShooterButton = shooterStick.getRawButton(Parameters.kTurnShooterForwardButton);
                        gameMech.moveShooterManual(turnShooterButton, true);
                        currentRobotActivity = "You have control over"
                                + " the Game Mechanism";
                        boolean turnShooterReverseButton = shooterStick.getRawButton(Parameters.kTurnShooterReverseButton);
                        gameMech.moveShooterManual(turnShooterReverseButton, false);
                    }
                    // Game Mech is controlled autonomously
                    if (armStick.getRawButton(Parameters.kShootButton))
                    {
                        gameMech.shoot();
                        currentRobotActivity = "Shooting";
                    }
                    if (armStick.getRawButton(Parameters.kReloadButton))
                    {
                        if (gameMech.getDesiredState() != GameMech.GameMechState.kUnloaded)
                        {
                            gameMech.reload();
                        }
                        currentRobotActivity = "Reloading Shooter";
                    }
                    if (state != GameMech.GameMechState.kManualControl)
                    {
                        gameMech.processGameMech();

                    }
                } catch (CANTimeoutException e)
                {
                    System.out.println(e);
                }

            }
            if (climber != null)
            {
                double leftArmValue = shooterStick.getY();
                double rightArmValue = armStick.getY();
                try
                {
                    if (leftArmValue < Parameters.kJoystickDeadband
                            && leftArmValue > (-1.0 * Parameters.kJoystickDeadband))
                    {
                        climber.moveForwardArmByJoystick(0.0);
                    } else
                    {
                        climber.moveForwardArmByJoystick(leftArmValue);
                    }
                    if (rightArmValue < Parameters.kJoystickDeadband
                            && rightArmValue > (-1.0 * Parameters.kJoystickDeadband))
                    {
                        climber.moveBackArmByJoystick(0.0);
                    } else
                    {
                        climber.moveBackArmByJoystick(rightArmValue);
                    }
                } catch (CANTimeoutException ex)
                {
                    ex.printStackTrace();
                }
            }
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled())
        {
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    /**
     * aim()
     *
     * This method will align the robot with the target +/- one degree
     */
    public boolean aim() throws NoTargetFoundException
    {
        if (turning && isAimedAtTarget())
        {
            DisableAimController();
            System.out.println("Done turning");
            return true;
        }
        if (!turning)
        {
            EnableAimController();
            aimController.setSetpoint(0.0);
//            System.out.println("Angle to Target: " + getDegreesToTarget());
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
    public boolean setAngle(double setpoint)
    {
        if (drive.isGyroPresent())
        {
            if (turning && turnController.onTarget())
            {
                DisableTurnController();
                return true;
            }
            if (!turning)
            {
                EnableTurnController();
                turnController.setSetpoint(setpoint);
            }
        }
        return false;
    }

    /**
     * isAimedAtTarget()
     *
     * This method will check to see if the target is within +/- one degree of
     * the center.
     */
    public boolean isAimedAtTarget() throws NoTargetFoundException
    {
        return visionSystem.isAimedAtTarget();
    }

    public void DisableAimController()
    {
        if (turning)
        {
            aimController.disable();
        }
        turning = false;
    }

    public void EnableAimController()
    {
        if (!turning)
        {
            aimController.enable();
        }
        turning = true;
    }

    public void DisableTurnController()
    {
        if (drive.isGyroPresent())
        {
            if (turning)
            {
                turnController.disable();
            }
            turning = false;
        }
    }

    public void EnableTurnController()
    {
        if (drive.isGyroPresent())
        {
            if (!turning)
            {
                turnController.enable();
            }
            turning = true;
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
    public double getDistanceToTarget()
    {
        return visionSystem.getDistanceToTarget();
    }

    /**
     * getDiscCount()
     *
     * This method will return the remaining number of discs for display on the
     * dashboard
     *
     * @return
     */
    public int getDiscCount()
    {
        if (gameMech != null)
        {
            return gameMech.getDiscCount();
        } else
        {
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
    public boolean isShooterCocked()
    {
        if (gameMech != null)
        {
            return gameMech.isShooterCocked();
        } else
        {
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
    public double getDegreesToTarget()
    {
        double temp = 0.0;
        try
        {
            temp = visionSystem.getDegreesToTarget();
        } catch (NoTargetFoundException e)
        {
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
    public String getArmState()
    {
        return climber.getArmState();
    }

    /**
     * isShooterLoaded()
     *
     * This method will return a boolean to tell whether or not a disc is in the
     * shooter for display on the dashboard.
     *
     * @return
     */
    public boolean isShooterLoaded()
    {
        return gameMech.isShooterLoaded();
    }

    /**
     * getAutonState()
     *
     * This method returns currentRobotActivity for display on the dashboard.
     *
     * @return String to indicate where we are in the autonomous loop/
     */
    public String getCurrentRobotActivity()
    {
        return currentRobotActivity;
    }
}
