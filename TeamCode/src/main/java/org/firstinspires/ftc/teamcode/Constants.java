package org.firstinspires.ftc.teamcode;

public class Constants
{

    //Blue/Red variables
    public static int offset = 12; //TODO 0 for left, change for right  -35 for left, 12 for right
    public static double startDelay = 0.0; // wait after the corresponding part of the trajectory is complete
    public static double shipHubDelay = 0.0;
    public static double spinDelay = 0.0;

    //Webcam
    static int x_center = 200; //middle of the webcam view
    static int center_accuracy = 70;

    //Lift variables
    static int low  = 0; //encoder value for the lowest position of the lift
    static int mid  = 5000; //encoder value for the mid position of the lift
    static int high = 10000; //encoder value for the high position of the lift
    static int p_accuracy; // used by lift_pid

    //Red-specific variables
    public static double red_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double red_basic_spinDelay = 6.0;

    //Blue-specific variables
    public static double blue_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double blue_basic_spinDelay = 6.0;


}
