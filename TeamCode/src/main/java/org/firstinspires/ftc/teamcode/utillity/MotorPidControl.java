package org.firstinspires.ftc.teamcode.utillity;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.drive.SubsystemConstants;

public class MotorPidControl // pid controller.
{
    static boolean updatePosition (int pos, DcMotor motor) // iterative
    {
         if(motor.getCurrentPosition()<(pos- SubsystemConstants.p_error))
         {
             motor.setPower(Math.min(((pos- SubsystemConstants.p_error)-motor.getCurrentPosition())* SubsystemConstants.p,1));
             return false;
         }
         if(motor.getCurrentPosition()>(pos+ SubsystemConstants.p_error))
         {
             motor.setPower(Math.max(((pos+ SubsystemConstants.p_error)-motor.getCurrentPosition())* SubsystemConstants.p,-1));
             return false;
         }
         return true; //we are within the accuracy 
    }

    static void runToPosition (int pos, DcMotor motor)
    {
        while(true)
        {
            if(motor.getCurrentPosition()<(pos- SubsystemConstants.p_error))
            {
                motor.setPower(Math.min(((pos- SubsystemConstants.p_error)-motor.getCurrentPosition())* SubsystemConstants.p,1));
            }
            else if(motor.getCurrentPosition()>(pos+ SubsystemConstants.p_error))
            {
                motor.setPower(Math.max(((pos+ SubsystemConstants.p_error)-motor.getCurrentPosition())* SubsystemConstants.p,-1));
            }
            else return;
             //we are within the accuracy
        }
    }
}

//TODO go rename some variables
