package com.phoebushighschool.phoebusrobotics.ultimateascent;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.camera.*;
import edu.wpi.first.wpilibj.image.*;
import java.util.Vector;

/*
 */
public class AimingSystem implements PIDSource
{

    final int XMAXSIZE = 24;
    final int XMINSIZE = 24;
    final int YMAXSIZE = 24;
    final int YMINSIZE = 48;
    final double xMax[] =
    {
        1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1
    };
    final double xMin[] =
    {
        .4, .6, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, 0.6, 0
    };
    final double yMax[] =
    {
        1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1
    };
    final double yMin[] =
    {
        .4, .6, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
        .05, .05, .6, 0
    };
    final int RECTANGULARITY_LIMIT = 60;
    final int ASPECT_RATIO_LIMIT = 75;
    final int X_EDGE_LIMIT = 40;
    final int Y_EDGE_LIMIT = 60;
    final double TARGET_WIDTH = 62.0;
    final double climbPosition = Parameters.kCameraClimbPosition;
    final double shootPosition = Parameters.kCameraShooterPosition;
    int imageState = 0;
    AxisCamera camera;
    Ultrasonic ultrasonicSensor;
    Servo servo;
    CriteriaCollection cc;
    ColorImage image;
    BinaryImage thresholdImage;
    BinaryImage convexHullImage;
    BinaryImage filteredImage;
    ParticleAnalysisReport[] reports = null;
    ParticleAnalysisReport t = null;
    AimingSystem.Target target = null;
    private boolean busy = false;

    public AimingSystem()
    {
        camera = AxisCamera.getInstance(Parameters.cameraIP);
        camera.writeResolution(AxisCamera.ResolutionT.k640x480);
        camera.writeExposurePriority(AxisCamera.ExposurePriorityT.imageQuality);
        camera.writeExposureControl(AxisCamera.ExposureT.hold);
        camera.writeWhiteBalance(AxisCamera.WhiteBalanceT.fixedIndoor);
        cc = new CriteriaCollection();
        cc.addCriteria(NIVision.MeasurementType.IMAQ_MT_AREA, 500, 65535, false);
        ultrasonicSensor = new Ultrasonic(Parameters.UltrasonicAnalogChannel);
        servo = new Servo(Parameters.cameraServoPWMChannel);
        setShootPosition();
    }

    public class Scores
    {

        double rectangularity;
        double aspectRatioHigh;
        double aspectRatioMiddle;
        double xEdge;
        double yEdge;

        public Scores()
        {
            rectangularity = 0.0;
            aspectRatioHigh = 0.0;
            aspectRatioMiddle = 0.0;
            xEdge = 0.0;
            yEdge = 0.0;
        }
    }

    public class Target
    {

        double aspectRatio;
        boolean middle;
        double center_mass_x;
        double target_width;

        public Target()
        {
            aspectRatio = 0.0;
            middle = true;
            center_mass_x = 0.0;
            target_width = 0.0;
        }
    }

    /**
     * This method will find the target we are aiming at, and it's center of
     * mass in the x axis.
     */
    public void processImage()
    {
        try
        {
            if (busy)
            {
                return;
            }
//            if (reports == null)
//            {
//                image = camera.getImage();
//                thresholdImage = image.thresholdRGB(25, 75, 185, 255, 145, 255);  // green values
////              thresholdImage = image.thresholdHSV(115, 125, 195, 255, 220, 255);
//                convexHullImage = thresholdImage.convexHull(true);
//                filteredImage = convexHullImage.particleFilter(cc);
//                reports = filteredImage.getOrderedParticleAnalysisReports();
////                scoreParticles();
//            } else if (reports != null)
//            {
            switch (imageState)
            {
                case 0:
                    busy = true;
                    image = camera.getImage();
//                    System.out.println("Got image");
                    imageState++;
                    busy = false;
                    break;
                case 1:
                    busy = true;
                    thresholdImage = image.thresholdRGB(0, 70, 185, 255, 145, 225);  // green values
//                      thresholdImage = image.thresholdHSV(115, 125, 195, 255, 220, 255);
                    image.free();
                    image = null;
//                    System.out.println("Got threshold");
                    imageState++;
                    busy = false;
                    break;
                case 2:
                    busy = true;
                    convexHullImage = thresholdImage.convexHull(true);
//                    System.out.println("Got convex hull");
                    imageState++;
                    busy = false;
                    break;
                case 3:
                    busy = true;
                    filteredImage = convexHullImage.particleFilter(cc);
                    convexHullImage.free();
                    convexHullImage = null;
//                    System.out.println("Got filtered");
                    imageState++;
                    busy = false;
                    break;
                case 4:
                    busy = true;
                    reports = filteredImage.getOrderedParticleAnalysisReports();
                    scoreParticles(reports);
                    thresholdImage.free();
                    filteredImage.free();
                    thresholdImage = null;
                    filteredImage = null;
                    imageState = 0;
                    busy = false;
                    break;
            }
//            }

//            for (int i = 0; i < reports.length; i++)
//            {
//                if (r == null)
//                {
//                    r = reports[i];
//                } else if (r.particleArea < reports[i].particleArea)
//                {
//                    r = reports[i];
//                }
//            }

        } catch (NIVisionException e)
        {
//            System.out.println(e);
//            e.printStackTrace();
            busy = false;
        } catch (AxisCameraException e)
        {
//            System.out.println(e);
//            e.printStackTrace();
            busy = false;
        }
    }

    public void DisableAimingSystem()
    {
        try
        {
            if (image != null) {
                image.free();
                image = null;
            }
            if (thresholdImage != null) {
                thresholdImage.free();
                thresholdImage = null;
            }
            if (convexHullImage != null) {
                convexHullImage.free();
                convexHullImage = null;
            }
            if (filteredImage != null) {
                filteredImage.free();
                filteredImage = null;
            }
        } catch (NIVisionException e)
        {
        }
        imageState = 0;
        busy = false;
    }

    /**
     * scoreParticles()
     *
     * This method takes the particle analysis that was produced in
     * getParticleAnalysis() and determines which particles are targets and then
     * which target is the one we are aiming for. The targets are selected for
     * by scoring the particles based on rectangularity, aspect ratio,
     * hollowness, and straightness of lines. After determining particles that
     * have characteristics of targets the particles are compared based on
     * aspect ratio, and the target with the best aspect ratio is selected.
     *
     * @throws NIVisionException
     */
    public void scoreParticles(ParticleAnalysisReport[] report) throws NIVisionException
    {
        boolean middle = Parameters.GO_FOR_MIDDLE_TARGET;
        Vector targets = new Vector();

        for (int i = 0; i < report.length; i++)
        {
            Scores score = new Scores();
            score.rectangularity = scoreRectangularity(report[i]);
            if (!middle)
            {
                score.aspectRatioHigh = scoreAspectRatio(filteredImage, report[i], i, false);
            } else
            {
                score.aspectRatioMiddle = scoreAspectRatio(filteredImage, report[i], i, true);
            }
            score.xEdge = scoreXEdge(thresholdImage, report[i]);
            score.yEdge = scoreYEdge(thresholdImage, report[i]);

            if (scoreCompare(score, middle))
            {
                Target t = new Target();
                t.aspectRatio = score.aspectRatioHigh;
                t.center_mass_x = report[i].center_mass_x;
                t.target_width = report[i].boundingRectWidth;
                t.middle = middle;
                targets.addElement(t);
            }
        }

        target = TargetCompare(targets);
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
    public double scoreRectangularity(ParticleAnalysisReport r)
    {
        if ((r.boundingRectHeight * r.boundingRectWidth) != 0.0)
        {
            return 100 * (r.particleArea / (r.boundingRectHeight * r.boundingRectWidth));
        } else
        {
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
            int particleNumber, boolean middle) throws NIVisionException
    {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.MeasureParticle(image.image, particleNumber, false,
                NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.MeasureParticle(image.image, particleNumber, false,
                NIVision.MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
        if (middle)
        {
            idealAspectRatio = 62 / 29;
        } else
        {
            idealAspectRatio = 62 / 20;
        }

        if (report.boundingRectWidth > report.boundingRectHeight)
        {
            aspectRatio = 100 * (1 - Math.abs((1 - ((rectLong / rectShort) / idealAspectRatio))));
        } else
        {
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
     * @param image the image from which the particle originated, needs to be
     * pre-convex hull
     * @param report the analysis of the particle
     * @return the score of the particle from 0 - 100
     * @throws NIVisionException
     */
    public double scoreXEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException
    {
        double total = 0;
        LinearAverages averages;

        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_COLUMN_AVERAGES, rect);
        float columnAverages[] = averages.getColumnAverages();
        for (int i = 0; i < (columnAverages.length); i++)
        {
            if (xMin[(i * (XMINSIZE - 1) / columnAverages.length)] < columnAverages[i]
                    && columnAverages[i] < xMax[i * (XMAXSIZE - 1) / columnAverages.length])
            {
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
     * @param image the image from which the particle originated, needs to be
     * pre-convex hull
     * @param report the analysis of the particle
     * @return the score of the particle from 0 -100
     * @throws NIVisionException
     */
    public double scoreYEdge(BinaryImage image, ParticleAnalysisReport report) throws NIVisionException
    {
        double total = 0;
        LinearAverages averages;

        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
        averages = NIVision.getLinearAverages(image.image, LinearAverages.LinearAveragesMode.IMAQ_ROW_AVERAGES, rect);
        float rowAverages[] = averages.getRowAverages();
        for (int i = 0; i < (rowAverages.length); i++)
        {
            if (yMin[(i * (YMINSIZE - 1) / rowAverages.length)] < rowAverages[i]
                    && rowAverages[i] < yMax[i * (YMAXSIZE - 1) / rowAverages.length])
            {
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
    boolean scoreCompare(AimingSystem.Scores scores, boolean middle)
    {
        boolean isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if (middle)
        {
            isTarget &= scores.aspectRatioMiddle > ASPECT_RATIO_LIMIT;
        } else
        {
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
    AimingSystem.Target TargetCompare(Vector targets)
    {
        AimingSystem.Target t = null;
        for (int i = 0; i < targets.size(); i++)
        {
            t = new Target();
            Target temp = (Target) targets.elementAt(i);
            if (i == 0)
            {
                t.aspectRatio = temp.aspectRatio;
                t.center_mass_x = temp.center_mass_x;
                t.target_width = temp.target_width;
                t.middle = temp.middle;
            } else if (t.aspectRatio < temp.aspectRatio)
            {
                t.aspectRatio = temp.aspectRatio;
                t.center_mass_x = temp.center_mass_x;
                t.target_width = temp.target_width;
            }
        }
        return t;
    }

    /**
     * isAimedAtTarget()
     *
     * This method determines if we are aimed at the target.
     *
     * @return true if the target is within +/- 1 degree, false if outside of
     * that range
     */
    public boolean isAimedAtTarget() throws NoTargetFoundException
    {
        double temp = getDegreesToTarget();
        if (temp < 1.0 && temp > -1.0)
        {
            return true;
        }
        return false;
    }

    /**
     * isDistance();
     * 
     * This method determines if we are the right distance to the target to make
     * our shots.
     * 
     * @return true - we are the right distance from the target
     *         false - we are not the right distance from the target
     */
    public boolean isCorrectDistance(boolean middle) throws NoTargetFoundException {
        double temp = getDistanceToTarget();
        if (temp < Parameters.MAX_SHOOT_DISTANCE && temp > Parameters.MIN_SHOOT_DISTANCE && middle) {
            return true;
        }
        if (temp < (Parameters.MAX_SHOOT_DISTANCE + 36.0) && temp > (Parameters.MIN_SHOOT_DISTANCE + 36.0) && !middle)
        {
            return true;
        }
        return false;
    }
    
    public double pidGet()
    {
        double temp;
        try
        {
            temp = getDegreesToTarget();
        } catch (NoTargetFoundException e)
        {
            return 0.0;
        }
        return temp * -1.0;
    }

    /**
     * getDegreesToTarget()
     *
     * This method returns the angle to the target.
     *
     * @return the angle to the target, negative th robot needs to turn left,
     * positive, right
     */
    public double getDegreesToTarget() throws NoTargetFoundException
    {
        processImage();
        double offset;
        if (target == null)
        {
            throw new NoTargetFoundException("No target found.");
        } else
        {
            offset = ((double) target.center_mass_x) - (camera.getResolution().width / 2.0);
            offset = offset * (TARGET_WIDTH / ((double) target.target_width));
            offset = MathUtils.atan(offset / getDistanceToTarget());
            return Math.toDegrees(offset);
        }
//        else
//        {
//            offset = ((double) r.center_mass_x) - (IMAGE_WIDTH / 2.0);
//            offset = offset * (24.0 / ((double) r.boundingRectWidth));
//            offset = MathUtils.atan(offset / getDistanceWCamera());
//            return Math.toDegrees(offset);
//        }
    }

    /**
     * ConvertRadiansToDegrees()
     *
     * This method converts a radian measure to degrees.
     *
     * @param radians an angle in radians
     * @return an angle in degrees
     */
    public double ConvertRadiansToDegrees(double radians)
    {
        return (radians * 180.0) / 3.1415926535898;
    }

    /**
     * getDistanceWUltrasonic()
     *
     * This method returns the distance to what the robot is facing.
     *
     * @return the distance to what the robot is facing in inches
     */
    public double getDistanceWUltrasonic()
    {
        return ultrasonicSensor.getAveragedDistance();
    }

    /**
     * getDistanceWCamera()
     *
     * This method gets the distance to the target using the camera.
     *
     * @return the distance to what the camera thinks is the target in inches
     */
    public double getDistanceWCamera() throws NoTargetFoundException
    {
        double w = 0.0;
        if (target == null)
        {
            throw new NoTargetFoundException("No target found");
        }
            w = ((double) camera.getResolution().width) * (TARGET_WIDTH / target.target_width);
            w = w / 2;
//        if (t != null)
//        {
//            w = IMAGE_WIDTH * (24.0 / r.boundingRectWidth);
//            w = w / 2;
//        }
        return (w / Math.tan(Math.toRadians(24.0))) + 10.0;
    }

    /**
     * getDistanceToTarget()
     *
     * This method returns what we believe to be the distance to the target. If
     * the camera distance and the ultrasonic distance are outside of +/- 5% of
     * each other, we choose the larger value. If they are within 5% of each
     * other we take the average of both and return that.
     *
     * @return the distance to the target.
     */
    public double getDistanceToTarget() throws NoTargetFoundException
    {
        double cameraD = getDistanceWCamera();
        double ultrasonicD = getDistanceWUltrasonic();
        if ((cameraD / ultrasonicD) > 1.05
                || (cameraD / ultrasonicD) < 0.95)
        {
            return Math.max(cameraD, ultrasonicD);
        } else
        {
            return (cameraD + ultrasonicD) / 2.0;
        }
//        return cameraD;
    }

    public double getServoPosition()
    {
        return servo.getPosition();
    }

    /**
     * setShootPosition()
     *
     * This method sets the camera to the shooting position.
     */
    public void setShootPosition()
    {
        servo.setAngle(shootPosition);
    }

    /**
     * setClimbPosition()
     *
     * This method sets the camera to the climbing position.
     */
    public void setClimbPosition()
    {
        servo.setAngle(climbPosition);
    }
}
