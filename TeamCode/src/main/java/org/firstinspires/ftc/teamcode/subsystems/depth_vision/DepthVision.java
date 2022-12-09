package org.firstinspires.ftc.teamcode.subsystems.depth_vision;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.navigation.Navigation;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.navigation.data_types.FlatCords;
import org.firstinspires.ftc.teamcode.drive.navigation.data_types.ObjectLabel;
import org.firstinspires.ftc.teamcode.drive.navigation.data_types.ObjectState;
import org.firstinspires.ftc.teamcode.drive.navigation.data_types.fieldObject;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

// everything "shared" for the depth camera (task manager and shared task methods)
// basically the whole thing is like the op-mode code structure ftc is using, but on a smaller scale
// TODO: MOST IMPORTANT implement object tracking to step through recognitions and differentiate between different ones.
public class DepthVision implements Runnable
{
    public enum DepthVisionState //what if you don't want to LOOK FOR GAME ELEMENTS, but, for example, want to make the distance servo DANCE? expand it
    {
        LOOK_FOR_GAME_ELEMENTS
    }

    private static final int QUEUE_SIZE = 10000;
    public static BlockingQueue<DepthVisionState> threadTaskQueue = new LinkedBlockingDeque<DepthVisionState>(QUEUE_SIZE); // always keep the task queue

    private final static int DV_X_OFFSET = 0; //from the center of the robot (the coordinate system Roadunner uses)
    private final static int DV_Y_OFFSET = 0;
    private final static int DV_CAMERA_X_OFFSET = 0; //from the coordinate system of the x rotation servo
    private final static int DV_DIST_SENSOR_X_OFFSET = 0;
    private final static int DV_DIST_SENSOR_Y_OFFSET = 0;
    private final static double DV_X_KP = 1, DV_X_KI = 0, DV_X_KD = 0, DV_X_KF = 0;
    private final static double DV_X_TOLERANCE = 1;
    private final static double DV_Y_KP = 1, DV_Y_KI = 0, DV_Y_KD = 0, DV_Y_KF = 0;
    private final static double DV_Y_TOLERANCE = 1;

    private final static int VIEW_CENTER_X = 200; //middle of the webcam view
    private final static int VIEW_CENTER_Y = 200; //middle of the webcam view
    private final static int CENTER_ACCURACY = 70;

    private static double DV_x_angle;
    private static double DV_y_angle;

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

        while(!threadTaskQueue.isEmpty()) // YOU'RE IN THIS LOOP UNLESS NOTHING HAS TO HAPPEN WITH VISION
        {
            if(threadTaskQueue.element() == DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
            {
                //initialization
                List<Recognition> recognitions = RobotHardwareMap.tfod.getRecognitions();
                int cur_index = 0;
                while (threadTaskQueue.element() == DepthVisionState.LOOK_FOR_GAME_ELEMENTS)
                {
                    if (recognitions != null)
                    {
                        fieldObject recognised_object = calculateCords(recognitions.get(cur_index));
                        if (recognised_object != null)
                        {
                            if(!Navigation.isMapped(recognised_object))
                            {
                                Navigation.fieldMap[Navigation.fieldMap_size] = recognised_object;
                                Navigation.fieldMap_size++;
                            }
                            recognitions = RobotHardwareMap.tfod.getRecognitions();
                            // we were at 1, if 2 amd size 2, doesnt work
                            if(recognitions.size()>cur_index+1) cur_index++;
                            else cur_index = 0;
                            // for now, since we dont have tracking, the only way we go through recognitions
                            // is this way, that works perfectly only when we are staying still.
                            //TODO: TOP PAGE
                        }
                    } else
                        asyncScan(0.1, 1, 0,
                                  0, 0, 0,
                                  100);//TODO: set values
                }
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
    private void asyncScan(double x_increment, double x_max_pos, double x_min_pos,
                           double y_increment, double y_max_pos, double y_min_pos, int x_cycle_ms)
    {
        // Define class members
        double  x_position = (x_max_pos - x_min_pos) / 2; // Start at halfway position

        if (x_rampUp)
        {
            // Keep stepping up until we hit the max value.
            x_position += x_increment ;
            if (x_position >= x_max_pos )
            {
                x_position = x_max_pos;
                x_rampUp = !x_rampUp;   // Switch ramp direction
            }
        } else {
            // Keep stepping down until we hit the min value.
            x_position -= x_increment ;
            if (x_position <= x_min_pos )
            {
                x_position = x_min_pos;
                x_rampUp = !x_rampUp;  // Switch ramp direction
            }
        }


        double  y_position = (x_max_pos - x_min_pos) / 2; // Start at halfway position
        if (y_rampUp)
        {
            // Keep stepping up until we hit the max value.
            y_position += x_increment ;
            if (y_position >= y_max_pos )
            {
                y_position = y_max_pos;
                y_rampUp = !y_rampUp;   // Switch ramp direction
            }
        } else {
            // Keep stepping down until we hit the min value.
            y_position -= y_increment ;
            if (y_position <= y_min_pos )
            {
                y_position = y_min_pos;
                y_rampUp = !y_rampUp;  // Switch ramp direction
            }
        }
        setServoPosition(x_position, y_position);
        try {
            Thread.sleep(x_cycle_ms);
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }
    }



    /**
     *  aims and finds the coordinates of what's closest to a given object,
     *  This uses centroid tracking, and since the game elements are not substantially unique,
     *  low enough frame rate / high enough robot movement speed / similar object locations
     *  may give you the data of a wrong object
     * @param target_object the given object
     * @return
     */
    // TODO: !! implement Deep SORT to actually track targets and get the cords of the recognition !!
    private fieldObject calculateCords(Recognition target_object)
    {
        com.arcrobotics.ftclib.controller.PIDFController pidf_x =
                new PIDFController(DV_X_KP, DV_X_KI,
                        DV_X_KD, DV_X_KF);
        com.arcrobotics.ftclib.controller.PIDFController pidf_y =
                new PIDFController(DV_Y_KP, DV_Y_KI,
                        DV_Y_KD, DV_Y_KF);

        List<Recognition> recognitions;
        recognitions = RobotHardwareMap.tfod.getRecognitions();

        double x_change;
        double y_change;

        FlatCords target_cords = getCenterCords(target_object);

        pidf_x.setSetPoint(DV_CAMERA_X_OFFSET - DV_DIST_SENSOR_X_OFFSET);
        pidf_y.setSetPoint(0);

        pidf_x.setTolerance(DV_X_TOLERANCE);
        pidf_x.setTolerance(DV_Y_TOLERANCE);

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
                double current_dist = sqrt(current_x_dist * current_x_dist + current_y_dist * current_y_dist);
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
            fieldObject target_object_field_coordinates = new fieldObject();
            target_object_field_coordinates.x =
                    Navigation.currentPose.getX() + DV_X_OFFSET + sin(DV_x_angle) * (measured_dist+DV_DIST_SENSOR_Y_OFFSET)
                            + cos(DV_x_angle) * DV_DIST_SENSOR_X_OFFSET;
            target_object_field_coordinates.y =
                    Navigation.currentPose.getY() + DV_Y_OFFSET + sin(DV_y_angle) * measured_dist;
            // an accurate
            switch (target_object.getLabel())
            {
                case "Ball": target_object_field_coordinates.label = ObjectLabel.BALL;
                    break;
                case "Cube": target_object_field_coordinates.label = ObjectLabel.CUBE;
                    break;
                case "Duck": target_object_field_coordinates.label = ObjectLabel.DUCK;
                    break;
            }
            target_object_field_coordinates.state = ObjectState.ON_THE_FIELD;
            return target_object_field_coordinates;
        }
        return null;
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
}



// TODO: Stashed from earlier

/*

  }
        SetServoPosition(x_position, y_position);
        setServoPosition(x_position, y_position);
    }

    /**
     *  aims and finds what's closest to a given object,
     *  try aiming at that.This uses centroid tracking, and since the game elements are not substantially unique,
     *  low enough frame rate may give you the data of a wrong object
     * @param
     *  Also, it only measures the current position so the initial position might not
     *  be the result if it's moving
     *  @param

// TODO: !! implement Deep SORT to actually track targets and get the cords of the recognition !!
private Storage.fieldObjects calculateCords()
{
    // Check if we see anything

    List<Recognition> recognitions;
    Storage.fieldObjects target_object = new Storage.fieldObjects;

    @ -127,6 +130,7 @@ public class DepthVision implements Runnable
    double x_current_dif;
    double x_required_dif;
    double x_change;
    Recognition Recognition;

    com.arcrobotics.ftclib.controller.PIDFController pidf =
            new PIDFController(SubsystemConstants.DV_KP, SubsystemConstants.DV_KI, SubsystemConstants.DV_KD, SubsystemConstants.DV_KF);
    @ -136,8 +140,7 @@ public class DepthVision implements Runnable

    while(!pidf.atSetPoint() && (recognitions != null))
    {
        recognitions = RobotHardwareMap.tfod.getRecognitions();
        Recognition cur_recognition = get_closest_recognition();
        cur_recognition = getClosestRecognition();

        x_current_dif = SubsystemConstants.VIEW_CENTER_X-(cur_recognition.getLeft() + cur_recognition.getWidth());
        x_change = pidf.calculate(x_current_dif);
        @ -154,6 +157,11 @@ public class DepthVision implements Runnable
        else return null;


    }

    private Recognition void getClosestRecognition ()
    {

    }
 */