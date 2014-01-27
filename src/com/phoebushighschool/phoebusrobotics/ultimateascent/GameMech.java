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
    public int switcher = 0;
    public boolean switched = true;
    public boolean reloading = false;
    public boolean shooting = false;

    /**
     *
     * @param bot
     * @throws CANTimeoutException
     */
    public GameMech(UltimateAscentBot bot) throws CANTimeoutException {
        robot = bot;
        loader = new Indexer();
        shooter = new Shooter();
        currentState = GameMechState.kManualControl;
        desiredState = GameMechState.kManualControl;
    }

    /**
     * This method will set the desired state to "Unloaded"
     */
    public boolean shoot() throws CANTimeoutException {
        if (!reloading) {
            desiredState = GameMech.GameMechState.kUnloaded;
            if (currentState == GameMech.GameMechState.kUnloaded) {
                return true;
            }
        }
        return false;
    }

    public void manualShoot() throws CANTimeoutException {
        shooter.shoot();
    }

    public void manualCock() throws CANTimeoutException {
        shooter.cockShooter();
    }

    public void setManualState() {
        desiredState = GameMechState.kManualControl;
        currentState = desiredState;
    }

    /**
     * This method will set the desired state to "Ready"
     */
    public boolean reload() {
        if (!shooting) {
            desiredState = GameMech.GameMechState.kArmed;
            if (currentState == GameMech.GameMechState.kArmed) {
                return true;
            }
        }
        return false;
    }

    /**
     * cockShooter
     *
     * @return
     * @throws CANTimeoutException
     */
    public boolean cockShooter() throws CANTimeoutException {
        return shooter.cockShooter();
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
        if (currentState == GameMech.GameMechState.kArmed) {
            return true;
        }
        return false;
    }

    public boolean isShooterRetracted() {
        return shooter.isShooterRetracted();
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
    public void moveShooterManual(boolean move, boolean forward) throws CANTimeoutException {
        currentState = GameMechState.kManualControl;
        desiredState = currentState;
        shooter.moveShooterManual(move, forward);
    }

    /**
     * getCurrentState()
     *
     * Returns the current currentState of the Game Mechanism.
     *
     * @return GameMechState
     */
    public GameMechState getCurrentState() {
        return currentState;
    }

    /**
     * getDesiredState()
     *
     * Returns the state that the Game Mechanism is striving to be in.
     *
     * @return GameMechState - Desired state
     */
    public GameMechState getDesiredState() {
        return desiredState;
    }

    /**
     * processGameMech()
     *
     * This method is going to compare the current GameMech state to the desired
     * GameMech state and perform the appropriate actions to achieve the desired
     * state. It will be called every time in the SimpleRobot's control while
     * loop (in either autonomous or operator control).
     *
     * This method should NEVER be called when the shooter is in Manual Control
     * mode!
     *
     * @return - the GameMech's current state
     */
    public GameMechState processGameMech() throws CANTimeoutException {
        if (currentState == GameMechState.kManualControl) // $$$ ToDo: Check to see if this is the right condition.
        {
            if (shooter.isShooterCocked() && shooter.isDiscLoaded()) {
                currentState = GameMechState.kArmed;
                return currentState;
            } else if (shooter.isShooterCocked()) {
                currentState = GameMechState.kReloading;
                return currentState;
            } else if (!shooter.isShooterCocked()) {
                currentState = GameMechState.kRecocking;
                return currentState;
            }
        }
        if (desiredState == GameMechState.kManualControl) {
            currentState = GameMechState.kManualControl;
            shooter.setShooterMotor(false);
            loader.setIndexerPiston(false);
            return currentState;
        }

        if (currentState == desiredState) {
            return currentState;
        }

        // We are not at our desired state yet
        if (currentState == GameMechState.kRecocking) {
            System.out.println("Cocking");
            if (shooter.cockShooter()) {
                System.out.println("Cocked");
                currentState = GameMechState.kReloading;
                return currentState;
            }
        }
        if (currentState == GameMechState.kReloading) {
            System.out.println("Reloading");
            if (shooter.isDiscLoaded()) {
                reloading = false;
                shooting = true;
                System.out.println("Reloaded");
                loader.setIndexerPiston(false);
                currentState = GameMechState.kArmed;
                return currentState;
            } else {
                loader.setIndexerPiston(switched);
                if (switcher == 9) {
                    switcher = 0;
                    switched = !switched;
                }
            }
            switcher++;
        }
        if (currentState == GameMechState.kArmed) {
            System.out.println("Shooting");
            if (shooter.shoot()) {
                shooting = false;
                reloading = true;
                System.out.println("Shot");
                currentState = GameMechState.kUnloaded;
                return currentState;
            }
        }
        if (currentState == GameMechState.kUnloaded) {
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