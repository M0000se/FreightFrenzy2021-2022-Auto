package org.firstinspires.ftc.teamcode;

public class Constants
{

    //Blue/Red variables
    public static int offset = 12; //TODO 0 for left, change for right  -35 for left, 12 for right
    public static double startDelay = 0.0; // wait after the corresponding part of the trajectory is complete
    public static double shipHubDelay = 0.0;
    public static double spinDelay = 0.0;

    //Webcam
    public static int x_center = 200; //middle of the webcam view
    public static int center_accuracy = 70;

    //Lift variables
    public static int liftIntake = -2200;
    public static int liftLow  = -2100; //encoder value for the lowest position of the lift
    public static int liftMid  = 0; //encoder value for the mid position of the lift
    public static int liftHigh = 0; //encoder value for the high position of the lift
    public static int p_accuracy = 20; // used by lift_pid
    public static int liftVelocity = 2000;

    //Dump variables
    public static double dumpLow  = 0.8; //encoder value for the lowest position of the lift
    public static double dumpMid  = 0.4; //encoder value for the mid position of the lift
    public static double dumpHigh = 10000; //encoder value for the high position of the lift

    //Red-specific variables
    public static double red_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double red_basic_spinDelay = 6.0;

    //Blue-specific variables
    public static double blue_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double blue_basic_spinDelay = 6.0;


}
