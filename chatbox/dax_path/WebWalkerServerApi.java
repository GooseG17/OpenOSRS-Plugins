package net.runelite.client.plugins.chatbox.dax_path;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.chatbox.PathFinder;
import net.runelite.client.plugins.chatbox.PathFinderConfig;
import net.runelite.client.plugins.chatbox.minimal_json.Json;
import net.runelite.client.plugins.chatbox.minimal_json.JsonObject;
import net.runelite.client.plugins.chatbox.minimal_json.JsonValue;
import net.runelite.client.plugins.chatbox.minimal_json.ParseException;

import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

@Singleton
public class WebWalkerServerApi {
    private Logger log = Logger.getLogger("dax_path.WebWalkerServerApi");

    private Client client;
    private PathFinder pathFinder;
    private PathFinderConfig config;

    @Inject
    public WebWalkerServerApi(Client client, PathFinder pathFinder, PathFinderConfig config) {
        this.client = client;
        this.pathFinder = pathFinder;
        this.config = config;
    }

    private static final String WALKER_ENDPOINT = "https://api.dax.cloud", TEST_ENDPOINT = "http://localhost:8080";

    private static final String
            GENERATE_PATH = "/walker/generatePath",
            GENERATE_BANK_PATH = "/walker/generateBankPath";


    private HashMap<String, String> cache = new HashMap<>();


    public PathResult getBankPath(Point3D start, RunescapeBank bank) {
        JsonObject pathRequest = new JsonObject();

        pathRequest.add("start", start.toJson());

        if (bank != null) {
            pathRequest.add("bank",bank.name());
        }
        try {
            return parseResult(post(pathRequest, WALKER_ENDPOINT + GENERATE_BANK_PATH));
        } catch (IOException e) {
            log.info("Is server down? Spam dax.");
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }
    }

    private PathResult parseResult(ServerResponse serverResponse) {
        if (!serverResponse.isSuccess()) {
            JsonValue jsonValue  = null;
            try{
                jsonValue = Json.parse(serverResponse.getContents());
            } catch(Exception | Error e){
                jsonValue = Json.NULL;
            }
            if (!jsonValue.isNull()) {
                log.info("[Error] " + jsonValue.asObject().getString(
                        "message",
                        "Could not generate path: " + serverResponse.getContents()
                ));
            }

            switch (serverResponse.getCode()) {
                case 429:
                    return new PathResult(PathStatus.RATE_LIMIT_EXCEEDED);
                case 400:
                case 401:
                case 404:
                    return new PathResult(PathStatus.INVALID_CREDENTIALS);
            }
        }

        PathResult pathResult;
        JsonObject jsonObject;
        try {
            jsonObject = Json.parse(serverResponse.getContents()).asObject();
        } catch (ParseException e) {
            pathResult = new PathResult(PathStatus.UNKNOWN);
            log.info("Error: " + pathResult.getPathStatus());
            return pathResult;
        }

        pathResult = PathResult.fromJson(jsonObject.toString());
        log.info("Response: " + pathResult.getPathStatus() + " Cost: " + pathResult.getCost());
        return pathResult;
    }

    private ServerResponse post(JsonObject jsonObject, String endpoint) throws IOException {
        log.info("Generating path: " + jsonObject.toString());
        if (cache.containsKey(jsonObject.toString())) {
            return new ServerResponse(true, HttpURLConnection.HTTP_OK, cache.get(jsonObject.toString()));
        }

        URL myurl = new URL(endpoint);
        HttpURLConnection connection = (HttpsURLConnection) myurl.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);

        connection.setRequestProperty("Method", "POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");


        connection.setRequestProperty("key", config.daxKey());
        connection.setRequestProperty("secret", config.daxSecret());

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return new ServerResponse(false, connection.getResponseCode(), IOHelper.readInputStream(connection.getErrorStream()));
        }

        String contents = IOHelper.readInputStream(connection.getInputStream());
        cache.put(jsonObject.toString(), contents);
        return new ServerResponse(true, HttpURLConnection.HTTP_OK, contents);
    }


    public static void main(String[] args) {
////        Tile[] result = api.getBankPath(new Point3D(3216, 3422, 0),RunescapeBank.GNOME_BANK).toRsbotPath();
////        for (Tile point3D : result) {
////            System.out.println(point3D);
////        }
////        System.out.println(result[result.length-1]);
//
//        for (RunescapeBank value : RunescapeBank.values()) {
//            WorldPoint[] result = api.getBankPath(new Point3D(3216, 3422, 0),value).toRunelitePath();
////            for (Tile point3D : result) {
////                System.out.println(point3D);
////            }
//            WorldPoint t = result[result.length-1];
//            System.out.println("AAAA "+value+"("+t.getX()+","+t.getY()+","+t.getPlane()+"),");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public WorldPoint[] getDaxBankPath(Client client) {
        Player p = client.getLocalPlayer();
        if (p == null)  return new WorldPoint[]{};

        Point3D point = new Point3D(p.getWorldLocation().getX(), p.getWorldLocation().getY(), p.getWorldLocation().getPlane());

        WorldPoint[] shortest = null;
        RunescapeBank nearestBank = RunescapeBank.AL_KHARID;
        for (RunescapeBank value : RunescapeBank.sortShortest(p.getWorldLocation())) {
            WorldPoint[] result = this.getBankPath(point,value).toRunelitePath();
            if (result.length == 0) continue;
            if (shortest == null) {
                shortest = result;
                nearestBank = value;
            } else if (result.length < shortest.length) {
                shortest = result;
                nearestBank = value;
            }
            if (shortest.length < 100) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (shortest != null) log.info("Dax path to " + nearestBank + " " + shortest.length);
        return shortest == null ? new WorldPoint[]{} : shortest;
    }

    public WorldPoint[] getPath(WorldPoint start, WorldPoint end) {
        return getPath(Point3D.fromWorldPoint(start), Point3D.fromWorldPoint(end));
    }


    public WorldPoint[] getPath(WorldPoint end) {
        Player p = client.getLocalPlayer();
        if (p == null) return new WorldPoint[]{};
        return getPath(Point3D.fromWorldPoint(p.getWorldLocation()), Point3D.fromWorldPoint(end));
    }

    public WorldPoint[] getPath(Point3D start, Point3D end) {
        JsonObject pathRequest = new JsonObject();
        pathRequest.add("start", start.toJson());
        pathRequest.add("end", end.toJson());

        try {
            return parseResult(post(pathRequest, WALKER_ENDPOINT + GENERATE_PATH)).toRunelitePath();
        } catch (IOException e) {
            log.info("Is server down? Spam dax.");
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER).toRunelitePath();
        }
    }
}
