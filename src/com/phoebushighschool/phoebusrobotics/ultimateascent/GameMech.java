package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.can.*;
/*
 */

public class GameMech {

    private UltimateAscentBot robot;
    protected Indexer loader;
    public Shooter shooter;
    private GameMechState currentState;
    private GameMechState desiredState;

    /**
     * 
     * @param bot
     * @throws CANTimeoutException 
     */
    public GameMech(UltimateAscentBot bot) throws CANTimeoutException
    {
        robot = bot;
        loader = new Indexer();
        shooter = new Shooter();
        currentState =  GameMechState.kManualControl;
        desiredState = GameMechState.kManualControl;
    }
    
    /**
     * This method will set the desired state to "Unloaded"
     */
    public boolean shoot() throws CANTimeoutException {
        desiredState = GameMech.GameMechState.kUnloaded;
        if (currentState == GameMech.GameMechState.kUnloaded)
            return true;
        return false;
    }

    /**
     * This method will set the desired state to "Ready"
     */
    public boolean reload() {
        desiredState = GameMech.GameMechState.kArmed;
        if (currentState == GameMech.GameMechState.kArmed)
            return true;
        return false;
    }

    /**
     * 
     * @return
     * @throws CANTimeoutException 
     */
    public boolean cockShooter() throws CANTimeoutException {
        //return shooter.cockShooter();
    }

    /**
     * 
     * @return 
     */
    public int getDiscCount() {
        return loader.getDiscCountCurrent();
    }

    /**
     * 
     * @return 
     */
    public boolean isShooterCocked() {
        if (currentState == GameMech.GameMechState.kArmed)
            return true;
        return false;
    }

    /**
     *
     */
    public void setIndexerPiston(boolean value) {
        loader.setIndexerPiston(value);
        currentState = GameMechState.kManualControl;
        desiredState = currentState;
    }

    /**
     * 
     * @return 
     */
    public boolean isShooterLoaded() {
        return shooter.isDiscLoaded();
    }

    /**
     * moveShooterManual()
     * 
     * This method moves the shooter cam at a constant speed.
     * 
     * @throws CANTimeoutException 
     */
    public void moveShooterManual(boolean move) throws CANTimeoutException {
        currentState = GameMechState.kManualControl;
        desiredState = currentState;
        shooter.moveShooterManual(move);
    }
    
    /**
     * getCurrentState()
     * 
     * Returns the current currentState of the Game Mechanism.
     * 
     * @return GameMechState
     */
    public GameMechState getCurrentState()
    {
        return currentState;
    }
    
    /**
     * getDesiredState()
     * 
     * Returns the state that the Game Mechanism is striving to be in.
     * 
     * @return GameMechState - Desired state
     */
    public GameMechState getDesiredState()
    {
        return desiredState;
    }

    /**
     * processGameMech()
     * 
     * This method is going to compare the current GameMech state to the 
     * desired GameMech state and perform the appropriate actions to achieve
     * the desired state.  It will be called every time in the SimpleRobot's
     * control while loop (in either autonomous or operator control).
     * 
     * This method should NEVER be called when the shooter is in Manual Control
     * mode!
     * 
     * @return - the GameMech's current state
     */
    public GameMechState processGameMech() throws CANTimeoutException
    {
        if(desiredState == GameMechState.kManualControl)
        {
            currentState = GameMechState.kManualControl;
            shooter.setShooterMotor(false);
            loader.setIndexerPiston(false);
            return currentState;
        }
        
        if(currentState == desiredState)
        {
            return currentState;
        }
        
        // We are not at our desired state yet
        if(currentState == GameMechState.kRecocking)
        {
            if(shooter.cockShooter())
            {
                currentState = GameMechState.kReloading;
                return currentState;
            }
        }
        if(currentState == GameMechState.kReloading)
        {
            if(shooter.isDiscLoaded())
            {
                loader.setIndexerPiston(false);
                currentState = GameMechState.kArmed;
                return currentState;
            }
            else 
            {
                loader.setIndexerPiston(true);
            }
        }
        if(currentState == GameMechState.kArmed)
        {
            if (shooter.shoot())
            {
                currentState = GameMechState.kUnloaded;
                return currentState;
            }
        }
        if (currentState == GameMechState.kUnloaded)
        {
            currentState = GameMechState.kRecocking;
        }
        return currentState;
    }
    
    /**
     *
     */
    public static class GameMechState {

        private static final int kManual = 0;
        private static final int kCocking = 1;
        private static final int kLoading = 2;
        private static final int kReady = 3;
        private static final int kEmpty = 4;
        public static final GameMechState kManualControl = new GameMechState(kManual);
        public static final GameMechState kRecocking = new GameMechState(kCocking);
        public static final GameMechState kReloading = new GameMechState(kLoading);
        public static final GameMechState kArmed = new GameMechState(kReady);
        public static final GameMechState kUnloaded = new GameMechState(kEmpty);
        private final int value;

        private GameMechState(int state) {
            this.value = state;
        }
    }
}