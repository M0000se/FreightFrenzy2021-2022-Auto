package org.firstinspires.ftc.teamcode.drive.navigation;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.drive.navigation.data_types.fieldObject;

public class Navigation
{
    private static final double tolerance = 0; // circular tolerance as to what we consider the same object
    private static final int MAX_FIELD_MAP_SIZE = 1000; // maximum number of objects we can store
    public static fieldObject[] fieldMap = new fieldObject[MAX_FIELD_MAP_SIZE]; //1000 field objects max
    public static Pose2d currentPose = new Pose2d(); // always keep our position, regardless of auto/teleop
    public static int fieldMap_size = 0;
    // (how close it should be to be considered the same thing)
    //TODO: set value
    /**
     * Returns if an object is already on the map
     * Simple linear search
     */
    //TODO: get a better search maybe
    public static boolean isMapped(fieldObject fieldObject)
    {
        for(int i = 0; i < fieldMap_size; i++)
        {
            if((fieldMap[i].x + tolerance > fieldObject.x) && (fieldMap[i].x - tolerance < fieldObject.x)
                    && (fieldMap[i].y + tolerance > fieldObject.y) && (fieldMap[i].y - tolerance < fieldObject.y))
                return true;
        }
        return false;
    }
}
