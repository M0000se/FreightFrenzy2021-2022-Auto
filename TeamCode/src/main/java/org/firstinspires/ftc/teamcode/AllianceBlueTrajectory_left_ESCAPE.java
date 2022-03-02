package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.List;

@Autonomous (name = "AUTO BLUE ESCAPE")
public class AllianceBlueTrajectory_left_ESCAPE extends LinearOpMode //spaghetti code incoming sry
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



    static int x_center=200; //fiddle with it
    static int center_accuracy = 70;
    private int TE_pose; // 0 for left, 1 for middle, 2 for right

    //------------------------------------------------------------------------------------

    int getElementPosition ()
    {
        initVuforia();
        initTfod();


        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1, 16.0 / 9.0);
        }

        /** Wait for the game to begin */

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);

        for (; ; )
        {
            if (tfod != null)
            {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    boolean isDuckDetected = false;     //  ** ADDED **
                    for (Recognition recognition : updatedRecognitions) {
                        i++;
                        // check label to see if the camera now sees a Duck
                        if (recognition.getLabel().equals("Duck")) {            //  ** ADDED **
                            isDuckDetected = true;
                            telemetry.addData("Object Detected", "Looks like a duck to me");      //  ** ADDED **
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());

                            if (recognition.getLeft() > x_center+center_accuracy) return 2;
                            else if (recognition.getLeft() < x_center-center_accuracy) return 0;
                            else return 1; //TODO: test*/

                        } else isDuckDetected = false;

                    }
                    telemetry.update();
                }
            }
        }
    }
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
    public void runOpMode() throws InterruptedException {
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        waitForStart();

        //int duckpose = getElementPosition();

        int duckpose = 2;
        //---------------------------------HERE-----------------------------//
        double escape_delay = 3.0;

        //telemetry.addData("!!! Duc pose = ", duckpose);
        //telemetry.update();

        //DRIVE
        DcMotor lift;
        DcMotor RightFront;
        DcMotor RightRear;
        DcMotor LeftRear;
        DcMotor LeftFront;
        DcMotor Spinner;
        Servo Claw;
        AndroidSoundPool androidSoundPool;
        Servo Dump;


        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        lift = hardwareMap.get(DcMotor.class, "Lift");
        Spinner = hardwareMap.get(DcMotor.class, "Spinner");
        Claw = hardwareMap.get(Servo.class, "Claw");
        androidSoundPool = new AndroidSoundPool();
        Claw.setPosition(1);
        Dump = hardwareMap.get(Servo.class, "Dump");
        lift.setDirection(DcMotorSimple.Direction.FORWARD);

        int offset = 0; //TODO 0 for left, change for right

        Pose2d startPose = new Pose2d((-33+offset), 60, Math.toRadians(90));
        Pose2d ESCAPE = new Pose2d(-50, 0, Math.toRadians(90));
        Pose2d ESCAPE_BACK = new Pose2d(-33+offset, 60, Math.toRadians(90));
        Pose2d location2 = new Pose2d(7, 22, Math.toRadians(90));
        Pose2d location3 = new Pose2d(-12, 42, Math.toRadians (0));
        Vector2d location4 = new Vector2d(-85, 42);
        Vector2d duckMidpoint = new Vector2d(-85, 48);
        Pose2d storageMidpoint = new Pose2d(0, 100, Math.toRadians(180));
        Vector2d storage = new Vector2d(120, 130);

        lift.setPower(1);
        sleep(500);
        lift.setPower(0);


        //make a marker for where to put red



        drive.setPoseEstimate(startPose);
        //TODO: based on duck position move accordingly



        /*if(duckpose==1)
        {
            location2= new Pose2d(7, 22, Math.toRadians(90));
        }

        if(duckpose==0)
        {
            lift.setPower(-1);
            sleep(500);
            lift.setPower(0);
            location2= new Pose2d(7, 22, Math.toRadians(90));
        }*/

        Dump.setPosition(-1.0);
        Spinner.setPower(-0.8);

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(ESCAPE)
                .lineToLinearHeading(ESCAPE_BACK)
                .waitSeconds(escape_delay)
                .lineToLinearHeading(location2)//location of the red shipping hub
                .addSpatialMarker(new Vector2d(7, 22), () -> { //coordinates of the LOCATION 2
                    Claw.setPosition(0.5);
                }) //servo dump
                .waitSeconds(0.3)
                .lineToLinearHeading(location3)
                .lineToConstantHeading(location4)
                .lineToConstantHeading(duckMidpoint)
                .waitSeconds(2)
                .splineToSplineHeading(storageMidpoint, Math.toRadians(-50))
                .lineToConstantHeading((storage))
                .build();




        if(isStopRequested()) return;

        drive.followTrajectorySequence(trajSeq);
    }


    /**
     * Initialize the Vuforia localization engine.
     */
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
    private void initTfod() {
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