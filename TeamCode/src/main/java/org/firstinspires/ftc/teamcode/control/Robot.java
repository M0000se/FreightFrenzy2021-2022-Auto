package org.firstinspires.ftc.teamcode.control;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
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

//================================================================================
// A superclass for all robot subsystems, regardless of auto or teleop
// Define all shared methods/ variables for robot
// (Basically all the initialization for both auto and teleop is done here)
//================================================================================

public class Robot extends LinearOpMode
{
    //visual navigation
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };
    private static final String VUFORIA_KEY =
            "AQ6C1J//////AAABmbFbgnFY8EZ5qg3cWA0ah41DbnifisxJLGcs/rleVs6vR426D48HVqkbcQcAoS2psojauMyRXL6EokX3ArzBtz0MZhNocumRhg5E0AUc8uZL8NAmpq/DwfWrK0tbffRw9VxAcOUVErt01llobKRzcR0vWAfurZ82ZH7a1MVM+ZApi3lxOoJYOEFzbt0JQufS6dYQm31m6/BVfQ63wL+aU3El7rURTxW/2qvSZt6kROmCnaZmNPSdfXGPy8j2xcyKL0vb0pjr8P9FgpktgXYmU5lpGX/lcD40JiLXQGNLD5k2inZtjVYzyvBtPXZ3Z1fqnh5Mp3jcydAa8DofLGHZs2UuC7fkAAAco11XG+w3v9K5";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    //motors and movement
    public Servo Claw;
    public SampleMecanumDrive drive;
    public DcMotor lift;
    public DcMotor Spinner;
    public Servo Dump;

    // color sensor
    private ColorSensor color_sensor;
    private ColorSensor color_sensor2;
    public static double color_error = 90; // max allowed color sensor rgb error

    public int duckPose;

    public void init_robot()
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        initVuforia();
        initTfod();
        initHardware();
        initBlue_basic();
        initRed_basic();

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        sleep(1500);

        //telemetry.addData("!!! Duc pose = ", duckPose);
        //telemetry.update();



        /////////////////////////// AUTO ////////////////////////////

        //set lift according to the duck location


        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        if(isStopRequested()) return;
        waitForStart();

        Run();
    }

    private void Run()
    {

        if(Storage.side == 0) //red
        {
            drive.followTrajectorySequence(Red); //OPERATION: red goes to collect the preset freight, spin, and go to the warehouse

            if(Storage.freight == true) //if we are going to be running the freight part
            {
                while(opModeIsActive()) // one cycle of autonomous freight collection
                {
                    Lift.runToPosition(Storage.liftIntake, lift); //OPERATION: lift goes to the intake position when it's in the warehouse
                    int x_value = 0;
                    boolean see_freight = false;
                    boolean enough_time = true;
                    while((!see_freight) && (enough_time))
                    {
                        if(isStopRequested()) return;

                        current_time = Time.time(); //OPERATION: get current time
                        enough_time = ((Storage.autoTimeLimit-current_time)> Storage.run_time_red); //OPERATION: if we have enough time
                        see_freight = (Color_sensor.see_freight(color_sensor) || Color_sensor.see_freight(color_sensor2)); //if either one sees freight

                        Trajectory myTrajectory =
                                drive.trajectoryBuilder(new Pose2d(warehouse_red.getX()+x_value, warehouse_red.getY(), warehouse_red.getHeading()))
                                        .lineTo(new Vector2d(warehouse_red.getX()+x_value+1, warehouse_red.getY())) //move one inch closer
                                        .build();
                        drive.followTrajectory(myTrajectory); //complete an inch forward movemet

                        x_value++; //and save position
                    }

                    Pose2d current_position = new Pose2d(warehouse_red.getX()+x_value, warehouse_red.getY(), warehouse_red.getHeading());

                    if(!enough_time)
                    {
                        initRed_return(current_position);
                        drive.followTrajectorySequence(Red_return);
                        Lift.runToPosition(Storage.liftIntake, lift);
                        return;
                    }
                    else if(see_freight)
                    {
                        Claw.setPosition(0); // open claw
                        Lift.runToPosition(Storage.liftHigh, lift); // move to the highest position
                        Dump.setPosition(Storage.dumpHigh); // move to the high position
                        initRed_freight(current_position); // move to the shipping hub and back
                        drive.followTrajectorySequence(Red_freight);
                    }
                }

            }
            else
            {
                Trajectory fit_side = drive.trajectoryBuilder(warehouse_red) //TODO here
                        .strafeLeft(40)
                        .build();
                drive.followTrajectory(fit_side);
            }
        }




        if(Storage.side == 1) //blue
        {
            drive.followTrajectorySequence(Blue); //OPERATION: blue goes to collect the preset freight, spin, and go to the warehouse
            //drive.followTrajectorySequence(Blue_barrier); //OPERATION: blue goes to collect the preset freight, spin, and go to the warehouse
            if(Storage.freight == true) //if we are going to be running the freight part
            {
                while(opModeIsActive()) // one cycle of autonomous freight collection
                {
                    Lift.runToPosition(Storage.liftIntake, lift); //OPERATION: lift goes to the intake position when it's in the warehouse
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
                        Lift.runToPosition(Storage.liftIntake, lift);
                        return;
                    }
                    else if(see_freight)
                    {
                        Claw.setPosition(0); // open claw
                        Lift.runToPosition(Storage.liftHigh, lift); // move to the highest position
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
        //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //todo: god fucking damnit disable it
        color_sensor  = hardwareMap.colorSensor.get("color1");
        color_sensor2 = hardwareMap.colorSensor.get("color2");

        duckPose = 0;
        //duckPose = Webcam.getElementPosition(tfod, vuforia); // TODO check with the positions
        //webcam
    }


    private void initVuforia()
    {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod()
    {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.2f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
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
