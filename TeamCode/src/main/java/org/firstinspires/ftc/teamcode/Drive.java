package org.firstinspires.ftc.teamcode;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import java.util.Locale;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;

@TeleOp(name = "DRIVE")
public class Drive extends LinearOpMode
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

    ElapsedTime time;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode()
    {
        b_open = true;
        right_bumperOpen = true;
        completed = false;
        activate = false;
        init = false;
        override = 0; // 0 for no override, 1 for lift override, 2 for total override
        direction = false;

        time = new ElapsedTime();

        currentTime = time.time();
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

        color_sensor = hardwareMap.colorSensor.get("color");

        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        RightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        RightRear.setDirection(DcMotorSimple.Direction.REVERSE);
        LeftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift_position=Constants.liftIntake; //set at the highest
        //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Claw.setDirection(Servo.Direction.REVERSE);
        androidSoundPool.initialize(SoundPlayer.getInstance());
        androidSoundPool.play("RawRes:ss_r2d2_up");


        waitForStart();


        if (opModeIsActive())
        {
            while (opModeIsActive())
            {
                // Put loop blocks here.
                // Updates the value from the sticks
                Vertical = -(gamepad1.right_stick_y * gamepad1.right_stick_y * gamepad1.right_stick_y * 1 * mult);
                Horizontal = gamepad1.right_stick_x * gamepad1.right_stick_x * gamepad1.right_stick_x * 1 * mult;
                Pivot = gamepad1.left_stick_x * gamepad1.left_stick_x * gamepad1.left_stick_x * 0.6*mult;

                led_pattern = 0;
                if(gamepad1.left_trigger >= 0.1)
                {
                    mult = 1;// regulatable% speed beyond 0.5
                    //TODO set blinkin Rainbow
                    led_pattern = 1;
                }
                else mult = 0.5;

                // switch modes

                //Override regulation

                if((gamepad2.b || gamepad1.b) && b_open)// switch between manual and auto control
                {
                    if(override==1)
                    {
                        override = 0;
                        telemetry.speak("Manual control");
                    }
                    else if(override==0)
                    {
                        override = 1;
                        telemetry.speak("Auto control");
                    }
                    //androidSoundPool.play("RawRes:ss_alarm");

                    b_open=false;
                }
                if(!(gamepad2.b || gamepad1.b)) b_open=true;

                if(override == 0) ManualSpecific();         // all manual-specific lift and dump
                else if(override == 1) AutoSpecific();      // all auto-specific lift and dump functions
                else if(override == 2) TotalAutoSpecific(); // all auto-specific lift, dump and claw functions TODO fix doesnt work well


                // crude implementation of automatic breaking spinner
                if(gamepad1.left_bumper)
                {
                    spinner_power=1;
                    spinner_direction_toggle=1;
                }
                else
                {
                    //telemetry.addData("spin dir tog!!!!", spinner_direction_toggle);
                    if(spinner_direction_toggle==1)
                    {
                        spinner_time_start=time.time();
                        spinner_power=-0.1;
                        spinner_direction_toggle=0;
                    }
                    if(time.time()-spinner_time_start>0.1 && spinner_direction_toggle==0)
                    {
                        spinner_power=0;
                    }


                    /////////////////////////////////////////////////////----
                }

                if((gamepad1.back||gamepad2.back) && backOpen) // spinner direction switch
                {
                    if(spinner_toggle==0) spinner_direction=false;
                    if(spinner_toggle==1) spinner_direction=true;
                    spinner_toggle++;
                    if(spinner_toggle==2) spinner_toggle=0;
                    backOpen = false;
                }
                if(!(gamepad2.back || gamepad1.b)) backOpen=true;

                if(spinner_direction==true) Spinner.setDirection(DcMotorSimple.Direction.FORWARD);
                if(spinner_direction==false)Spinner.setDirection(DcMotorSimple.Direction.REVERSE);





                    // Sets motor powers to stick levels
            /*RightFront.setPower(-Pivot + (Vertical - Horizontal));
            RightRear.setPower(-Pivot + Vertical + Horizontal);
            LeftFront.setPower(Pivot + Vertical + Horizontal);
            LeftRear.setPower(Pivot + (Vertical - Horizontal));*/

            /*if(left controller y = 1) go forward with the power of left controller y
            if(left controller y = 0) go back with the power of the left controller x.
            if()*/

                telemetry.addData("Vertical", Vertical);
                telemetry.addData("Horizontal", Horizontal);
                telemetry.addData("Pivot", Pivot);

                RightFront.setPower(-Pivot + (Vertical - Horizontal));
                RightRear.setPower(-Pivot + Vertical + Horizontal);
                LeftFront.setPower(Pivot + Vertical + Horizontal);
                LeftRear.setPower(Pivot + (Vertical - Horizontal));

                Dump.setPosition(dump_position);
                Claw.setPosition(claw_position);
                Spinner.setPower(spinner_power);
                if(led_pattern==0) led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
                if(led_pattern==1) led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
                if(led_pattern==2) led.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_RED);

                /*if(gamepad1.left_bumper || gamepad2.left_bumper) Spinner.setDirection(DcMotorSimple.Direction.REVERSE);
                else                   Spinner.setDirection(DcMotorSimple.Direction.FORWARD);*/


                if(override==0) telemetry.addLine("Override: OFF");
                else telemetry.addData("Override:", override);
                //telemetry.addData("override: False", override);
                telemetry.addData("TIME:", time.time());
                telemetry.update();
            }
        }
        androidSoundPool.close();
    }

    private void ManualSpecific() //Manual lift and dump control
    {
        if(gamepad2.dpad_down || gamepad1.dpad_down) lift_power=-1;
        else if(gamepad2.dpad_up || gamepad1.dpad_up) lift_power = 1;
        else lift_power = 0;

        //lift

        if (gamepad2.right_bumper || gamepad1.right_bumper) dump_position = Constants.dumpStraight;
        else dump_position = Constants.dumpHigh;

        //if (gamepad2.start || gamepad1.start) lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // reset encoder

        //dump


        claw_position = Math.max(gamepad2.right_trigger, gamepad1.right_trigger);
        // Operate claw with right trigger

        lift.setPower(lift_power);
    }

    public void AutoSpecific()
    {
        claw_position = Math.max(gamepad2.right_trigger, gamepad1.right_trigger);
        // Operate claw with right trigger

         // TODO complete if you have time*/

        if ( (gamepad2.right_bumper || gamepad1.right_bumper) && (lift_position == Constants.liftHigh)
                && (right_bumperOpen==true))
        {
            lift_position = Constants.liftIntake;// automatically go to the intake position(arm)
            right_bumperOpen = false;
        }
        if ( (gamepad2.right_bumper || gamepad1.right_bumper) && (lift_position == Constants.liftIntake)
                && (right_bumperOpen==true))
        {
            lift_position = Constants.liftHigh;// automatically go to the highest ship hub position(arm)
            right_bumperOpen = false;
        }
        if(!(gamepad1.right_bumper || gamepad2.right_bumper)) right_bumperOpen = true;// TODO: my life...

        if (gamepad2.x || gamepad1.x) lift_position = Constants.liftMid;// automatically go to the middle ship hub position(arm)
        if (gamepad2.a || gamepad1.a) lift_position = Constants.liftLow;// automatically go to the low ship hub position(arm)

        telemetry.addData("!!!Lift Position:", lift_position);
        telemetry.addData("Lift should be to go down:", (lift_position == Constants.liftHigh));
        telemetry.addData("Lift should be to go up:", (lift_position == Constants.liftIntake));
        telemetry.addData("gamepad1.right_bumper", gamepad1.right_bumper);
        if (Lift.updatePosition(lift_position, lift) == true) // middle
        {
            if (lift_position == Constants.liftLow) dump_position = Constants.dumpLow;
            if (lift_position == Constants.liftMid) dump_position = Constants.dumpMid;
            if (lift_position == Constants.liftHigh) dump_position = Constants.dumpHigh;
            if (lift_position == Constants.liftIntake) dump_position = Constants.dumpStraight;

            //finish
        }

        if(gamepad1.dpad_up)dump_position = Constants.dumpFold; //fold up if we press dpadup

        if ((color_sensor.red()>color_sensor.blue()*2 && color_sensor.green()>color_sensor.blue()*2)) // sees red
        {
            led_pattern=2;
            telemetry.addData("Done", "Done");
        }

        telemetry.addData("lift:", lift.getCurrentPosition());
    }

    public void TotalAutoSpecific() //too scary dont use
    {
        if(gamepad1.a) // automatically go to the intake position(arm)
        {
            lift_position = Constants.liftIntake;

            claw_position=0;
            dump_position=Constants.dumpFold; // start
        }

        if(Lift.updatePosition(lift_position, lift)==true
                && lift_position == Constants.liftIntake)
        {
            dump_position=Constants.dumpStraight;
            dump_position=Constants.dumpStraight;
            dump_position=Constants.dumpHigh;
            if(lift_position == Constants.liftIntake) dump_position=Constants.dumpStraight;
        }
        if((color_sensor.red()>color_sensor.blue()*2 && color_sensor.green()>color_sensor.blue()*2))
        //detects orange cube and it at the intake position
        {
            lift_position = Constants.liftHigh; //TODO check

            claw_position=0;
            dump_position=Constants.dumpFold; // start


            //finish
        }
    }
}