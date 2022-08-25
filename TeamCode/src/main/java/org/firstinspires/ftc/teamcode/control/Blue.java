package org.firstinspires.ftc.teamcode.control;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

//TODO: Uhm, I just thought... we are kinda screwed if we dont see a duck.

@Autonomous (name = "AUTO BLUE")//STARTUP TIME, AFTER WE DROP THE BLOCK TIME, AND SPINNER POINT DELAY
public class Blue extends LinearOpMode
{
    SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

    TrajectorySequence Blue;
    // set current position

    @Override
    public void runOpMode() throws InterruptedException
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        Robot.initRobot();

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();


        if(isStopRequested()) return;
        waitForStart();

        Run();
    }

    private void Run()
    {
        if(Storage.side == 1) //blue
        {
            drive.followTrajectorySequence(Blue); //OPERATION: blue goes to collect the preset freight, spin, and go to the warehouse
            //drive.followTrajectorySequence(Blue_barrier); //OPERATION: blue goes to collect the preset freight, spin, and go to the warehouse
            if(Storage.freight == true) //if we are going to be running the freight part
            {
                while(opModeIsActive()) // one cycle of autonomous freight collection
                {
                    MotorPidControl.runToPosition(Storage.liftIntake, lift); //OPERATION: lift goes to the intake position when it's in the warehouse
                    int x_value = 0;
                    boolean see_freight = false;
                    boolean enough_time = true;
                    while((!see_freight) && (enough_time))
                    {
                        if(isStopRequested()) return;

                        current_time = Time.time(); //OPERATION: get current time
                        enough_time = ((Storage.autoTimeLimit-current_time)> Storage.run_time_blue); //OPERATION: if we have enough time
                        see_freight = (Color_sensor.see_freight(color_sensor) || Color_sensor.see_freight(color_sensor2)); //if either one sees freight

                        Trajectory myTrajectory =
                                drive.trajectoryBuilder(new Pose2d(warehouse_blue.getX()+x_value, warehouse_blue.getY(), warehouse_blue.getHeading()))
                                        .lineTo(new Vector2d(warehouse_blue.getX()+x_value+1, warehouse_blue.getY())) //move one inch closer
                                        .build();
                        drive.followTrajectory(myTrajectory); //complete an inch forward movemet

                        x_value++; //and save position
                    }

                    Pose2d current_position = new Pose2d(warehouse_blue.getX()+x_value, warehouse_blue.getY(), warehouse_blue.getHeading());

                    if(!enough_time)
                    {
                        initBlue_return(current_position);
                        drive.followTrajectorySequence(Blue_return);
                        MotorPidControl.runToPosition(Storage.liftIntake, lift);
                        return;
                    }
                    else if(see_freight)
                    {
                        Claw.setPosition(0); // open claw
                        MotorPidControl.runToPosition(Storage.liftHigh, lift); // move to the highest position
                        Dump.setPosition(Storage.dumpHigh); // move to the high position
                        initBlue_freight(current_position); // move to the shipping hub and back
                        drive.followTrajectorySequence(Blue_freight);
                    }
                }

            }
            else
            {
                Trajectory fit_side = drive.trajectoryBuilder(warehouse_blue) //TODO here
                        .strafeLeft(40)
                        .build();
                drive.followTrajectory(fit_side);
            }
        }
    }

    private void initHardware()
    {
        drive = new SampleMecanumDrive(hardwareMap);
        lift = hardwareMap.get(DcMotorEx.class, "Lift");
        Spinner = hardwareMap.get(DcMotor.class, "Spinner");
        Claw = hardwareMap.get(Servo.class, "Claw");
        AndroidSoundPool androidSoundPool = new AndroidSoundPool();
        Claw.setPosition(1);
        Dump = hardwareMap.get(Servo.class, "Dump");
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        color_sensor2 = hardwareMap.colorSensor.get("color2");

        duckPose = 0;
        //duckPose = Webcam.getElementPosition(tfod, vuforia); // TODO check with the positions
        //webcam
    }
}






//////////////////////////////////////////////////////////////////








//TODO: add the abbility to run auto during teleop (break out of auto)
//TODO: !!! add the abbillity to change the settings in telemetry

//TODO: add gracious
// professionalism with png class and add a reset to go back to the position once you're done.
// An auto that goes to block, then proceeds with its usual schedule !!!


//add a marker and delay to put the arm up? TODO: not sure if we need to lift the arm up at all actually



//////////////////////////////////////////////////////////////////////
