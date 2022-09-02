package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

//Constants for all subsystems
public final class SubsystemConstants
{
    public static final double COLOR_ERROR = 90; // max allowed color sensor rgb error

    public static final int VIEW_CENTER_X = 200; //middle of the webcam view
    public static final int VIEW_CENTER_Y = 200; //middle of the webcam view
    public static final int CENTER_ACCURACY = 70;

    public static int P_ERROR = 50; // max allowed motor position error
    public static final double p = 0.001;
    public static int MAX_FIELD_MAP_SIZE = 1000; // maximum number of objects we can store

    public static final int QUEUE_SIZE = 10000;
    public static final int DV_X_OFFSET = 0; //from the center of the robot (the coordinate system Roadunner uses)
    public static final int DV_Y_OFFSET = 0;
    public static final int DV_CAMERA_X_OFFSET = 0; //from the coordinate system of the x rotation servo
    public static final int DV_DIST_SENSOR_X_OFFSET = 0;
    public static final int DV_DIST_SENSOR_Y_OFFSET = 0;
    public static final double DV_X_KP = 0, DV_X_KI = 0, DV_X_KD = 0, DV_X_KF = 0;
    public static final double DV_X_TOLERENCE = 0;
    public static final double DV_Y_KP = 0, DV_Y_KI = 0, DV_Y_KD = 0, DV_Y_KF = 0;
    public static final double DV_Y_TOLERENCE = 0;

    // TODO: set the value WILL NOT WORK WITHOUT THAT
}
