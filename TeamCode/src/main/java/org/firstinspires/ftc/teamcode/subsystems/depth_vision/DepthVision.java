package org.firstinspires.ftc.teamcode.subsystems.depth_vision;

import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_CAMERA_X_OFFSET;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_DIST_SENSOR_X_OFFSET;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_DIST_SENSOR_Y_OFFSET;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_X_OFFSET;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_X_TOLERENCE;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_Y_OFFSET;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.DV_Y_TOLERENCE;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.VIEW_CENTER_X;
import static org.firstinspires.ftc.teamcode.drive.SubsystemConstants.VIEW_CENTER_Y;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.Storage;
import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;

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

    class FlatCords
    {
        double x;
        double y;
        FlatCords(double _x, double _y)
        {
            this.x = _x;
            this.y = _y;
        }
        FlatCords() {}
    }

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
        setServoPosition(x_position, y_position);
    }

    /**
     *  aims and finds the coordinates of what's closest to a given object,
     *  This uses centroid tracking, and since the game elements are not substantially unique,
     *  low enough frame rate / high enough robot movement speed / similar object locations
     *  may give you the data of a wrong object
     * @param recognition the given object
     * @return
     */
    // TODO: !! implement Deep SORT to actually track targets and get the cords of the recognition !!
    private Storage.fieldObject calculateCords(Recognition target_object)
    {
        com.arcrobotics.ftclib.controller.PIDFController pidf_x =
                new PIDFController(SubsystemConstants.DV_X_KP, SubsystemConstants.DV_X_KI,
                        SubsystemConstants.DV_X_KD, SubsystemConstants.DV_X_KF);
        com.arcrobotics.ftclib.controller.PIDFController pidf_y =
                new PIDFController(SubsystemConstants.DV_Y_KP, SubsystemConstants.DV_Y_KI,
                        SubsystemConstants.DV_Y_KD, SubsystemConstants.DV_Y_KF);

        List<Recognition> recognitions;
        recognitions = RobotHardwareMap.tfod.getRecognitions();

        double x_change;
        double y_change;

        FlatCords target_cords = getCenterCords(target_object);

        pidf_x.setSetPoint(DV_CAMERA_X_OFFSET - DV_DIST_SENSOR_X_OFFSET);
        pidf_y.setSetPoint(0);

        pidf_x.setTolerance(DV_X_TOLERENCE);
        pidf_x.setTolerance(DV_Y_TOLERENCE);

        while(!pidf_y.atSetPoint() && !pidf_x.atSetPoint() && !(recognitions == null))
        {
            int num_of_recognitions = recognitions.size();
            int min_dist_index = 0;
            double min_dist_value = Double.MAX_VALUE;
            int i = 0;
            for(Recognition recognition : recognitions)
            {
                FlatCords current_cords = getCenterCords(recognition);

                double current_x_dist = abs(target_cords.x - current_cords.x);
                double current_y_dist = abs(target_cords.y - current_cords.y);
                double current_dist = sqrt(current_x_dist * current_x_dist + current_y_dist * current_y_dist;
                if(min_dist_value > current_dist)
                {
                    min_dist_index = i;
                    min_dist_value = current_dist;
                }
                i++;
            }
            target_object = recognitions.get(min_dist_index); // position of the target object as of pidf calculation
            target_cords = getCenterCords(target_object);
            x_change = pidf_x.calculate(VIEW_CENTER_X - target_cords.x);
            y_change = pidf_y.calculate(VIEW_CENTER_Y - target_cords.y);
            setServoPosition(DV_x_angle + x_change, DV_y_angle + y_change); //adjust the servo
            recognitions = RobotHardwareMap.tfod.getRecognitions();
        }
        if(pidf_y.atSetPoint() && pidf_x.atSetPoint())
        {
            double measured_dist = dist_sensor.getDistance(DistanceUnit.INCH);
            Storage.fieldObject target_object_field_coordinates = new Storage.fieldObject();
            target_object_field_coordinates.x =
                    Storage.currentPose.getX() + DV_X_OFFSET + sin(DV_x_angle) * (measured_dist+DV_DIST_SENSOR_Y_OFFSET)
                            + cos(DV_x_angle) * DV_DIST_SENSOR_X_OFFSET;
            target_object_field_coordinates.y =
                    Storage.currentPose.getY() + DV_Y_OFFSET + sin(DV_y_angle) * measured_dist;

        }
        else return null;


    }


    /**
     * finds
     * @param target_object
     * @return true if it aimed successfully, and false if it couldn't
     */
    private boolean aimAtClosestRecognition(Recognition target_object)
    {

    }

    /**
     * Returns the coordinates from the center of the camera view to the middle of an object
     * @param recognition camera recognition
     * @return flat_cords Middle
     */
    private FlatCords getCenterCords(Recognition recognition)
    {
        FlatCords flat_cords = new FlatCords();
        flat_cords.x = recognition.getLeft() + recognition.getWidth()/2 - VIEW_CENTER_X;
        flat_cords.y = recognition.getBottom() + recognition.getHeight()/2 - VIEW_CENTER_Y;
        return flat_cords;
    }

    private void setServoPosition(double x, double y)
    {
        DV_x_angle = x;
        DV_y_angle = y;
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
                            if (recognition.getLeft() > SubsystemConstants.x_center + SubsystemConstants.CENTER_ACCURACY) return 2;
                            else if (recognition.getLeft() < SubsystemConstants.x_center - SubsystemConstants.CENTER_ACCURACY) return 0;
                            else return 1; //TODO: test

                        } else isDuckDetected = false;

                    }
                }
            }
        }
    }
}
