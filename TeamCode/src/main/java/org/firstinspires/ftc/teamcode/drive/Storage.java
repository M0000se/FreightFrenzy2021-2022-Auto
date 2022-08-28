package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//================================================================================
// Shared Dynamic variables
//================================================================================

public class Storage
{
    public enum OnjectLabel //TODO:expand
    {
        BALL,
        DUCK,
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
    public class fieldObjects
    {
        public int x,y,z; //since Im not finding the height at which objects are located (yet), y is always 0
        public OnjectLabel label; // what the object is, eg a duck or a cube
        public ObjectState state; // for now, simply is it collected or on the field

        fieldObjects(int x, int y, int z, OnjectLabel label, ObjectState state)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.label = label;
            this.state = state;
        }
    }
    public static Pose2d currentPose = new Pose2d(); // always keep our position, regardless of auto/teleop
    public static BlockingQueue<DepthVisionState> threadTaskQueue = new LinkedBlockingDeque<DepthVisionState>(SubsystemConstants.queue_size); // always keep the task queue
    public static fieldObjects[] fieldMap = new fieldObjects[SubsystemConstants.max_field_map_size]; //1000 field objects max
    public static int pointer = 0;

}
