package net.runelite.client.plugins.chatbox.dax_path;


import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.chatbox.minimal_json.JsonObject;

public class Point3D {


    private int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public JsonObject toJson() {
        JsonObject coordObj = new JsonObject();
        coordObj.add("x",x);
        coordObj.add("y",y);
        coordObj.add("z",z);
        return coordObj;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public static Point3D fromWorldPoint(WorldPoint worldPoint) {
        return new Point3D(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

}
