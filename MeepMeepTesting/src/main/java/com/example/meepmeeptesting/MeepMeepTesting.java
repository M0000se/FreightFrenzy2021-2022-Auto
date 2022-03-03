package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.SampleMecanumDrive;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting
{
    static int offset = 12; //TODO 0 for left, change for right  -35 for left, 12 for right
    static int side = 0;
    static double startDelay = 0.0; // wait after the corresponding part of the trajectory is complete
    static double shipHubDelay = 0.0;
    static double spinDelay = 0.0;

    //Webcam
    static int x_center = 200; //middle of the webcam view
    static int center_accuracy = 70;

    //Lift variables
    static int liftIntake = -3700;
    static int liftLow  = -2600; //encoder value for the low ship hub position of the lift
    static int liftMid  = -1; //encoder value for the mid ship hub position of the lift
    static int liftHigh = 0; //encoder value for the high ship hub position of the lift
    static int p_accuracy = 50; // used by lift_pid
    static double p = 0.005;

    //Dump variables (Dump is arm)
    static double dumpStraight = 0.775; //position value for the Intake position of the dump (straight angle)
    static double dumpLow = 0.775;
    static double dumpMid = 0.72;
    static double dumpHigh = 0.55;    //position value for the high ship hub position of the dump
    static double dumpFold = 0.5;      //position value for the folded position of the dump (NOT THE COMPLETELY FOLDED LIKE HOW WE START)

    //Red-specific variables
    static double red_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    double red_basic_spinDelay = 6.0;

    //Blue-specific variables
    static double blue_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    static double blue_basic_spinDelay = 6.0;

    static double slow = 20;

    static double TRACK_WIDTH = 8; // in TODO test
    static double MAX_VEL = 70;
    static double MAX_ACCEL = 70;
    static double MAX_ANG_VEL = 12;
    static double MAX_ANG_ACCEL = 20;

    public static void main(String[] args)
    {
        MeepMeep meepMeep = new MeepMeep(700);

        // Declare a MeepMeep instance
        // With a field size of 800 pixels
        /*
        Pose2d startPose = new Pose2d(12, 60, Math.toRadians(-90)); //-35 for left, 12 for right
        Vector2d location2 = new Vector2d(-12, 42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-60, 45, Math.toRadians (0));
        Pose2d spinner_shift = new Pose2d(-60, 50, Math.toRadians (0));
        //Pose2d spinner_shift = new Pose2d(-55, 55, Math.toRadians (0));
        Vector2d warehouse_midpoint = new Vector2d(-25, 65);
        Pose2d warehouse = new Pose2d(40, 65, Math.toRadians (0));

        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(70, 70, 25, 25,
                        15)
                // Option: Set theme. Default = ColorSchemeRedDark()
                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineTo(location2)//location of the red shipping hub
                                .addTemporalMarker(1000, () -> {}) //servo dump
                                .waitSeconds(1)
                                .lineToLinearHeading(spinner)
                                .lineToLinearHeading(spinner_shift)
                                .addTemporalMarker(5000, () -> {}) // motor spinner wheel
                                .waitSeconds(3.5)
                                .splineToConstantHeading(warehouse_midpoint, 0)
                                .lineToLinearHeading(warehouse)
                                .build());
        */

        /*Pose2d startPose = new Pose2d(0, 60, Math.toRadians(-90));
        Vector2d location2 = new Vector2d(-12, 42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-50, 55, Math.toRadians (90));
        //Pose2d spinner_shift = new Pose2d(-55, 55, Math.toRadians (0));
        Pose2d warehouse_midpoint = new Pose2d(-20, 60, Math.toRadians (0));
        Pose2d warehouse = new Pose2d(40, 65, Math.toRadians (0));

        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, 25, 25,
                        12)
                // Option: Set theme. Default = ColorSchemeRedDark()
                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineTo(location2)//location of the red shipping hub
                                .addTemporalMarker(1000, () -> {}) //servo dump
                                .waitSeconds(1)
                                .lineToLinearHeading(spinner)
                                .addTemporalMarker(5000, () -> {}) // motor spinner wheel
                                .waitSeconds(3.5)
                                .lineToLinearHeading(warehouse_midpoint)
                                .lineToLinearHeading(warehouse)
                                .build());
        */


        /*Pose2d startPose = new Pose2d(0, -60, Math.toRadians(90));
        Vector2d location2 = new Vector2d(-12, -42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-55, -60, Math.toRadians (90));
        Pose2d warehouse_midpoint = new Pose2d(-40, -65, Math.toRadians (0));
        Pose2d warehouse = new Pose2d(40, -65, Math.toRadians (0));

        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, 25, 25,
                        12)
                // Option: Set theme. Default = ColorSchemeRedDark()
                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineTo(location2)//location of the red shipping hub
                                .addTemporalMarker(1000, () -> {}) //servo dump
                                .waitSeconds(1)
                                .lineToLinearHeading(spinner)
                                .addTemporalMarker(5000, () -> {}) // motor spinner wheel
                                .waitSeconds(3.5)
                                .lineToLinearHeading(warehouse_midpoint)
                                .lineToLinearHeading(warehouse)
                                .build());
        */


        //we use gracious profesionalism in all we do, our auto runs graciously and profesionally, our teleop is very grasious too. Our team is an example of what a good gracious professional ftc member should be

        // Set field image

        blue_freight();
    }
    public static void blue_freight()
    {
        MeepMeep meepMeep = new MeepMeep(700);

        Pose2d startPose = new Pose2d(  offset, 60, Math.toRadians(-90));
        Vector2d shippingHub = new Vector2d(-12, 42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-60, 45, Math.toRadians(0));
        Pose2d spinner_shift = new Pose2d(spinner.getX(), 50, Math.toRadians(0));
        //Pose2d spinner_shift = new Pose2d(-55, 55, Math.toRadians (0));
        Vector2d warehouse_midpoint = new Vector2d(-25, 65);
        Pose2d warehouse = new Pose2d(40, warehouse_midpoint.getY(), Math.toRadians(0));
        Vector2d fitSide = new Vector2d(40, warehouse_midpoint.getY()-25);
        Vector2d collect_freight = new Vector2d(60, 65);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(70, 70, 12, 20,
                        8)

                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .waitSeconds(  startDelay)
                                .lineTo(shippingHub)//location of the red shipping hub
                                .waitSeconds(  blue_basic_shipHubDelay +   shipHubDelay)

                                .lineToLinearHeading(spinner)
                                .lineToLinearHeading(spinner_shift)
                                .waitSeconds(  blue_basic_spinDelay +   spinDelay)

                                .splineToConstantHeading(warehouse_midpoint, 0)
                                    .lineToLinearHeading(warehouse)
                                .lineTo(fitSide)
                                //.setVelConstraint(slow)
                                .build());








        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(true)
                // Background opacity from 0-1
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

    public static void blue_gracious()
    {
        Pose2d startPose = new Pose2d(0, -60, Math.toRadians(90));
        Vector2d location2 = new Vector2d(10, -60);
        Vector2d very_gracious = new Vector2d(10, 60);
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, 25, 25,
                        12)
                // Option: Set theme. Default = ColorSchemeRedDark()
                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineTo(location2)//location of the red shipping hub
                                .lineTo(very_gracious)
                                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(true)
                // Background opacity from 0-1
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

}