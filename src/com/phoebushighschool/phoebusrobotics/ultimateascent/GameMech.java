package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.can.*;
/*
 */

public class GameMech
{

    public UltimateAscentBot robot;
    protected Indexer loader;
    public Shooter shooter;

    /**
     * This method will launch the disc.
     */
    public boolean shoot() throws CANTimeoutException
    {

        return shooter.shoot();
    }

    /**
     * This method will allow one disc into the shooter
     */
    public boolean reload()
    {
        loader.indexOneDisc();
        return shooter.isDiscLoaded();

    }

    public boolean cockShooter() throws CANTimeoutException
    {
        return shooter.cock();
    }

    public int getDiscCount()
    {
        return loader.getDiscCountCurrent();
    }

    public boolean isShooterCocked()
    {
        return shooter.isShooterCocked();
    }

    /**
     *
     */
    public void setIndexerPiston(boolean value)
    {
        loader.setIndexerPiston(value);
    }
    
    /**
     * 
     * @param value 
     */
    public void setShooterMotor(boolean value) throws CANTimeoutException 
    {
        shooter.setShooterMotor(value);
    }
}
