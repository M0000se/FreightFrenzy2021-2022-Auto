package org.firstinspires.ftc.teamcode.subsystems.depth_vision;

import androidx.annotation.Nullable;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.Storage;
import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;
import org.firstinspires.ftc.teamcode.subsystems.DistanceSensor;

import java.util.List;

// everything "shared" for the depth camera (task manager and shared task methods)
// basically the whole thing is like the op-mode code structure ftc is using, but on a smaller scale
public class DepthVision implements Runnable
{
    static double DV_x_angle;
    static double DV_y_angle;

    //to implicitly restrict access to those to only other task classes
    DistanceSensor dist_sensor;

    Servo x_servo;
    Servo y_servo;

    //geometry incoming
    public void run() // EVERY dv substask gets called from here
    {
        //setup
        RobotHardwareMap hw = new RobotHardwareMap();
        Servo dv_x_angle = hw.servoX;
        Servo dv_y_angle = hw.servoY;

        dist_sensor = hw.navDistanceSensor;

        while(!Storage.threadTaskQueue.isEmpty()) // YOU'RE IN THIS LOOP UNLESS NOTHING HAS TO HAPPEN WITH VISION
        {
            if(Storage.threadTaskQueue.element() == Storage.DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
            {
                lookForGameElements();
            }
        }
    }

    /* passses down the DV state, at which the completion should still proceed from the task called
       amount to slew servo each CYCLE_MS cycle
       period of each cycle
       Maximum rotational position
       Minimum rotational position
       Same for y

       Moves the servo up and down while moving it side to side
       Basic settings are

       */
    boolean x_rampUp = true;
    boolean y_rampUp = true;
    private void asyncScan(Storage.DepthVisionState allowed_state,
                      double x_increment, int x_cycle_ms, double x_max_pos, double x_min_pose,
                      double y_increment, int y_cycle_ms, double y_max_pos, double y_min_pose)
    {
        // Define class members
        double  x_position = (x_max_pos - x_min_pose) / 2; // Start at halfway position

        if (x_rampUp)
        {
            // Keep stepping up until we hit the max value.
            x_position += x_increment ;
            if (x_position >= x_max_pos )
            {
                x_position = x_max_pos;
                x_rampUp = !x_rampUp;   // Switch ramp direction
            }
        }
        else {
            // Keep stepping down until we hit the min value.
            x_position -= x_increment ;
            if (x_position <= x_min_pose )
            {
                x_position = x_min_pose;
                x_rampUp = !x_rampUp;  // Switch ramp direction
            }
        }


        double  y_position = (x_max_pos - x_min_pose) / 2; // Start at halfway position
        if (y_rampUp)
        {
            // Keep stepping up until we hit the max value.
            y_position += x_increment ;
            if (y_position >= y_max_pos )
            {
                y_position = y_max_pos;
                y_rampUp = !y_rampUp;   // Switch ramp direction
            }
        }
        else {
            // Keep stepping down until we hit the min value.
            y_position -= y_increment ;
            if (y_position <= y_min_pose )
            {
                y_position = y_min_pose;
                y_rampUp = !y_rampUp;  // Switch ramp direction
            }
        }
        SetServoPosition(x_position, y_position);
    }

    /**
     *  aims and finds what's closest to a given object,
     *  try aiming at that.This uses centroid tracking, and since the game elements are not substantially unique,
     *  low enough frame rate may give you the data of a wrong object
     * @param 
     */
    // TODO: !! implement Deep SORT to actually track targets and get the cords of the recognition !!
    private Storage.fieldObjects calculateCords()
    {
        // Check if we see anything
        List<Recognition> recognitions;
        Storage.fieldObjects target_object = new Storage.fieldObjects;

        // the math is documented in my separate set of notes

        double x_current_dif;
        double x_required_dif;
        double x_change;

        com.arcrobotics.ftclib.controller.PIDFController pidf =
                new PIDFController(SubsystemConstants.DV_KP, SubsystemConstants.DV_KI, SubsystemConstants.DV_KD, SubsystemConstants.DV_KF);

        x_required_dif = SubsystemConstants.DV_CAMERA_X_OFFSET - SubsystemConstants.DV_DIST_SENSOR_X_OFFSET;
        pidf.setSetPoint(x_required_dif);

        while(!pidf.atSetPoint() && (recognitions != null))
        {
            recognitions = RobotHardwareMap.tfod.getRecognitions();
            Recognition cur_recognition = get_closest_recognition();

            x_current_dif = SubsystemConstants.VIEW_CENTER_X-(cur_recognition.getLeft() + cur_recognition.getWidth());
            x_change = pidf.calculate(x_current_dif);

            DV_CAMERA_X_OFFSET
        }
        if(pidf.atSetPoint()) //finish this from calculations
        {
            target_object.y =
                    target_object.x =
                            Storage.currentPose.getX() + SubsystemConstants.DV_X_OFFSET + Math.sin(x_servo.getPosition())
            return target_object
        }
        else return null;


    }

    private void setServoPosition(double x, double y) // every servo_position modification goes through here
    {
        x_servo.setPosition(x);
        y_servo.setPosition(y);
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
                            if (recognition.getLeft() > SubsystemConstants.x_center+ SubsystemConstants.CENTER_ACCURACY) return 2;
                            else if (recognition.getLeft() < SubsystemConstants.x_center- SubsystemConstants.CENTER_ACCURACY) return 0;
                            else return 1; //TODO: test

                        } else isDuckDetected = false;

                    }
                }
            }
        }
    }
}
