package org.firstinspires.ftc.teamcode.robot_opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.RobotHardwareMap;
import org.firstinspires.ftc.teamcode.subsystems.depth_vision.DepthVision;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

//TODO: Uhm, I just thought... we are kinda screwed if we don't see a duck.

@Autonomous (name = "Test Auto")//STARTUP TIME, AFTER WE DROP THE BLOCK TIME, AND SPINNER POINT DELAY
public class TestAuto extends LinearOpMode
{
    RobotHardwareMap hw = new RobotHardwareMap();

    TrajectorySequence Blue;
    // set current position

    @Override
    public void runOpMode() throws InterruptedException
    {
        /////////////////////////// INITIALISATION ////////////////////////////
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();


        RobotHardwareMap hw = new RobotHardwareMap();
        hw.initVision();

        Runnable runnable = new DepthVision();
        Thread thread = new Thread(runnable);
        thread.start();

        if(isStopRequested()) return;
        waitForStart();

        Run();
    }

    private void Run()
    {
        DepthVision.threadTaskQueue.add(DepthVision.DepthVisionState.LOOK_FOR_GAME_ELEMENTS);
        while(DepthVision.threadTaskQueue.isEmpty()){}
        telemetry.addLine("found");
        telemetry.update();
    }


}






//////////////////////////////////////////////////////////////////








//TODO: add the ability to run auto during tele-op (break out of auto)
//TODO: !!! add the ability to change the settings in telemetry


// An auto that goes to block, then proceeds with its usual schedule !!!


//add a marker and delay to put the arm up? TODO: not sure if we need to lift the arm up at all actually



//////////////////////////////////////////////////////////////////////
