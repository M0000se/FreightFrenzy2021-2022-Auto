package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.drive.Storage.fieldMap;
import static org.firstinspires.ftc.teamcode.drive.Storage.pointer;

import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.drive.Storage;
import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;

import java.util.List;

public class DepthVision implements Runnable //now with distance sensor depth perception
{
    public void run()
    {
        double x,y,z; //the object's coordinates

        y=0;
        while(!Storage.threadTaskQueue.isEmpty())
        {
            if(Storage.threadTaskQueue.element() == Storage.DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
            {
                List<Recognition> updatedRecognitions = RobotHardwareMap.tfod.getUpdatedRecognitions();
                if(updatedRecognitions != null)
                {
                    x = Storage.currentPose.getX()+SubsystemConstants.DEPTH_CAMERA_X
                            //x position of the center of camera
                            + ((updatedRecognitions.get(0).getLeft()+updatedRecognitions.get(0).getWidth())*0.5)
                            //x position of the middle of the object
                            -SubsystemConstants.view_center_x;
                            //x position of the middle of camera middle


                    fieldMap[pointer]= new Storage.fieldObjects();
                    //TODO: recheck code, written at 11 pm
                }
            }
        }
    }

    private void findDepth()
    {

    }

    static public void getElementPosition ()
    {






        for (; ; )
        {
            if (RobotHardwareMap.tfod != null)
            {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.

                if (updatedRecognitions != null)
                {
                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    boolean isDuckDetected = false;     //  ** ADDED **
                    for (Recognition recognition : updatedRecognitions) {
                        i++;
                        // check label to see if the camera now sees a Duck
                        if (recognition.getLabel().equals("Duck")) {            //  ** ADDED **
                            isDuckDetected = true;
                            if (recognition.getLeft() > SubsystemConstants.x_center+ SubsystemConstants.center_accuracy) return 2;
                            else if (recognition.getLeft() < SubsystemConstants.x_center- SubsystemConstants.center_accuracy) return 0;
                            else return 1; //TODO: test

                        } else isDuckDetected = false;

                    }
                }
            }
        }
    }
}
