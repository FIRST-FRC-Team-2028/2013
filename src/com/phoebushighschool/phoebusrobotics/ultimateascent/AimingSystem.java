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
    protected UltimateAscentBot robot;
    public AxisCamera camera;
    public Ultrasonic ultrasonicSensor;
    CriteriaCollection cc;
    ColorImage image;
    BinaryImage thresholdImage;
    BinaryImage convexHullImage;
    BinaryImage filteredImage;
    ParticleAnalysisReport r;

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

    /**
     * This method will find the target we are aiming at, and it's center of
     * mass in the x axis.
     */
    public void processImage() {
        try {
            image = camera.getImage();
            thresholdImage = image.thresholdHSV(110, 150, 200, 255, 240, 255);
            convexHullImage = thresholdImage.convexHull(true);
            filteredImage = convexHullImage.particleFilter(cc);
            Scores scores[] = new Scores[filteredImage.getNumberParticles()];

            for (int i = 0; i < scores.length; i++) {
                r = filteredImage.getParticleAnalysisReport(i);
                scores[i] = new Scores();

                scores[i].rectangularity = scoreRectangularity(r);
                scores[i].aspectRatioHigh = scoreAspectRatio(filteredImage, r, i, false);
                scores[i].aspectRatioMiddle = scoreAspectRatio(filteredImage, r, i, true);
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