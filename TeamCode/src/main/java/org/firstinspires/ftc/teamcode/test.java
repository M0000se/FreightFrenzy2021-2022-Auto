package org.firstinspires.ftc.teamcode;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.ColorSensor;


import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;

@TeleOp(name = "test")

public class test extends LinearOpMode
{

    private DcMotor lift;
    private DcMotor RightFront;
    private DcMotor RightRear;
    private DcMotor LeftRear;
    private DcMotor LeftFront;
    private DcMotor Spinner;
    private Servo Claw;
    private AndroidSoundPool androidSoundPool;
    private Servo Dump;
    private RevBlinkinLedDriver led;
    private ColorSensor color_sensor;
    private ColorSensor color_sensor2;

    private boolean backOpen = true;
    private boolean b_open = true;
    private boolean completed = false;
    private boolean activate = false;
    private boolean init = false;
    private int override = 0; // 0 for no override, 1 for lift override, 2 for total override
    private boolean direction = false;
    private boolean right_bumperOpen;

    private double currentTime;
    private double dump_position;
    private double claw_position=0;
    private double lift_power=0;    // for manual operation
    private int    lift_position=0; // for autonomous operation
    private double spinner_power=0;
    private boolean spinner_direction = false;
    private int spinner_toggle=0;
    private int spinner_direction_toggle=0;
    private double spinner_time_start = 0;
    private double delay = 0;
    private double mult=0.5;
    private int led_pattern = 0;

    private double Vertical;
    private double Horizontal;
    private double Pivot;

    //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


    @Override
    public void runOpMode()
    {
        waitForStart();

        b_open = true;
        right_bumperOpen = true;
        completed = false;
        activate = false;
        init = false;
        override = 1; // 0 for no override, 1 for lift override, 2 for total override
        direction = false;

        dump_position=0;
        claw_position=0;
        lift_power=0;    // for manual operation
        lift_position=0; // for autonomous operation
        spinner_power=0;
        spinner_direction = false;
        delay = 0;
        mult=0.5;

        lift = hardwareMap.get(DcMotorEx.class, "Lift");
        RightFront = hardwareMap.get(DcMotor.class, "RightFront");
        RightRear = hardwareMap.get(DcMotor.class, "RightRear");
        LeftRear = hardwareMap.get(DcMotor.class, "LeftRear");
        LeftFront = hardwareMap.get(DcMotor.class, "LeftFront");
        Spinner = hardwareMap.get(DcMotor.class, "Spinner");
        Claw = hardwareMap.get(Servo.class, "Claw");
        androidSoundPool = new AndroidSoundPool();
        Dump = hardwareMap.get(Servo.class, "Dump");

        led = hardwareMap.get(RevBlinkinLedDriver.class, "led");

        color_sensor = hardwareMap.colorSensor.get("color1");
        color_sensor2 = hardwareMap.colorSensor.get("color2");

        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        RightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        RightRear.setDirection(DcMotorSimple.Direction.REVERSE);
        LeftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift_position=Constants.liftHigh; //set at the highest
        //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Claw.setDirection(Servo.Direction.REVERSE);
        androidSoundPool.initialize(SoundPlayer.getInstance());
        androidSoundPool.play("RawRes:ss_r2d2_up");

        if (opModeIsActive())
        {

        }
    }
}