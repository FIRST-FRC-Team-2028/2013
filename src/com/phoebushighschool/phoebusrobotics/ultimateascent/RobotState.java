/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phoebushighschool.phoebusrobotics.ultimateascent;

/**
 *
 * @author djennings001
 */
public class RobotState {
    public static final int drive  = 0;
    public static final int turnTowardsTarget = 1;
    public static final int turnToTarget = 2;
    public static final int cockShooter = 3;
    public static final int loadShooter = 4;
    public static final int shootShooter = 5;
    
    int state;
    
    public RobotState() {
        state = drive;
    }
    
    /**
     * nextState()
     * 
     * This method advances the autonomous switch to the next state.
     */
    public void nextState() {
        if (state == shootShooter) {
            state = cockShooter;
        } else {
            state++;
        }
    }
    
    public int getState() {
        return state;
    }
}
