package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

/*
 */
public class Indexer {

    protected GameMech gameMech;
    protected Solenoid indexer;
    public DigitalInput discPreIndex;
    public int discCountCurrent;
    public boolean indexerHasRun;
    
  public Indexer()
  {
      gameMech = new GameMech();
      indexer = new Solenoid(Parameters.DiscIndexerSolenoidChannel);
      discPreIndex = new DigitalInput(Parameters.DiscInShooterGPIOChannel);
      discCountCurrent = Parameters.discCountInit;
      indexerHasRun = false;
  }

  /** 
   *  This method will index one disc into the shooter.
   */
  public void indexOneDisc() {
      if (discPreIndex.get() && discCountCurrent >= 1 && !indexerHasRun){
        indexer.set(true);
            if (!discPreIndex.get()){                
                if (discPreIndex.get()){
                    indexer.set(false);
                }
            }
            indexerHasRun = true;
      }
      if (discCountCurrent >= 1 && indexerHasRun) {
          discCountCurrent = discCountCurrent - 1;
          indexerHasRun = false;
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
