package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;

public class Color_sensor
{
    static boolean see_rgb (int r, int g, int b, ColorSensor color_sensor)
    // does the color_sensor see the color? scaling function to compare the real
    // rgb value of the sensor and a given rgb under a standard accuracy
    {
        double scalar = color_sensor.red() / r;
        double sensor_scaled_r = color_sensor.red() / scalar;
        double sensor_scaled_g = color_sensor.green() / scalar;
        double sensor_scaled_b = color_sensor.blue() / scalar;

        //checks
        if(!((r+Constants.color_error)>sensor_scaled_r) && (sensor_scaled_r>(r-Constants.color_error)))
            return false;
        if(!((g+Constants.color_error)>sensor_scaled_g) && (sensor_scaled_g>(g-Constants.color_error)))
            return false;
        if(!((b+Constants.color_error)>sensor_scaled_b) && (sensor_scaled_b>(b-Constants.color_error)))
            return false;

        // it does indeed, see the color
        return true;
    }

    static boolean see_rgb (int r, int g, int b, ColorSensor color_sensor, double accuracy)
    // does the color_sensor see the color? scaling function to compare the real
    // rgb value of the sensor and a given rgb under a set accuracy
    {
        double scalar = color_sensor.red() / r;
        double sensor_scaled_r = color_sensor.red() / scalar;
        double sensor_scaled_g = color_sensor.green() / scalar;
        double sensor_scaled_b = color_sensor.blue() / scalar;

        //checks
        if(!((r+Constants.color_error)>sensor_scaled_r) && (sensor_scaled_r>(r-Constants.color_error)))
            return false;
        if(!((g+Constants.color_error)>sensor_scaled_g) && (sensor_scaled_g>(g-Constants.color_error)))
            return false;
        if(!((b+Constants.color_error)>sensor_scaled_b) && (sensor_scaled_b>(b-Constants.color_error)))
            return false;

        // it does indeed, see the color
        return true;
    }

    static boolean see_freight (ColorSensor color_sensor) {
    // does the color_sensor see freight? scaling utility to compare the real
    // rgb value of the sensor and the rgb of freight under a set accuracy
        return(see_rgb(255, 165, 0, color_sensor ) || see_rgb(255, 255, 255, color_sensor));
    }
}
