package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorPidControl // pid controller.
{
    static boolean updatePosition (int pos, DcMotor motor) // iterative
    {
         if(motor.getCurrentPosition()<(pos- Storage.p_error))
         {
             motor.setPower(Math.min(((pos- Storage.p_error)-motor.getCurrentPosition())* Storage.p,1));
             return false;
         }
         if(motor.getCurrentPosition()>(pos+ Storage.p_error))
         {
             motor.setPower(Math.max(((pos+ Storage.p_error)-motor.getCurrentPosition())* Storage.p,-1));
             return false;
         }
         return true; //we are within the accuracy 
    }

    static void runToPosition (int pos, DcMotor motor)
    {
        while(true)
        {
            if(motor.getCurrentPosition()<(pos- Storage.p_error))
            {
                motor.setPower(Math.min(((pos- Storage.p_error)-motor.getCurrentPosition())* Storage.p,1));
            }
            else if(motor.getCurrentPosition()>(pos+ Storage.p_error))
            {
                motor.setPower(Math.max(((pos+ Storage.p_error)-motor.getCurrentPosition())* Storage.p,-1));
            }
            else return;
             //we are within the accuracy
        }
    }
}

//TODO go rename some variables
