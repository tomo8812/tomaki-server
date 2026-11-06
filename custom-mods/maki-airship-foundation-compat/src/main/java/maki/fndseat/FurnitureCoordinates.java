package maki.fndseat;

import org.joml.Matrix4dc;
import org.joml.Vector3d;

public final class FurnitureCoordinates {
    private FurnitureCoordinates() {}

    public static Vector3d localPrevious(double x, double y, double z, boolean alreadyShipyard, Matrix4dc worldToShip) {
        Vector3d position = new Vector3d(x, y, z);
        return alreadyShipyard ? position : worldToShip.transformPosition(position);
    }
}
