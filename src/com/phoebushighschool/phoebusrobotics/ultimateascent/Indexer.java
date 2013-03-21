package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;

/*
 */
public class Indexer
{

//    protected Relay indexer;
    protected Solenoid push;
    protected Solenoid pull;
    protected DigitalInput discPreIndex;
    public int discCountCurrent;
    public boolean indexerHasRun;
    public boolean indexerDiscPass;

    public Indexer()
    {
//        indexer = new Relay(Parameters.PushDiscIntoShooterRelayChannel);
//        indexer.setDirection(Relay.Direction.kForward);
        push = new Solenoid(Parameters.pushDiscOutSolenoidChannel);
        pull = new Solenoid(Parameters.pullDiscInSolenoidChannel);
        push.set(false);
        pull.set(true);
    }

    /**
     * This method will index one disc into the shooter.
     */
    public void setIndexerPiston(boolean value)
    {
//        if (value)
//        {
//            indexer.set(Relay.Value.kOn);
//        } else
//        {
//            indexer.set(Relay.Value.kOff);
//        }
        push.set(value);
        pull.set(!value);
    }

    public boolean isDiscLoaded()
    {
        return discPreIndex.get();
    }

    public int getDiscCountCurrent()
    {
        return discCountCurrent;
    }
}