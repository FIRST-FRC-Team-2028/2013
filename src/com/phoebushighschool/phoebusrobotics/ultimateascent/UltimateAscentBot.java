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
    private DashBoard driverO;
    DriverStation ds;
    boolean turning = false;
    protected Joystick driveStick;
    protected Joystick shooterStick;

    public UltimateAscentBot()
    {
        try
        {
            drive = new TankDrive();
            //gameMech = new GameMech();
        } catch (CANTimeoutException ex)
        {
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
        driverO = new DashBoard(this);
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
    }

    public void autonomous()
    {
        try
        {
//            double _P = (ds.getAnalogIn(1) / 3.3) * 100.0;
//            double _I = (ds.getAnalogIn(2) / 3.3) * 0.01;
//            double _D = (ds.getAnalogIn(3) / 3.3) * 0.01;
//            System.out.println("P: " + _P + ", I: " + _I + ", D: " + _D);
//            aimController.setPID(_P, _I, _D);
//            if (turnController != null)
//            {
//                turnController.setPID(_P, _I, _D);
//            }
            RobotState state = new RobotState();
            while (isAutonomous() && isEnabled())
            {
                driverO.updateDashboard();
                double time = Timer.getFPGATimestamp();
                switch (state.getState())
                {
                    case RobotState.drive:
                        drive.drive(Parameters.AUTONOMOUS_DRIVE_FORWARD_SPEED, 0.0);
                        visionSystem.setShootPosition();
                        if (Timer.getFPGATimestamp() - time < 0.5)
                        {
                            state.nextState();
                        }
                        System.out.println("Driving forward");
                        break;
                    case RobotState.turnTowardsTarget:
                        if (drive.isGyroPresent())
                        {
                            if (setAngle(-20.0))
                            {
                                state.nextState();
                            }
                            System.out.println("Turning towards");
                            break;
                        } else
                        {
                            state.nextState();
                            System.out.println("No Gyro");
                            break;
                        }
                    case RobotState.turnToTarget:
                        if (aim())
                        {
                            state.nextState();
                            DisableAimController();
                        }
                        break;
                    case RobotState.cockShooter:
                        if (gameMech != null)
                        {
                            if (gameMech.cockShooter())
                            {
                                state.nextState();
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
        double _P = (ds.getAnalogIn(1) / 3.3) * 100.0;
        double _I = (ds.getAnalogIn(2) / 3.3) * 0.01;
        double _D = (ds.getAnalogIn(3) / 3.3) * 0.01;
        aimController.setPID(_P, _I, _D);
        if (turnController != null)
        {
            turnController.setPID(_P, _I, _D);
        }
        while (isOperatorControl() && isEnabled())
        {
            driverO.updateDashboard();
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
                drive.drive(drivePercent, turnPercent);
            } catch (CANTimeoutException e)
            {
                System.out.println(e);
            }

            boolean lowGear = driveStick.getRawButton(Parameters.kLowGearButton);
            boolean highGear = driveStick.getRawButton(Parameters.kHighGearButton);
            Tread.Gear newGear = drive.getGear();
            if (highGear)
            {
                newGear = Tread.Gear.kHigh;
            }
            if (lowGear)
            {
                newGear = Tread.Gear.kLow;
            }
            drive.setGear(newGear);

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
                boolean isReloading = false;
                if (!isReloading)
                {
                    boolean indexerPiston = shooterStick.getRawButton(Parameters.kIndexerPistonButton);
                    gameMech.setIndexerPiston(indexerPiston);
                }
                try
                {
                    boolean isShooting = false;
                    if (!isShooting)
                    {
                        boolean turnShooter = shooterStick.getRawButton(Parameters.kTurnShooterButton);
                        gameMech.setShooterMotor(turnShooter);
                    }
                } catch (CANTimeoutException e)
                {
                    System.out.println(e);
                }
                Timer.delay(Parameters.TIMER_DELAY);
                getWatchdog().feed();

            }
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
    public boolean aim()
    {
        if (turning && aimController.onTarget())
        {
            DisableAimController();
            System.out.println("Done turning");
            return true;
        }
        if (!turning)
        {
            EnableAimController();
            aimController.setSetpoint(0.0);
            System.out.println("Turning");
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
    public boolean isAimedAtTarget()
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

    public double getDistanceToTarget()
    {
        return visionSystem.getDistanceToTarget();
    }

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

    public double getDegreesToTarget()
    {
        return visionSystem.getDegreesToTarget();
    }
}
