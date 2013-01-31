package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

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
public class UltimateAscentBot extends SimpleRobot {

    protected AimingSystem visionSystem;
    protected TankDrive drive;
    protected GyroSensor gyro;
    public GameMech gameMech;
    public Parameters param;
    public FRCMath math;
    public PIDController aimController;
    public PIDController turnController;
    private DriverStation driverO;
    boolean turning = false;
    int state = 0;

    public UltimateAscentBot() {
        visionSystem = new AimingSystem();
        gyro = new GyroSensor(Parameters.gyroAnalogChannel);
        aimController = new PIDController(Parameters.kRobotProportional,
                Parameters.kRobotIntegral,
                Parameters.kRobotDifferential,
                visionSystem,
                drive);
        turnController = new PIDController(Parameters.kRobotProportional,
                Parameters.kRobotIntegral,
                Parameters.kRobotDifferential,
                gyro,
                drive);
        driverO = new DriverStation(this);
        aimController.setInputRange(Parameters.MAX_CAMERA_INPUT, Parameters.MIN_CAMERA_INPUT);
        aimController.setOutputRange(Parameters.MAX_OUTPUT, Parameters.MIN_OUTPUT);
        aimController.setAbsoluteTolerance(Parameters.PIDController_TOLERANCE);
        turnController.setInputRange(Parameters.MAX_GYRO_INPUT, Parameters.MIN_GYRO_INPUT);
        turnController.setOutputRange(Parameters.MAX_OUTPUT, Parameters.MIN_OUTPUT);
        turnController.setAbsoluteTolerance(Parameters.PIDController_TOLERANCE);
        turnController.setContinuous();
        try {
            drive = new TankDrive();
            gameMech = new GameMech();
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public void autonomous() {
        try {
        while (isAutonomous() && isEnabled()) {
            driverO.updateDashboard();
            double time = Timer.getFPGATimestamp();
            switch (state) {
                case 0:
                    drive.drive(1.0, 0.0);
                    if (Timer.getFPGATimestamp() - time < 0.5) {
                        state++;
                    }
                    break;
                case 1:
                    state++; //TODO: Fix me!!!!
                    break;
                case 2:
                    aim();
                    if(isAimedAtTarget()) {
                        state++;
                    }
                    break;
                
            }
            
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
        } catch (CANTimeoutException e) {
        }
    }

    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            driverO.updateDashboard();
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
     * aim()
     * 
     * This method will align the robot with the target +/- one degree
     */
    public boolean aim() {
        if (turning && isAimedAtTarget()) {
            DisableAimController();
            return true;
        }
        if (!turning) {
            EnableAimController();
            aimController.setSetpoint(0.0);
        }
        return false;
    }
/**
 * setAngle()
 * 
 * This method takes a setpoint and turns the robot until we are at that setpoint.
 * @param setpoint an angle in degrees, from -360.0 - 0.0 - 360.0
 * @return true - we are at the setpoint
 *         false - we are not at the setpoint
 */
    public boolean setAngle(double setpoint) {
        if (turning && turnController.onTarget()) {
            DisableTurnController();
            return true;
        }
        if (!turning) {
            EnableTurnController();
            turnController.setSetpoint(setpoint);
        }
        return false;
    }
    
    /**
     * isAimedAtTarget()
     * 
     * This method will check to see if the target is within +/- one degree of
     * the center.
     */
    public boolean isAimedAtTarget() {
        return visionSystem.isAimedAtTarget();
    }

    public void DisableAimController() {
        if (turning) {
            aimController.disable();
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
        if (turning) {
            turnController.disable();
        }
        turning = false;
    }

    public void EnableTurnController() {
        if (!turning) {
            turnController.enable();
        }
        turning = true;
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
}
