package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Lift // pid controller.
{
    static boolean updatePosition (int pos, DcMotor motor) // iterative
    {
         if(motor.getCurrentPosition()<(pos-Constants.p_accuracy)) 
         {
             motor.setPower(1);
             return false;
         } 
         if(motor.getCurrentPosition()>(pos-Constants.p_accuracy))
         {
             motor.setPower(-1);
             return false;
         }
         return true; //we are within the accuracy 
    }

    static void runToPosition (int pos, DcMotor motor)
    {
        while(true)
        {
            if(motor.getCurrentPosition()<(pos-Constants.p_accuracy))  motor.setPower(1);
            else if(motor.getCurrentPosition()>(pos-Constants.p_accuracy)) motor.setPower(-1);
            else return; //we are within the accuracy
        }
    }
}

//TODO go rename some variables
