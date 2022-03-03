package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;

import java.util.List;

//TODO: Uhm, I just thought... we are kinda screwed if we dont see a duck.

@Autonomous (name = "BLUE")//STARTUP TIME, AFTER WE DROP THE BLOCK TIME, AND SPINNER POINT DELAY
public class Blue extends LinearOpMode //spaghetti code incoming sry
{
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

    public Servo Claw;
    public SampleMecanumDrive drive;
    public DcMotor lift;
    public DcMotor Spinner;
    public Servo Dump;
    private ColorSensor color_sensor;
    private ColorSensor color_sensor2;

    TrajectorySequence Red;
    TrajectorySequence Blue;
    Pose2d warehouse_red; // connection point for freight red
    Pose2d warehouse_blue; // connection point for freight blue

    public int duckPose;

    @Override
    public void runOpMode() throws InterruptedException
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        initVuforia();
        initTfod();
        initHardware();
        initBlue_basic();
        initRed_basic();
        initBlue_freight();
        //initRed_freight();  TODO


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
        ElapsedTime time = new ElapsedTime();
        double current_time=time.time(); // in seconds by default
        Dump.setPosition(-1.0);
        Spinner.setPower(-0.8);
        if(duckPose==0) Lift.runToPosition(Constants.liftLow, lift);
        if(duckPose==1) Lift.runToPosition(Constants.liftMid, lift);
        if(duckPose==2) Lift.runToPosition(Constants.liftHigh, lift);

        if(Constants.side == 0) //red
        {
            drive.followTrajectorySequence(Red); //default

            if(Constants.freight == true)
            {
                while(opModeIsActive()) // one cycle of autonomous freight collection
                {
                    int x_value = 0;
                    boolean see_freight = false;
                    boolean enough_time = true;
                    while((!see_freight) && (enough_time))
                    {
                        if(isStopRequested()) return;

                        current_time = time.time();
                        enough_time = ((Constants.autoTimeLimit-current_time)>Constants.run_time);
                        see_freight = (Color_sensor.see_freight(color_sensor) || Color_sensor.see_freight(color_sensor2));

                        Trajectory myTrajectory =
                                drive.trajectoryBuilder(new Pose2d(warehouse_red.getX(), warehouse_red.getY(), warehouse_red.getHeading()))
                                        .lineTo(new Vector2d(warehouse_red.getX()+x_value+1, warehouse_red.getY()))
                                        .build();
                        drive.followTrajectory(myTrajectory);

                        x_value++;
                    }
                    if(!enough_time)
                    {
                        //initRed_return();
                        return;
                    }
                    else if(see_freight)
                    {
                        Pose2d current_position = new Pose2d(warehouse_red.getX(), warehouse_red.getY(), warehouse_red.getHeading());
                        //initRed_freight();
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
        if(Constants.side == 1)// blue
        {
            drive.followTrajectorySequence(Blue);
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
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //drive
        color_sensor  = hardwareMap.colorSensor.get("color");
        color_sensor2 = hardwareMap.colorSensor.get("color1");

        duckPose = 0;
        //duckPose = Webcam.getElementPosition(tfod, vuforia); // TODO check with the positions
        //webcam
    }

    private void initRed_basic()
    {
        Pose2d startPose =          new Pose2d(Constants.offset, -60, Math.toRadians(90));
        Vector2d shippingHub =        new Vector2d(-12, -42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner =            new Pose2d(-55, -60, Math.toRadians (90));
        Pose2d warehouse_midpoint = new Pose2d(-40, -65, Math.toRadians (0));
        warehouse_red =          new Pose2d(40, -65, Math.toRadians (0));

        TrajectorySequence Red = drive.trajectorySequenceBuilder(startPose)
                .waitSeconds(Constants.startDelay)
                .lineTo(shippingHub)//location of the red shipping hub
                .addTemporalMarker(Constants.startDelay, () -> {Claw.setPosition(0.5);}) //servo dump
                .waitSeconds(Constants.red_basic_shipHubDelay+Constants.shipHubDelay)
                // shipping hub dump delay + additional delay if we wish
                .lineToLinearHeading(spinner)
                .waitSeconds(Constants.red_basic_spinDelay+Constants.spinDelay)
                // carousel spinning delay + additional delay if we wish
                .lineToLinearHeading(warehouse_midpoint)
                .lineToLinearHeading(warehouse_red)
                .build();
    }

    private void initRed_freight(Pose2d startPose)
    {
        Vector2d shippingHub =        new Vector2d(-12, -42);
        //Vector2d midpoint = new Vector2d(-30, -42);

        Pose2d warehouse_midpoint = new Pose2d(-40, -65, Math.toRadians (0));
        Pose2d warehouse =          new Pose2d(40, -65, Math.toRadians (0));

        TrajectorySequence Red = drive.trajectorySequenceBuilder(startPose)
                .waitSeconds(Constants.startDelay)
                .lineTo(shippingHub)//location of the red shipping hub
                .addTemporalMarker(Constants.startDelay, () -> {Claw.setPosition(0.5);}) //servo dump
                .waitSeconds(Constants.red_basic_shipHubDelay+Constants.shipHubDelay)
                // shipping hub dump delay + additional delay if we wish
                //.lineToLinearHeading(spinner)
                .waitSeconds(Constants.red_basic_spinDelay+Constants.spinDelay)
                // carousel spinning delay + additional delay if we wish
                .lineToLinearHeading(warehouse_midpoint)
                .lineToLinearHeading(warehouse)
                .build();
    }

    private void initBlue_basic()
    {
        Pose2d startPose = new Pose2d(Constants.offset, 60, Math.toRadians(-90));
        Vector2d shippingHub = new Vector2d(-12, 42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-60, 45, Math.toRadians (0));
        Pose2d spinner_shift = new Pose2d(-60, 50, Math.toRadians (0));
        //Pose2d spinner_shift = new Pose2d(-55, 55, Math.toRadians (0));
        Vector2d warehouse_midpoint = new Vector2d(-25, 65);
        warehouse_blue = new Pose2d(40, 65, Math.toRadians (0));

        drive.setPoseEstimate(startPose);

        TrajectorySequence Blue = drive.trajectorySequenceBuilder(startPose)
                .waitSeconds(Constants.startDelay)
                .lineTo(shippingHub)//location of the red shipping hub
                .addTemporalMarker(Constants.startDelay, () -> {Claw.setPosition(0.5);}) //servo dump
                .waitSeconds(Constants.blue_basic_shipHubDelay+Constants.shipHubDelay)
                // shipping hub dump delay + additional delay if we wish
                .lineToLinearHeading(spinner)
                .lineToLinearHeading(spinner_shift)
                .waitSeconds(Constants.blue_basic_spinDelay+Constants.spinDelay)
                // carousel spinning delay + additional delay if we wish
                .splineToConstantHeading(warehouse_midpoint, 0)
                //.lineToLinearHeading(warehouse)
                .build();
    }

    private void initBlue_freight()
    {

        Pose2d startPose = new Pose2d(Constants.offset, 60, Math.toRadians(-90));
        Vector2d shippingHub = new Vector2d(-12, 42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner = new Pose2d(-60, 45, Math.toRadians (0));
        Pose2d spinner_shift = new Pose2d(-60, 50, Math.toRadians (0));
        //Pose2d spinner_shift = new Pose2d(-55, 55, Math.toRadians (0));
        Vector2d warehouse_midpoint = new Vector2d(-25, 65);
        Pose2d warehouse = new Pose2d(40, 65, Math.toRadians (0));

        drive.setPoseEstimate(startPose);

        TrajectorySequence Blue = drive.trajectorySequenceBuilder(startPose)
                .waitSeconds(Constants.startDelay)
                .lineTo(shippingHub)//location of the red shipping hub
                .addTemporalMarker(Constants.startDelay, () -> {Claw.setPosition(0.5);}) //servo dump
                .waitSeconds(Constants.blue_basic_shipHubDelay+Constants.shipHubDelay)
                // shipping hub dump delay + additional delay if we wish
                .lineToLinearHeading(spinner)
                .lineToLinearHeading(spinner_shift)
                .waitSeconds(Constants.blue_basic_spinDelay+Constants.spinDelay)
                // carousel spinning delay + additional delay if we wish
                .splineToConstantHeading(warehouse_midpoint, 0)
                .lineToLinearHeading(warehouse)
                .build();
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
