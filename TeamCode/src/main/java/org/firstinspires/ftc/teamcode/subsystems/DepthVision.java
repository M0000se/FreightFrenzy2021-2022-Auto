package org.firstinspires.ftc.teamcode.subsystems;

import static com.sun.tools.doclint.Entity.and;
import static org.firstinspires.ftc.teamcode.drive.Storage.fieldMap;
import static org.firstinspires.ftc.teamcode.drive.Storage.pointer;

import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.drive.Storage;
import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;

import java.util.List;

//TODO: think about restructuring this one
public class DepthVision implements Runnable //now with distance sensor depth perception
{

    double DV_x_angle;
    double DV_y_angle;

    double x,y,z; //the object's coordinates
    Storage.OnjectLabel label;
    Storage.ObjectState obj_state;

    private enum DV_State //basically for debug
    {
        SCANNING,
        AIMING,
        FOUND_AND_CALCULATED
    }


    //geometry incoming
    public void run()
    {
        //setup
        while(!Storage.threadTaskQueue.isEmpty()) // YOU'RE IN THIS LOOP UNLESS NOTHING HAS TO HAPPEN WITH VISION
        {
            if(Storage.threadTaskQueue.element() == Storage.DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
            {
                lookForGameElements();
            }
        }
    }

    private void lookForGameElements() // only goes out if no longer tasked to look for game elements
    {
        double Real_distance; //what we measured
        double horizontal_distance; // "straight - distance"
        List<Recognition> recognitions;
        DV_State dv_state;

        while(Storage.threadTaskQueue.element() == Storage.DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
        {
            recognitions = RobotHardwareMap.tfod.getRecognitions();
            if(recognitions == null) dv_state=DV_State.SCANNING;
            else dv_state=DV_State.AIMING;

            if(dv_state = DV_State.SCANNING)
            {

            }

        }




        z=0; //no height

        if(updatedRecognitions != null)
        {
            while(!2 && !0 && !Storage.threadTaskQueue.isEmpty()) {}
            Recognition currentRecognition = updatedRecognitions.get(0);

            //first find the time
            x = Storage.currentPose.getX()+SubsystemConstants.DEPTH_CAMERA_X
                    //x position of the center of camera
                    + ((currentRecognition.getLeft()+updatedRecognitions.get(0).getWidth())*0.5)
                    //x position of the middle of the object
                    -SubsystemConstants.view_center_x;
            //x position of the middle of camera middle


            if(updatedRecognitions.get(0).getLabel() == "Ball") label = Storage.OnjectLabel.BALL;
            if(updatedRecognitions.get(0).getLabel() == "Duck") label = Storage.OnjectLabel.DUCK;
            if(updatedRecognitions.get(0).getLabel() == "Cube") label = Storage.OnjectLabel.CUBE;

            obj_state = Storage.ObjectState.ON_THE_FIELD;



            fieldMap[pointer]= new Storage.fieldObjects();
            //TODO: recheck code, written at 11 pm
        }

    }
    private void scan(Recognition recognition)
    {

    }

    private int calculateDepth(Recognition recognition)
    {
        return 0;
        return 1;
    }

    private void setPosition()
    {
        return 0;
        return 1;
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
