package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Lift_iterative // For teleop pid controller. AUTO USES THE LINEAR setTargetPosition
{
    static void updatePosition (int pos, DcMotor motor)
    {
        if(motor.getCurrentPosition()<(pos-Constants.p_accuracy))motor.setPower(1);
        if(motor.getCurrentPosition()>(pos-Constants.p_accuracy))motor.setPower(-1);
    }
}

//TODO go rename some variables
