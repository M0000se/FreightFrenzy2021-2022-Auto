package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorPidControl // pid controller.
{
    public static int P_ERROR = 50; // max allowed motor position error
    public static final double p = 0.001;

    static boolean updatePosition (int pos, DcMotor motor) // iterative
    {
         if(motor.getCurrentPosition()<(pos- P_ERROR))
         {
             motor.setPower(Math.min(((pos- P_ERROR)-motor.getCurrentPosition())* p,1));
             return false;
         }
         if(motor.getCurrentPosition()>(pos+ P_ERROR))
         {
             motor.setPower(Math.max(((pos+ P_ERROR)-motor.getCurrentPosition())* p,-1));
             return false;
         }
         return true; //we are within the accuracy 
    }

    static void runToPosition (int pos, DcMotor motor)
    {
        while(true)
        {
            if(motor.getCurrentPosition()<(pos- P_ERROR))
            {
                motor.setPower(Math.min(((pos- P_ERROR)-motor.getCurrentPosition())* p,1));
            }
            else if(motor.getCurrentPosition()>(pos+ P_ERROR))
            {
                motor.setPower(Math.max(((pos+ P_ERROR)-motor.getCurrentPosition())* p,-1));
            }
            else return;
             //we are within the accuracy
        }
    }
}

//TODO go rename some variables
