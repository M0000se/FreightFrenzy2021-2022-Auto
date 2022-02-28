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

        Pose2d startPose = new Pose2d(0, 60, Math.toRadians(-90));
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
                                .lineToLinearHeading(spinner_shift)
                                .addTemporalMarker(5000, () -> {}) // motor spinner wheel
                                .waitSeconds(3.5)
                                .splineToConstantHeading(warehouse_midpoint, 0)
                                .lineToLinearHeading(warehouse)
                                .build());


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
        Pose2d spinner = new Pose2d(-45, -60, Math.toRadians (90));
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


        // Set field image
        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(true)
                // Background opacity from 0-1
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}