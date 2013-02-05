package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DriverStation;

/*
 */
/**
 *
 * @author jmiller015
 */
public class UltimateAscentBot extends SimpleRobot {

    protected AimingSystem visionSystem;
    protected TankDrive drive;
    public GameMech gameMech = null;
    public Parameters param;
    public FRCMath math;
    public PIDController aimController;
    public PIDController turnController;
    private SmartDashBoard dash;
    DriverStation ds;
    boolean turning = false;
    protected Joystick driveStick;
    protected Joystick shooterStick;
    public String autonState;

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
    }

public void autonomous()
    {
        try
        {
            double _P = (ds.getAnalogIn(1) / 3.3) * 500.0;
            double _I = (ds.getAnalogIn(2) / 3.3);
            double _D = (ds.getAnalogIn(3) / 3.3);
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
                        drive.drive(Parameters.AUTONOMOUS_DRIVE_FORWARD_SPEED, 0.0);
                        if (Timer.getFPGATimestamp() - time < 0.5)
                        {
                            state.nextState();
                        }
                        System.out.println("Driving forward");
                        autonState = "Driving";
                        break;
                    case RobotState.turnTowardsTarget:
                        if (drive.isGyroPresent())
                        {
                            if (setAngle(-20.0))
                            {
                                state.nextState();
                            }
                            System.out.println("Turning towards");
                            autonState = "Turning to face target";
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
                        }
                        System.out.println("Turning to. Angle: " + visionSystem.getDegreesToTarget());
                        autonState = "Lining up with target";
                        break;
                    case RobotState.cockShooter:
                        if (gameMech != null)
                        {
                            if (gameMech.cockShooter())
                            {
                                state.nextState();
                                autonState = "preparing to shoot";
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
                              autonState = "shooting. Faint Wheeeeeee is heard";
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


    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            dash.updateDashboard();
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    public void test() {
        while (isTest() && isEnabled()) {
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    /**
     * This method will align the robot with the target +/- one degree
     */
    public boolean aim() {
        if (turning && isAimedAtTarget()) {
            DisableTurnController();
            return true;
        }
        if (!turning) {
            double setPoint = visionSystem.getDegreesToTarget();
            EnableTurnController();
            aimController.setSetpoint(setPoint);
        }
        return false;
    }

    /**
     * This method will check to see if the target is within +/- one degree of
     * the center.
     */
    public boolean isAimedAtTarget() {
        return visionSystem.isAimedAtTarget();
    }
    public double getDistanceToTarget() {
        return visionSystem.getDistanceToTarget();
    }

    public int getDiscCount() {
        return gameMech.getDiscCount();
    }

    public boolean isShooterCocked() {
        return gameMech.isShooterCocked();
    }

    public double getDegreesToTarget()  {
        return visionSystem.getDegreesToTarget();
    }
    public String getArmState()
    {
        return drive.getArmState();
    }
    public boolean isShooterLoaded()
    {
        return gameMech.isShooterLoaded();
    } public void DisableAimController()
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
}
