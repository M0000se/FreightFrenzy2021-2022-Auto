package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;

import java.util.List;

public class Vision
{
    static public int getElementPosition (SampleMecanumDrive sampleMecanumDrive)
    {
        if (RobotHardwareMap.tfod != null) {
            RobotHardwareMap.tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            RobotHardwareMap.tfod.setZoom(1, 16.0 / 9.0);
        }

        /** Wait for the game to begin */

        FtcDashboard.getInstance().startCameraStream(RobotHardwareMap.vuforia, 0);

        for (; ; )
        {
            if (RobotHardwareMap.tfod != null)
            {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = RobotHardwareMap.tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {

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
                            else return 1; //TODO: test*/

                        } else isDuckDetected = false;

                    }
                }
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
}
