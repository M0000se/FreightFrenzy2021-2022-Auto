package org.firstinspires.ftc.teamcode.drive.navigation.data_types;

//TODO: consider moving these enums and the fieldObjects class
public class fieldObject {
    public double x, y, z; //since Im not finding the height at which objects are located (yet), z is always 0
    public ObjectLabel label; // what the object is, eg a duck or a cube
    public ObjectState state; // for now, simply is it collected or on the field

    public fieldObject(double x, double y, double z, ObjectLabel label, ObjectState state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = label;
        this.state = state;
    }

    public fieldObject() {
    }

}
