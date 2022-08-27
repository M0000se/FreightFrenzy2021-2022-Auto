package org.firstinspires.ftc.teamcode.subsystems;

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
        while(!Storage.threadTaskQueue.isEmpty())
        {
            if(Storage.threadTaskQueue.element()==Storage.DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
            {
                List<Recognition> updatedRecognitions = RobotHardwareMap.tfod.getUpdatedRecognitions();
            }
        }
    }

    private void findDepth()
    {

    }

    /*static public void getElementPosition ()
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
    } */
}
