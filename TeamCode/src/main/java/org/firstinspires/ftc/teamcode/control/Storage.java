package org.firstinspires.ftc.teamcode.control;

import com.acmerobotics.roadrunner.geometry.Pose2d;

//================================================================================
// Dynamic shared variables/ Allows for quick access to everything during competitions
//================================================================================

public class Storage
{



    //================================================================================
    // Auto variables
    //================================================================================


    //================================================================================
    // Red/Blue variables
    //================================================================================

    //================================================================================
    // Accessors
    //================================================================================
    //Blue/Red variables
    public static int offset = -35; //TODO 0 for left, change for right  -35 for left, 12 for right
    public static int side = 1;






    //Red-specific auto variables


    //Blue-specific auto variables


    //for freight collection during auto
    public static double autoTimeLimit = 30; // in seconds
    public static double run_time_red = 7;
    public static double run_time_blue = 10;
    public static boolean freight = false;

    ///////////////////////////////////ROBOT SUBSYSTEMS////////////////////////////////
    //Webcam
    public static int x_center = 200; //middle of the webcam view
    public static int center_accuracy = 70;


}
