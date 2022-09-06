package org.firstinspires.ftc.teamcode.drive;

import org.firstinspires.ftc.teamcode.drive.Storage;

public class Navigation
{
    private static final double tolerance = 0; // circular tolerance as to what we consider the same object
                                               // (how close it should be to be ocnsidered the same thing)
    //TODO: set value
    /**
     * Returns if an object is already on the map
     * Simple linear search
     */
    //TODO: get a better search maybe
    public static boolean isMapped(Storage.fieldObject fieldObject)
    {
        for(int i = 0; i < Storage.fieldMap_size; i++)
        {
            if((Storage.fieldMap[i].x + tolerance > fieldObject.x) && (Storage.fieldMap[i].x - tolerance < fieldObject.x)
                    && (Storage.fieldMap[i].y + tolerance > fieldObject.y) && (Storage.fieldMap[i].y - tolerance < fieldObject.y))
                return true;
        }
        return false;
    }
}
