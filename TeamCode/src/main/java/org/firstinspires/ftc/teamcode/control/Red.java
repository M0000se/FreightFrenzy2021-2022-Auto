package org.firstinspires.ftc.teamcode.control;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Webcam;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

//TODO: Uhm, I just thought... we are kinda done if we dont see a duck.

@Disabled
@Autonomous (name = "RED RIGHT")//STARTUP TIME, AFTER WE DROP THE BLOCK TIME, AND SPINNER POINT DELAY
public class Red extends LinearOpMode //spaghetti code incoming sry
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




    //:TODO take the lowest reductions with the motor in the clean tray. use the arm for
    // intake but put a button sensor so that it
    // would slowly move around the freight and if it collects one it will proceed.

    /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
     * the following 4 detectable objects
     *  0: Ball,
     *  1: Cube,
     *  2: Duck,
     *  3: Marker (duck location tape marker)
     *
     *  Two additional model assets are available which only contain a subset of the objects:
     *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
     *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
     */


    @Override
    public void runOpMode() throws InterruptedException
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        initVuforia();
        initTfod();

        int duckPose = 0;
        duckPose = Webcam.getElementPosition(tfod, vuforia); // TODO check with the positions

        //telemetry.addData("!!! Duc pose = ", duckPose);
        //telemetry.update();

        //DRIVE
        DcMotor Lift;
        DcMotor RightFront;
        DcMotor RightRear;
        DcMotor LeftRear;
        DcMotor LeftFront;
        DcMotor Spinner;
        Servo Claw;
        AndroidSoundPool androidSoundPool;
        Servo Dump;


        Robot drive = new Robot(hardwareMap);
        Lift = hardwareMap.get(DcMotor.class, "Lift");
        Spinner = hardwareMap.get(DcMotor.class, "Spinner");
        Claw = hardwareMap.get(Servo.class, "Claw");
        androidSoundPool = new AndroidSoundPool();
        Claw.setPosition(1);
        Dump = hardwareMap.get(Servo.class, "Dump");
        Lift.setDirection(DcMotorSimple.Direction.FORWARD);

        Pose2d startPose =          new Pose2d(Storage.offset, -60, Math.toRadians(90));
        Vector2d shippingHub =        new Vector2d(-12, -42);
        //Vector2d midpoint = new Vector2d(-30, -42);
        Pose2d spinner =            new Pose2d(-55, -60, Math.toRadians (90));
        Pose2d warehouse_midpoint = new Pose2d(-40, -65, Math.toRadians (0));
        Pose2d warehouse =          new Pose2d(40, -65, Math.toRadians (0));


        drive.setPoseEstimate(startPose);

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        ///////////////////////////////// AUTO ///////////////////////////////////////

        //set lift according to the duck location
        if(duckPose==0) Lift.setTargetPosition(Storage.liftLow);
        if(duckPose==1) Lift.setTargetPosition(Storage.liftMid);
        if(duckPose==2) Lift.setTargetPosition(Storage.liftHigh);

        Dump.setPosition(-1.0);
        Spinner.setPower(-0.8);

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .waitSeconds(Storage.startDelay)
                .lineTo(shippingHub)//location of the red shipping hub
                .addTemporalMarker(Storage.startDelay, () -> {Claw.setPosition(0.5);}) //servo dump
                .waitSeconds(Storage.red_basic_shipHubDelay+ Storage.shipHubDelay)
                // shipping hub dump delay + additional delay if we wish
                .lineToLinearHeading(spinner)
                .waitSeconds(Storage.red_basic_spinDelay+ Storage.spinDelay)
                // carousel spinning delay + additional delay if we wish
                .lineToLinearHeading(warehouse_midpoint)
                .lineToLinearHeading(warehouse)
                .build();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(trajSeq);
    }

    private void initVuforia() {
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



