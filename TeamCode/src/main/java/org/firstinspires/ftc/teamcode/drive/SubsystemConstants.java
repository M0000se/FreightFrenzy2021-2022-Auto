package org.firstinspires.ftc.teamcode.drive;

//Constants for all subsystems
public final class SubsystemConstants
{
    public static final double color_error = 90; // max allowed color sensor rgb error

    public static final int view_center_x = 200; //middle of the webcam view
    public static final int center_accuracy = 70;

    public static int p_error = 50; // max allowed motor position error
    public static final double p = 0.001;
    public static int max_field_map_size = 1000; // maximum number of objects we can store

    public static final int queue_size = 10000;
    public static final int DEPTH_CAMERA_X = 0; //from the center of the robot (the coordinate system Roadunner uses)
    // TODO: set the value WILL NOT WORK WITHOUT THAT
    public static int DEPTH_CAMERA_Y = 0;
    public static int DEPTH_CAMERA_Z = 0;

}
