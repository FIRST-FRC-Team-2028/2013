package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

/*
 */
public class Indexer
{

    protected Relay indexer;
    protected DigitalInput discPreIndex;
    public int discCountCurrent;
    public boolean indexerHasRun;
    public boolean indexerDiscPass;

    public Indexer()
    {
        indexer = new Relay(Parameters.PushDiscIntoShooterRelayChannel);
        indexer.setDirection(Relay.Direction.kForward);
    }

    /**
     * This method will index one disc into the shooter.
     */
    public void setIndexerPiston(boolean value)
    {
        if (value)
        {
            indexer.set(Relay.Value.kOn);
        } else
        {
            indexer.set(Relay.Value.kOff);
        }
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