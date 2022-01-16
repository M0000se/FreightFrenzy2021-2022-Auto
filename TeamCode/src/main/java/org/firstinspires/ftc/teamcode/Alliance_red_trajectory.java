package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

public class Alliance_red_trajectory extends LinearOpMode //stichcode incoming
{
    @Override

    public void runOpMode()
    {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        DcMotor Spinner = hardwareMap.dcMotor.get("Spinner");
        Servo servo = hardwareMap.servo.get("Dump");
        Pose2d startPose = new Pose2d(0, -60, Math.toRadians(90));
        Vector2d location2 = new Vector2d(-12, -42);
        Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d location3 = new Pose2d(-45, -55, Math.toRadians (0));
        Vector2d storage = new Vector2d(56, -64);

        drive.setPoseEstimate(startPose);

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .lineTo(location2)//location of the red shipping hub
                .addTemporalMarker(3, () -> {servo.setPosition(1.0);}) //servo dump
                .waitSeconds(1)
                .lineToLinearHeading(location3)
                .addTemporalMarker(5, () -> {Spinner.setPower(1.0);}) // motor spinner wheel
                .waitSeconds(6)
                .splineTo((storage), Math.toRadians(90))
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(trajSeq);
    }
}