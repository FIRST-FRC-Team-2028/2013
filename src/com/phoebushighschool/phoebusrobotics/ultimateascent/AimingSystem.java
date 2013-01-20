package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.camera.*;
import edu.wpi.first.wpilibj.image.*;

/*
 */
public class AimingSystem implements PIDSource {

    final int XMAXSIZE = 24;
    final int XMINSIZE = 24;
    final int YMAXSIZE = 24;
    final int YMINSIZE = 48;
    final double xMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double xMin[] = {.4, .6, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, 0.6, 0};
    final double yMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double yMin[] = {.4, .6, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .6, 0};
    
    final int RECTANGULARITY_LIMIT = 60;
    final int ASPECT_RATIO_LIMIT = 75;
    final int X_EDGE_LIMIT = 40;
    final int Y_EDGE_LIMIT = 60;
    
    protected UltimateAscentBot robot;
    public AxisCamera camera;
    public Ultrasonic ultrasonicSensor;
    CriteriaCollection cc;
    ColorImage image;
    BinaryImage thresholdImage;
    BinaryImage convexHullImage;
    BinaryImage filteredImage;
    ParticleAnalysisReport r;
    HighTargets[] highTargets;
    MiddleTargets[] middleTargets;
    Target target;

    public AimingSystem() {
        camera = AxisCamera.getInstance(Parameters.cameraIP);
        camera.writeResolution(AxisCamera.ResolutionT.k320x240);
        camera.writeExposurePriority(AxisCamera.ExposurePriorityT.imageQuality);
        camera.writeExposureControl(AxisCamera.ExposureT.hold);
        camera.writeWhiteBalance(AxisCamera.WhiteBalanceT.fixedIndoor);
        cc = new CriteriaCollection();
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, 500, 65535, false);
    }

    public class Scores {

        double rectangularity;
        double aspectRatioHigh;
        double aspectRatioMiddle;
        double xEdge;
        double yEdge;
    }

    public class HighTargets {
        double rectangularity;
        double aspectRatioHigh;
        double xEdge;
        double yEdge;
    }
    
    public class MiddleTargets {
        double rectangularity;
        double aspectRatioMiddle;
        double xEdge;
        double yEdge;
    }
    
    public class Target {
        double rectangularity;
        double aspectRatio;
        double xEdge;
        double yEdge;
    }
    /**
     * This method will find the target we are aiming at, and it's center of
     * mass in the x axis.
     */
    public void processImage(boolean leftTarget) {
        try {
            image = camera.getImage();
            thresholdImage = image.thresholdHSV(110, 150, 200, 255, 240, 255);
            convexHullImage = thresholdImage.convexHull(true);
            filteredImage = convexHullImage.particleFilter(cc);
            Scores scores[] = new Scores[filteredImage.getNumberParticles()];
            
            int nHigh = 0;
            int nMiddle = 0;

            for (int i = 0; i < scores.length; i++) {
                r = filteredImage.getParticleAnalysisReport(i);
                scores[i] = new Scores();

                scores[i].rectangularity = scoreRectangularity(r);
                scores[i].aspectRatioHigh = scoreAspectRatio(filteredImage, r, i, false);
                scores[i].aspectRatioMiddle = scoreAspectRatio(filteredImage, r, i, true);
                scores[i].xEdge = scoreXEdge(filteredImage, r);
                scores[i].yEdge = scoreYEdge(filteredImage, r);
                
                if (scoreCompare(scores[i], false)) {
                    highTargets[nHigh].rectangularity = scores[i].rectangularity;
                    highTargets[nHigh].aspectRatioHigh = scores[i].aspectRatioHigh;
                    highTargets[nHigh].xEdge = scores[i].xEdge;
                    highTargets[nHigh].yEdge = scores[i].yEdge;
                    nHigh++;
                } else if (scoreCompare(scores[i], true)) {
                    middleTargets[nMiddle].rectangularity = scores[i].rectangularity;
                    middleTargets[nMiddle].aspectRatioMiddle = scores[i].aspectRatioMiddle;
                    middleTargets[nMiddle].xEdge = scores[i].xEdge;
                    middleTargets[nMiddle].yEdge = scores[i].yEdge;
                }
            }

            filteredImage.free();
            convexHullImage.free();
            thresholdImage.free();
            image.free();
        } catch (NIVisionException e) {
        } catch (AxisCameraException e) {
        }
    }

    public double scoreRectangularity(ParticleAnalysisReport r) {
        if ((r.boundingRectHeight * r.boundingRectWidth) != 0.0) {
            return 100 * (r.particleArea / (r.boundingRectHeight * r.boundingRectWidth));
        } else {
            return 0.0;
        }
    }

    public double scoreAspectRatio(BinaryImage image,
            ParticleAnalysisReport report,
            int particleNumber,
            boolean middle)
            throws NIVisionException {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false,
                NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false,
                NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        if (middle) {
            idealAspectRatio = 62 / 29;
        } else {
            idealAspectRatio = 62 / 20;
        }

        if (report.boundingRectWidth > report.boundingRectHeight) {
            aspectRatio = 100 * (1 - Math.abs((1 - ((rectLong / rectShort) / idealAspectRatio))));
        } else {
            aspectRatio = 100 * (1 - Math.abs((1 - ((rectShort / rectLong) / idealAspectRatio))));
        }

        return Math.max(0, Math.min(aspectRatio, 100.0));
    }

    public double scoreXEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException {
        double total = 0;
        LinearAverages averages;

        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_COLUMN_AVERAGES, rect);
        float columnAverages[] = averages.getColumnAverages();
        for (int i = 0; i < (columnAverages.length); i++) {
            if (xMin[(i * (XMINSIZE - 1) / columnAverages.length)] < columnAverages[i]
                    && columnAverages[i] < xMax[i * (XMAXSIZE - 1) / columnAverages.length]) {
                total++;
            }
        }
        total = 100 * total / (columnAverages.length);
        return total;
    }

    public double scoreYEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException
    {
        double total = 0;
        LinearAverages averages;
        
        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_ROW_AVERAGES, rect);
        float rowAverages[] = averages.getRowAverages();
        for(int i=0; i < (rowAverages.length); i++){
                if(yMin[(i*(YMINSIZE-1)/rowAverages.length)] < rowAverages[i] 
                   && rowAverages[i] < yMax[i*(YMAXSIZE-1)/rowAverages.length]){
                        total++;
                }
        }
        total = 100*total/(rowAverages.length);
        return total;
    }
    
    boolean scoreCompare(Scores scores, boolean outer){
            boolean isTarget = true;

            isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
            if(outer){
                    isTarget &= scores.aspectRatioMiddle > ASPECT_RATIO_LIMIT;
            } else {
                    isTarget &= scores.aspectRatioHigh > ASPECT_RATIO_LIMIT;
            }
            isTarget &= scores.xEdge > X_EDGE_LIMIT;
            isTarget &= scores.yEdge > Y_EDGE_LIMIT;

            return isTarget;
    }
    
    Target TargetCompare(HighTargets[] highT, MiddleTargets[] middleT, boolean middle, boolean leftTarget) {
        Target t = null;
        if (middle) {
            
        } else {
            for (int i = 0; i < highT.length; i++) {
                if (t == null) {
                    t.rectangularity = highT[i].rectangularity;
                    t.aspectRatio = highT[i].aspectRatioHigh;
                    t.xEdge = highT[i].xEdge;
                    t.yEdge = highT[i].yEdge;
                } else if (t.rectangularity < highT[i].rectangularity
                        && t.aspectRatio < highT[i].aspectRatioHigh) {
                    t.rectangularity = highT[i].rectangularity;
                    t.aspectRatio = highT[i].aspectRatioHigh;
                    t.xEdge = highT[i].xEdge;
                    t.yEdge = highT[i].yEdge;
                }
            }
        }
        return t;
    }
    /**
     * This method returns true if the target is +/- x degree of the camera's
     * center, and false otherwise.
     */
    public boolean isAimedAtTarget() {
        return false;
    }

    public double pidGet() {
        return 0.0;
    }

    public double getDegreesToTarget() {
        return 0.0;
    }
}