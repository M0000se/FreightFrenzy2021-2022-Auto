package org.firstinspires.ftc.teamcode.drive;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.subsystems.DistanceSensor;

//For keeping all and everything connected to the robot in one place
public class RobotHardwareMap
{
    public DcMotorEx leftFront, leftRear, rightRear, rightFront;
    public Servo servoX, servoY;
    public DistanceSensor navDistanceSensor;
    //public static ColorSensor ColorSensor;
    //TODO: add distance sensor
    //TODO: consider making them static

    final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };
    final String VUFORIA_KEY =
            "AQ6C1J//////AAABmbFbgnFY8EZ5qg3cWA0ah41DbnifisxJLGcs/rleVs6vR426D48HVqkbcQcAoS2psojauMyRXL6EokX3ArzBtz0MZhNocumRhg5E0AUc8uZL8NAmpq/DwfWrK0tbffRw9VxAcOUVErt01llobKRzcR0vWAfurZ82ZH7a1MVM+ZApi3lxOoJYOEFzbt0JQufS6dYQm31m6/BVfQ63wL+aU3El7rURTxW/2qvSZt6kROmCnaZmNPSdfXGPy8j2xcyKL0vb0pjr8P9FgpktgXYmU5lpGX/lcD40JiLXQGNLD5k2inZtjVYzyvBtPXZ3Z1fqnh5Mp3jcydAa8DofLGHZs2UuC7fkAAAco11XG+w3v9K5";
    public static VuforiaLocalizer vuforia;
    public static TFObjectDetector tfod;

    // set to static to not have to reintialize it between auto and teleop

    //TODO: consider not making those static

    public RobotHardwareMap()
    {
        leftFront = hardwareMap.get(DcMotorEx.class,"leftFront");
        leftRear = hardwareMap.get(DcMotorEx.class,"leftRear");
        rightFront = hardwareMap.get(DcMotorEx.class,"rightFront");
        rightRear = hardwareMap.get(DcMotorEx.class,"rightRear");

        servoX = hardwareMap.get(Servo.class,"servoX");
        servoY = hardwareMap.get(Servo.class,"servoY");

        navDistanceSensor = hardwareMap.get(DistanceSensor.class, "navDistanceSensor");
    }

    public void initVision() // init vision separately since it takes a longer time.
    {

        //================================================================================
        // INITIALIZING CAMERAS
        //================================================================================
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters vuforiaParameters = new VuforiaLocalizer.Parameters();

        vuforiaParameters.vuforiaLicenseKey = VUFORIA_KEY;
        vuforiaParameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(vuforiaParameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.

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
