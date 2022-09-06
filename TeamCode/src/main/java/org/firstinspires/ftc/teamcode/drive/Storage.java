package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//================================================================================
// Shared Dynamic variables
//================================================================================

public class Storage
{
    private static final int QUEUE_SIZE = 10000;
    private static final int MAX_FIELD_MAP_SIZE = 1000; // maximum number of objects we can store

    public enum ObjectLabel //TODO:expand
    {
        BALL,
        DUCK,
        CUBE
    }
    public enum ObjectState
    {
        COLLECTED,
        ON_THE_FIELD
    }
    public enum DepthVisionState //what if you don't want to LOOK FOR GAME ELEMENTS, but, for example, want to make the distance servo DANCE? expand it
    {
        LOOK_FOR_GAME_ELEMENTS
    }

    //TODO: consider moving these enums and the fieldObjects class
    public class fieldObject
    {
        public double x,y,z; //since Im not finding the height at which objects are located (yet), z is always 0
        public ObjectLabel label; // what the object is, eg a duck or a cube
        public ObjectState state; // for now, simply is it collected or on the field

        fieldObject(double x, double y, double z, ObjectLabel label, ObjectState state)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.label = label;
            this.state = state;
        }
        fieldObject() {}

    }
    public static Pose2d currentPose = new Pose2d(); // always keep our position, regardless of auto/teleop
    public static BlockingQueue<DepthVisionState> threadTaskQueue = new LinkedBlockingDeque<DepthVisionState>(QUEUE_SIZE); // always keep the task queue
    public static fieldObject[] fieldMap = new fieldObject[MAX_FIELD_MAP_SIZE]; //1000 field objects max
    public static int fieldMap_size = 0;

}
