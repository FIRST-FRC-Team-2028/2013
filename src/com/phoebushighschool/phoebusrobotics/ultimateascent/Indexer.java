package com.phoebushighschool.phoebusrobotics.ultimateascent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/*
 */
public class Indexer {
    protected Solenoid indexer;
    protected DigitalInput discPreIndex;
    public int discCountCurrent;
    public boolean indexerHasRun;
    public boolean indexerDiscPass;
    
  public Indexer()
  {
      indexer = new Solenoid(Parameters.DiscIndexerSolenoidChannel);
      
  }
  /** 
   *  This method will index one disc into the shooter.
   */
      
  public void setIndexerPiston(boolean value){
      indexer.set(value);
  }

  public boolean isDiscLoaded() {  
    return discPreIndex.get();
  }
  
  public int getDiscCountCurrent() {
      return discCountCurrent;
  }
}