package client;

import org.json.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Deserializer {

    public static Object deserializeObject(String source) throws Exception {

        JSONObject jsonObject = new JSONObject(source);
        JSONArray object_list = jsonObject.getJSONArray("objects");
        Map object_map  = new HashMap();
        createInstances(object_map, object_list);
        assignFieldValues(object_map, object_list);

        return object_map.get(0);

    }

    private static void createInstances(Map object_map, JSONArray object_list) throws Exception {

        for (int i = 0; i < object_list.length(); i++) {
            JSONObject object_info = object_list.getJSONObject(i);
            Class object_class = Class.forName(object_info.getString("class"));
            Constructor constructor = object_class.getDeclaredConstructor();

            Object object_instance = constructor.newInstance();
            object_map.put(object_info.get("id"), object_instance);
        }

    }

    private static void assignFieldValues(Map object_map, JSONArray object_lis) throws Exception {

    }

}
