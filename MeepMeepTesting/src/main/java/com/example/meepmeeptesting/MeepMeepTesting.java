package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        // Declare a MeepMeep instance
        // With a field size of 800 pixels
        int offset=0;
        Pose2d startPose = new Pose2d((-33+offset), 60, Math.toRadians(90)); //TODO say please tighten everything on the robot someday
        Pose2d location2 = new Pose2d(7, 22, Math.toRadians(90));
        //Vector2d midpoint = new Vector2d(-30, 42);
        Pose2d location3 = new Pose2d(-12, 51, Math.toRadians (180));
        Vector2d location4 = new Vector2d(-90, 55);
        Vector2d duckMidpoint = new Vector2d(-90, 65);
        Pose2d storageMidpoint = new Pose2d(0, 80, Math.toRadians(0));
        Vector2d storage = new Vector2d(105, 80);

        /*Pose2d startPose = new Pose2d((-33+offset), -60, Math.toRadians(270));
        Pose2d location2 = new Pose2d(7, -22, Math.toRadians(270));
        Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d location3 = new Pose2d(-12, -51, Math.toRadians (180));
        Vector2d location4 = new Vector2d(-90, -55);
        Vector2d duckMidpoint = new Vector2d(-90, -65);
        Pose2d storageMidpoint = new Pose2d(0, -80, Math.toRadians(0));
        Vector2d storage = new Vector2d(105, -80);*/

        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, 25, 25,
                        12)
                // Option: Set theme. Default = ColorSchemeRedDark()
                .setColorScheme(new ColorSchemeRedDark())

                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineToLinearHeading(location2)//location of the red shipping hub
                                .addTemporalMarker(3, () -> {}) //servo dump
                                .waitSeconds(3)
                                .lineToLinearHeading(location3)
                                .lineToConstantHeading(location4)
                                .lineToConstantHeading(duckMidpoint)
                                .addTemporalMarker(5, () -> {}) // motor spinner wheel
                                .waitSeconds(5)
                                .splineToSplineHeading(storageMidpoint, Math.toRadians(-50))
                                .lineToConstantHeading((storage))
                                .build());


        // Set field image
        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(true)
                // Background opacity from 0-1
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}