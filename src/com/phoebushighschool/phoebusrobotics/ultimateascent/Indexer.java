package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

/*
 */
public class Indexer {

    protected GameMech gameMech;
    protected Relay indexer;
    public DigitalInput discPreIndex;
    public int discCountCurrent;
    
  public Indexer()
  {
      gameMech = new GameMech();
      indexer = new Relay(Parameters.DiscIndexerRelayChannel);
      discPreIndex = new DigitalInput(Parameters.DiscIsLoadedInput);
      discCountCurrent = Parameters.discCountInit;
      
  }

  /** 
   *  This method will index one disc into the shooter.
   */
  public void indexOneDisc() {
//      if (discPostIndex.get()) {
//          /**
//           *  DO NOTHING
//           */
//      }
//      else {
//          /**
//           * Check the disc's position Turn on/off motor etc
//           */
//          if (discPreIndex.get()){
//              indexer.set(Relay.Value.kOn);
//              if (discPostIndex.get()) {
//                  indexer.set(Relay.Value.kOff);
//              }
//          }
//      }
      if (discPreIndex.get() && discCountCurrent >= 1){
        indexer.set(Relay.Value.kForward);
            if (discPreIndex.get() == false){                
                if (discPreIndex.get()){
                    indexer.set(Relay.Value.kOff);
                    discCountCurrent = discCountCurrent - 1;
                }
            }
      }
  }

  public boolean isDiscLoaded() {  
    return discPreIndex.get();
  }
}