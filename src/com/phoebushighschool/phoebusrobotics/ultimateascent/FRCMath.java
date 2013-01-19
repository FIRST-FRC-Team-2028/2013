package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import java.lang.String;

/*
 */
public class FRCMath {

  
  /** 
   *  This method will take the absolute value of a number
   */
  public static double abs(double value) {
  return 0.0;
  }

  /** 
   *  This methhod takes a radian and returns degrees
   */
    public double ConvertRadiansToDegrees(double radians)
    {
        return (radians * 180.0) / 3.1415926535898;
    }

    /***
     * Normalize()
     * 
     * this method reduces a number to 5 decimal places
     * 
     * @param num a very long decimal
     * @return 
     */
    static public String Normalize(double num)
    {
        int t = ((int)(num * 1000.0));
        double d = t / 1000.0;
        String str = "" + d;
        while (str.length() < 5)
            str += "0";
        return str;
        //return String.format("%10.4f", num);
    }

}