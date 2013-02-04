package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/*
 */
public class Indexer {

    protected GameMech gameMech;
    protected Solenoid indexer;
    protected DigitalInput discPreIndex;
    public int discCountCurrent;
    public boolean indexerHasRun;
    public boolean indexerDiscPass;
    
  public Indexer()
  {
      gameMech = new GameMech();
      indexer = new Solenoid(Parameters.DiscIndexerSolenoidChannel);
      discPreIndex = new DigitalInput(Parameters.DiscInShooterGPIOChannel);
      discCountCurrent = Parameters.discCountInit;
      indexerHasRun = false;
      indexerDiscPass = false;
  }

  /** 
   *  This method will index one disc into the shooter.
   */
  public void indexOneDisc() {
      if (discPreIndex.get() && discCountCurrent >= 1 && !indexerHasRun && !indexerDiscPass){
          indexer.set(true);           
      }
      else if (!discPreIndex.get() && !indexerHasRun){                
          indexerDiscPass = true;
      }
      else if (discPreIndex.get() && indexerDiscPass){
          indexer.set(false);
          indexerDiscPass = false;
          indexerHasRun = true;
      }
      else if (discCountCurrent >= 1 && indexerHasRun) {
          discCountCurrent = discCountCurrent - 1;
          indexerHasRun = false;
      }
      else {
          indexer.set(false);
      }
  }

  public boolean isDiscLoaded() {  
    return discPreIndex.get();
  }
  
  public int getDiscCountCurrent()
  {
      return discCountCurrent;
  }
}
