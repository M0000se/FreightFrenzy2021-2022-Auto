package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.drive.Robot;


@TeleOp(name = "Reset Encoders")//STARTUP TIME, AFTER WE DROP THE BLOCK TIME, AND SPINNER POINT DELAY
public class ResetEncoders extends LinearOpMode //spaghetti code incoming sry
{
    @Override
    public void runOpMode()
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        /*initVuforia();
        initTfod();*/

        int duckPose = 0;
        //duckPose = Webcam.getElementPosition(tfod, vuforia); // TODO check with the positions

        //telemetry.addData("!!! Duc pose = ", duckPose);
        //telemetry.update();

        Robot drive = new Robot(hardwareMap);

        //lift.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                telemetry.addData("encoder", lift.getCurrentPosition());
                telemetry.update();
                if (gamepad1.dpad_down) lift.setPower(-1);
                else if (gamepad1.dpad_up) lift.setPower(1);
                else motor.setPower(0);
                if (gamepad1.a) {
                    //Dump.setPosition(0.5);
                    drive.motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    telemetry.addLine("encoders reset");
                    telemetry.update();

                    sleep(1000);
                    return;
                }
            }
        }


        if(isStopRequested()) return;
    }