package org.firstinspires.ftc.teamcode.subsystems;

public class ColorSensor
{
    public static final double COLOR_ERROR = 90; // max allowed color sensor rgb error

    static boolean see_rgb (int r, int g, int b, com.qualcomm.robotcore.hardware.ColorSensor color_sensor)
    // does the color_sensor see the color? scaling function to compare the real
    // rgb value of the sensor and a given rgb under a standard accuracy
    {
        double scalar = color_sensor.red() / 255;// TODO change
        double sensor_scaled_r = color_sensor.red() / scalar;
        double sensor_scaled_g = color_sensor.green() / scalar;
        double sensor_scaled_b = color_sensor.blue() / scalar;

        //checks
        if(!((r+ COLOR_ERROR)>sensor_scaled_r) && (sensor_scaled_r>(r- COLOR_ERROR)))
            return false;
        if(!((g+ COLOR_ERROR)>sensor_scaled_g) && (sensor_scaled_g>(g- COLOR_ERROR)))
            return false;
        if(!((b+ COLOR_ERROR)>sensor_scaled_b) && (sensor_scaled_b>(b- COLOR_ERROR)))
            return false;

        // it does indeed, see the color
        return true;
    }

    static boolean see_rgb (int r, int g, int b, com.qualcomm.robotcore.hardware.ColorSensor color_sensor, double accuracy)
    // does the color_sensor see the color? scaling function to compare the real
    // rgb value of the sensor and a given rgb under a set accuracy
    {
        double scalar = color_sensor.red() / r;
        double sensor_scaled_r = color_sensor.red() / scalar;
        double sensor_scaled_g = color_sensor.green() / scalar;
        double sensor_scaled_b = color_sensor.blue() / scalar;



        //checks
        if(!((r+ COLOR_ERROR)>sensor_scaled_r) && (sensor_scaled_r>(r- COLOR_ERROR)))
            return false;
        if(!((g+ COLOR_ERROR)>sensor_scaled_g) && (sensor_scaled_g>(g- COLOR_ERROR)))
            return false;
        if(!((b+ COLOR_ERROR)>sensor_scaled_b) && (sensor_scaled_b>(b- COLOR_ERROR)))
            return false;

        // it does indeed, see the color
        return true;
    }

    static boolean see_freight (com.qualcomm.robotcore.hardware.ColorSensor color_sensor) {
    // does the color_sensor see freight? scaling utility to compare the real
    // rgb value of the sensor and the rgb of freight under a set accuracy
        return(see_rgb(300, 400, 200, color_sensor ) || see_rgb(270, 320, 100, color_sensor) || see_rgb(350, 550, 500, color_sensor));
    }
}
