package com.PhoebusHighSchool.PhoebusRobotics.UltimateAscent;

import com.sun.squawk.util.MathUtils;
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
    final int IMAGE_WIDTH = 320;
    protected UltimateAscentBot robot;
    public AxisCamera camera;
    public Ultrasonic ultrasonicSensor;
    CriteriaCollection cc;
    ColorImage image;
    BinaryImage thresholdImage;
    BinaryImage convexHullImage;
    BinaryImage filteredImage;
    ParticleAnalysisReport r;
    Target[] highTargets;
    Target[] middleTargets;
    Target target;

    public AimingSystem() {
        camera = AxisCamera.getInstance(Parameters.cameraIP);
        camera.writeResolution(AxisCamera.ResolutionT.k320x240);
        camera.writeExposurePriority(AxisCamera.ExposurePriorityT.imageQuality);
        camera.writeExposureControl(AxisCamera.ExposureT.hold);
        camera.writeWhiteBalance(AxisCamera.WhiteBalanceT.fixedIndoor);
        cc = new CriteriaCollection();
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, 500, 65535, false);
        ultrasonicSensor = new Ultrasonic(Parameters.UltrasonicAnalogChannel);
    }

    public class Scores {

        double rectangularity;
        double aspectRatioHigh;
        double aspectRatioMiddle;
        double xEdge;
        double yEdge;
    }

    public class Target {

        double aspectRatio;
        double center_mass_x;
    }

    /**
     * This method will find the target we are aiming at, and it's center of
     * mass in the x axis.
     */
    public void processImage(boolean middle) {
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
                scores[i].xEdge = scoreXEdge(thresholdImage, r);
                scores[i].yEdge = scoreYEdge(thresholdImage, r);

                if (scoreCompare(scores[i], false)) {
                    highTargets[nHigh].aspectRatio = scores[i].aspectRatioHigh;
                    highTargets[nHigh].center_mass_x = r.center_mass_x;
                    nHigh++;
                } else if (scoreCompare(scores[i], true)) {
                    middleTargets[nMiddle].aspectRatio = scores[i].aspectRatioMiddle;
                    middleTargets[nMiddle].center_mass_x = r.center_mass_x;
                    nMiddle++;
                }
            }

            target = TargetCompare(highTargets, middleTargets, middle);

            filteredImage.free();
            convexHullImage.free();
            thresholdImage.free();
            image.free();
        } catch (NIVisionException e) {
        } catch (AxisCameraException e) {
        }
    }

    /**
     * scoreRectangularity()
     *
     * This method scores a particle from 0 - 100 based on the ratio of the area
     * of the particle to the area of the rectangle that bounds it. A score of
     * 100 means that the particle is perfectly rectangular.
     *
     * @param r the analysis report for the particle, used to determine the area
     * of the bounding rectangle.
     * @return the score of the particle.
     */
    public double scoreRectangularity(ParticleAnalysisReport r) {
        if ((r.boundingRectHeight * r.boundingRectWidth) != 0.0) {
            return 100 * (r.particleArea / (r.boundingRectHeight * r.boundingRectWidth));
        } else {
            return 0.0;
        }
    }

    /**
     * scoreAspectRatio()
     *
     * This method scores the particle from 0 - 100 based on how similar its
     * aspect ratio is to the aspect ratio of either the high target or the
     * middle target. A score of 100 means that the target has an aspect ratio
     * identical to either the middle or high target.
     *
     * @param image the image from which the particle originates.
     * @param report the analysis of the particle
     * @param middle true if aspect ratio to be compared to is the middle
     * target, false if it is the high target.
     * @return the score of the particle, from 0 - 100
     * @throws NIVisionException
     */
    public double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report,
            int particleNumber, boolean middle) throws NIVisionException {
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

    /**
     * scoreXEdge()
     *
     * This method scores the particle from 0 - 100 based on how solid the
     * vertical edges are and how hollow the center of the particle are.
     *
     * @param image the image from which the particle originated, needs to be pre convex hull
     * @param report the analysis of the particle
     * @return the score of the particle from 0 - 100
     * @throws NIVisionException
     */
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

    /**
     * scoreYEdge()
     *
     * This method scores the particle from 0 - 100 based on how solid the
     * horizontal edges are and how hollow the center of the particle are.
     *
     * @param image the image from which the particle originated, needs to be pre convex hull
     * @param report the analysis of the particle
     * @return the score of the particle from 0 -100
     * @throws NIVisionException
     */
    public double scoreYEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException {
        double total = 0;
        LinearAverages averages;

        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_ROW_AVERAGES, rect);
        float rowAverages[] = averages.getRowAverages();
        for (int i = 0; i < (rowAverages.length); i++) {
            if (yMin[(i * (YMINSIZE - 1) / rowAverages.length)] < rowAverages[i]
                    && rowAverages[i] < yMax[i * (YMAXSIZE - 1) / rowAverages.length]) {
                total++;
            }
        }
        total = 100 * total / (rowAverages.length);
        return total;
    }

    /**
     * scoreCompare()
     *
     * This method determines if a particle is a target based on if the
     * particles score compared to an given value.
     *
     * @param scores the score of the particle to be compared to
     * @param middle true if the score to be compared to is the middle target,
     * false if it is the high target
     * @return returns true if it qualifies as a target
     */
    boolean scoreCompare(Scores scores, boolean middle) {
        boolean isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if (middle) {
            isTarget &= scores.aspectRatioMiddle > ASPECT_RATIO_LIMIT;
        } else {
            isTarget &= scores.aspectRatioHigh > ASPECT_RATIO_LIMIT;
        }
        isTarget &= scores.xEdge > X_EDGE_LIMIT;
        isTarget &= scores.yEdge > Y_EDGE_LIMIT;

        return isTarget;
    }

    /**
     * TargetCompare()
     *
     * This method identifies which target of all the targets scoreCompare()
     * identified is actually the target we are shooting at. if the aspect ratio
     * score of the current particle is larger than the former particle the
     * current particle is substituted for the former. *note: even thought there
     * are two middle targets, based on how the particles are analyzed the
     * target on our current side of the pyramid will be the better target.
     *
     * @param highT the list of all targets determined to be high targets
     * @param middleT the list of all targets determined to be middle targets
     * @param middle true if the target that we are shooting at is the middle
     * target, false if the target we are shooting at is the high target.
     * @return the target we are shooting at.
     */
    Target TargetCompare(Target[] highT, Target[] middleT, boolean middle) {
        Target t = null;
        if (middle) {
            for (int i = 0; i < middleT.length; i++) {
                if (t == null) {
                    t.aspectRatio = middleT[i].aspectRatio;
                    t.center_mass_x = middleT[i].center_mass_x;
                } else if (t.aspectRatio < middleT[i].aspectRatio) {
                    t.aspectRatio = middleT[i].aspectRatio;
                    t.center_mass_x = middleT[i].center_mass_x;
                }
            }
        } else {
            for (int i = 0; i < highT.length; i++) {
                if (t == null) {
                    t.aspectRatio = highT[i].aspectRatio;
                    t.center_mass_x = highT[i].center_mass_x;
                } else if (t.aspectRatio < highT[i].aspectRatio) {
                    t.aspectRatio = highT[i].aspectRatio;
                    t.center_mass_x = highT[i].center_mass_x;
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
        if (getDegreesToTarget() < 1.0 && getDegreesToTarget() > -1.0) {
            return true;
        }
        return false;
    }

    public double pidGet() {
        return 0.0;
    }

    public double getDegreesToTarget() {
        double offset = 0.0;
        if (r != null) {
            offset = target.center_mass_x - (IMAGE_WIDTH / 2);
            offset = MathUtils.atan(offset / 367.97488075578);
        }
        return ConvertRadiansToDegrees(offset);
    }

    public double ConvertRadiansToDegrees(double radians) {
        return (radians * 180.0) / 3.1415926535898;
    }

    public double getDistance() {
        return ultrasonicSensor.getDistance();
    }
}