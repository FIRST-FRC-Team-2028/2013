package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.*;     

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
    public GameMech gameMech;
    public Parameters param;
    public FRCMath math;
    public PIDController aimController;
    boolean turning = false;

    public UltimateAscentBot() {
        visionSystem = new AimingSystem();
        aimController = new PIDController(Parameters.kRobotProportional,
                Parameters.kRobotIntegral, Parameters.kRobotDifferential,
                visionSystem, drive);
        aimController.setOutputRange(Parameters.MAX_OUTPUT, Parameters.MIN_OUTPUT);
        try{
        drive = new TankDrive();
        }catch(CANTimeoutException E){
            
        }
    }

    public void autonomous() {
        while (isAutonomous() && isEnabled()) {
            Timer.delay(Parameters.TIMER_DELAY);
            getWatchdog().feed();
        }
    }

    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
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

    public void DisableTurnController() {
        if (turning) {
            aimController.disable();
        }
        turning = false;
    }

    public void EnableTurnController() {
        if (!turning) {
            aimController.enable();
        }
        turning = true;
    }
}
