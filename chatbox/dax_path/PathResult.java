package net.runelite.client.plugins.chatbox.dax_path;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.chatbox.minimal_json.JsonObject;
import net.runelite.client.plugins.chatbox.minimal_json.JsonValue;
import net.runelite.client.plugins.chatbox.minimal_json.Json;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PathResult {

    private PathStatus pathStatus;

    private List<Point3D> path;

    private int cost;

    private PathResult() {

    }

    public PathResult(PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public PathResult(PathStatus pathStatus, List<Point3D> path, int cost) {
        this.pathStatus = pathStatus;
        this.path = path;
        this.cost = cost;
    }

    public PathStatus getPathStatus() {
        return pathStatus;
    }

    public void setPathStatus(PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public List<Point3D> getPath() {
        return path;
    }

    public void setPath(List<Point3D> path) {
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathResult that = (PathResult) o;
        return cost == that.cost &&
                pathStatus == that.pathStatus &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathStatus, path, cost);
    }

    public static PathResult fromJson(String string) {
        AtomicReference<PathStatus> status = new AtomicReference<>(PathStatus.SUCCESS);
        List<Point3D> points = new ArrayList<>();
        int cost = 0;
        JsonObject responseObj = Json.parse(string).asObject();

        if (responseObj.get("pathStatus") != null) {
            Arrays.stream(PathStatus.values()).filter(s -> responseObj.get("pathStatus").asString().equals(s.name())).findFirst().ifPresent(status::set);
        }

        if (responseObj.get("cost") != null) {
            cost = responseObj.getInt("cost",-1);
        }

        if (responseObj.get("path") != null) {
            for (JsonValue pointVal : responseObj.get("path").asArray()) {
                JsonObject pointObj = pointVal.asObject();
                int x = pointObj.getInt("x",-1);
                int y = pointObj.getInt("y",-1);
                int z = pointObj.getInt("z",-1);
                points.add(new Point3D(x,y,z));
            }
        }
        return new PathResult(status.get(),points,cost);
    }

    // assumes taken directly from api i.e. 1 tile distance between tiles
    public WorldPoint[] toRunelitePath() {
        if (path == null || path.isEmpty()) return new WorldPoint[]{};
        List<WorldPoint> tiles = new ArrayList<>();
//        Point3D last = path.get(path.size()-1);
//        for (int i = 0; i < path.size(); i+=3) {
//            Point3D temp = path.get(i);
//            tiles.add(new WorldPoint(temp.getX(),temp.getY(),temp.getZ()));
//        }
//        tiles.add(new WorldPoint(last.getX(),last.getY(),last.getZ()));
        path.forEach(temp -> tiles.add(new WorldPoint(temp.getX(),temp.getY(),temp.getZ())));
        return tiles.toArray(new WorldPoint[0]);
    }
}
