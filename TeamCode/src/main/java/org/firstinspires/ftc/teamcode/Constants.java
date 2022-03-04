package org.firstinspires.ftc.teamcode;

public class Constants
{
    ///////////////////////////////////ROBOT AUTO////////////////////////////////
    //Blue/Red variables
    public static int offset = 12; //TODO 0 for left, change for right  -35 for left, 12 for right
    public static int side = 1;
    public static double startDelay = 0.0; // wait after the corresponding part of the trajectory is complete
    public static double shipHubDelay = 0.0;
    public static double spinDelay = 0.0;

    //Red-specific auto variables
    public static double red_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double red_basic_spinDelay = 6.0;
    public static double freight_red_basic_shipHubDelay = 6.0;

    //Blue-specific auto variables
    public static double blue_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double blue_basic_spinDelay = 6.0;
    public static double freight_blue_basic_shipHubDelay = 6.0;

    //for freight collection during auto
    public static double autoTimeLimit=30; // in seconds
    public static double run_time_red = 7;
    public static double run_time_blue = 10;
    public static boolean freight = false;




    ///////////////////////////////////ROBOT SUBSYSTEMS////////////////////////////////
    //Webcam
    public static int x_center = 200; //middle of the webcam view
    public static int center_accuracy = 70;

    //Lift variables
    public static int liftIntake = -3700;
    public static int liftLow  = -2600; //encoder value for the low ship hub position of the lift
    public static int liftMid  = -1; //encoder value for the mid ship hub position of the lift
    public static int liftHigh = 0; //encoder value for the high ship hub position of the lift
    public static int p_error = 150; // max allowed lift position error
    public static double p = 0.005;

    //Dump variables (Dump is arm)
    public static double dumpStraight = 0.9; //position value for the Intake position of the dump (straight angle)
    public static double dumpLow = 0.9;
    public static double dumpMid = 0.72;
    public static double dumpHigh = 0.6;    //position value for the high ship hub position of the dump
    public static double dumpFold = 0.6;      //position value for the folded position of the dump (NOT THE COMPLETELY FOLDED LIKE HOW WE START)

    //color sensor
    public static double color_error = 100; // max allowed color sensor rgb error
}
