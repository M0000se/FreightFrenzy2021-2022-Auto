package org.firstinspires.ftc.teamcode;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import java.util.Locale;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;

@TeleOp(name = "NEW_DRIVE")
public class Drive extends LinearOpMode {

    private DcMotor lift;
    private DcMotor RightFront;
    private DcMotor RightRear;
    private DcMotor LeftRear;
    private DcMotor LeftFront;
    private DcMotor Spinner;
    private Servo Claw;
    private AndroidSoundPool androidSoundPool;
    private Servo Dump;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        double Vertical;
        double Horizontal;
        double Pivot;

        lift = hardwareMap.get(DcMotor.class, "Lift");
        RightFront = hardwareMap.get(DcMotor.class, "RightFront");
        RightRear = hardwareMap.get(DcMotor.class, "RightRear");
        LeftRear = hardwareMap.get(DcMotor.class, "LeftRear");
        LeftFront = hardwareMap.get(DcMotor.class, "LeftFront");
        Spinner = hardwareMap.get(DcMotor.class, "Spinner");
        Claw = hardwareMap.get(Servo.class, "Claw");
        androidSoundPool = new AndroidSoundPool();
        Dump = hardwareMap.get(Servo.class, "Dump");

        // Put initialization blocks here.
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        RightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        RightRear.setDirection(DcMotorSimple.Direction.REVERSE);
        LeftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Claw.setDirection(Servo.Direction.REVERSE);
        androidSoundPool.initialize(SoundPlayer.getInstance());
        androidSoundPool.play("RawRes:ss_r2d2_up");


        waitForStart();

        ElapsedTime time = new ElapsedTime();
        boolean b_open=true;
        boolean completed=false;
        boolean activate=false;
        boolean init=false;
        int override = 0; // 0 for no override, 1 for lift override, 2 for total override
        boolean direction = false;

        double currentTime = time.time();
        double dump_position=0;
        double claw_position=0;
        double lift_power=0;
        double spinner_power=0;
        boolean spinner_direction = false;
        double delay = 0;
        double mult=0.5;

        if (opModeIsActive())
        {
            while (opModeIsActive())
            {

                // Put loop blocks here.
                // Updates the value from the sticks
                Vertical = -(gamepad1.right_stick_y * gamepad1.right_stick_y * gamepad1.right_stick_y * 1 * mult);
                Horizontal = gamepad1.right_stick_x * gamepad1.right_stick_x * gamepad1.right_stick_x * 1 * mult;
                Pivot = gamepad1.left_stick_x * gamepad1.left_stick_x * gamepad1.left_stick_x * 0.6*mult;

                if(gamepad1.left_bumper) mult = 1;// 50% speed
                else mult = 0.5;

                // switch modes

                //Override regulation
                if(gamepad2.dpad_down || gamepad1.dpad_left) // automatically go down(arm)
                {
                    activate = true;//reset lift
                    override = 1; // on the way down, you're free to move the arm
                    direction = false;
                    delay = 1.90;

                    claw_position=1;
                    dump_position=0.5;
                    currentTime=time.time();
                }

                if(gamepad2.dpad_up || gamepad1.dpad_right) // automatically go up(arm)
                {
                    activate = true;//reset lift
                    override = 2; // on the way up, you're not free to move
                    direction = true;
                    delay=1.90;

                    claw_position=1;
                    dump_position=0.5;
                    currentTime=time.time();
                }

                if((gamepad2.b||gamepad1.b) && b_open)// manually disable override with b
                {
                    override = 0;
                    //androidSoundPool.play("RawRes:ss_alarm");
                    telemetry.speak("Override disabled");
                    b_open=false;
                }
                if((!gamepad2.b||gamepad1.b)) b_open=true;

                if(override>0) // if we gave auto control running (override == 1 or 2), and it has not been interrupted by manually switching off the override, we run it.
                {
                    //some noodlecode bs
                    if(activate)
                    {
                        //telemetry.addData("direction",direction );
                        //stop();
                        boolean dont_allocate_memory_like_this = false; //TODO: fix
                        if(direction==false) dont_allocate_memory_like_this = Lift.updatePosition(Constants.liftLow, lift);
                        if(direction==true)  dont_allocate_memory_like_this = Lift.updatePosition(Constants.liftHigh, lift);
                        if(dont_allocate_memory_like_this)
                        {
                            dump_position=1;
                            activate=false;
                            if(direction==true)  dump_position=0;
                            if(direction==false) dump_position=1;
                        }
                    }
                }
                // you can always move the claw
                claw_position = Math.max(gamepad2.right_trigger, gamepad1.right_trigger);
                // Operate claw with right trigger

                if(override<2) //you can move the dump when in override 1
                {
                    // Operate Dump with Y and right bumper
                    if (gamepad2.y || gamepad1.y)
                    {
                        dump_position=Constants.dumpMid;
                    }
                    if (gamepad2.right_bumper || gamepad1.right_bumper)
                    {
                        dump_position=Constants.dumpLow;
                    }
            /*else //!!!!                                                          DECOMENT FOR MAYA CODE
            {
              dump_position=0.5;
            }*/
                }

                if(override<1) //you can also move the lift when in override 0
                {
                    // Operate lift with DPad
                    if(gamepad2.dpad_left || gamepad1.dpad_down) lift_power=-1;
                    else if(gamepad2.dpad_right || gamepad1.dpad_up) lift_power = 1;
                    else {lift_power = 0;}
                    //disable manual lift movement when automatically moving the lift
                }


                spinner_power=Math.max(gamepad2.left_trigger,gamepad1.left_trigger) ;




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


                lift.setPower(lift_power);
                Dump.setPosition(dump_position);
                Claw.setPosition(claw_position);
                Spinner.setPower(spinner_power);

                if(gamepad1.left_bumper || gamepad2.left_bumper) Spinner.setDirection(DcMotorSimple.Direction.REVERSE);
                else                   Spinner.setDirection(DcMotorSimple.Direction.FORWARD);


                if(override==0) telemetry.addLine("Override: OFF");
                else telemetry.addData("Override:", override);
                //telemetry.addData("override: False", override);
                telemetry.addData("TIME:", time.time());
                telemetry.update();
            }
        }

        androidSoundPool.close();
    }
}