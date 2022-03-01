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

    //Blue variables
    public static double red_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double red_basic_spinDelay = 6.0;

    //Red variables
    public static double blue_basic_shipHubDelay = 0.1; // constant minimal time to complete a corresponding marker
    public static double blue_basic_spinDelay = 6.0;
}
