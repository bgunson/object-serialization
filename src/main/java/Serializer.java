import org.json.JSONArray;
import org.json.JSONObject;

public class Serializer {

    public static String serializeObject(Object source) {

        JSONObject json = new JSONObject();
        JSONArray objects = new JSONArray();
        objects.put("Object 1");
        objects.put("Object 2");
        json.put("objects", objects);

        return json.toString();
    }

}
