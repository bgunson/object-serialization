package server;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

public class Serializer {

    public static String serializeObject(Object source) throws Exception{

        JSONArray object_list = new JSONArray();
        JSONObject json_container = new JSONObject();

        serializeHelper(source, object_list, new IdentityHashMap());

        json_container.put("objects", object_list);

        System.out.println("Serialized Object:");
        System.out.println(json_container.toString(4));
        return json_container.toString();
    }

    private static void serializeHelper(Object source, JSONArray object_list, Map object_tracking_map) throws Exception {

        if (source != null) {
            String object_id = Integer.toString(object_tracking_map.size());
            object_tracking_map.put(source, object_id);
            JSONObject object = new JSONObject();

            object.put("class", source.getClass().getName());
            object.put("id", object_id);
            object.put("type", "object");

            Field[] fields = source.getClass().getDeclaredFields();
            JSONArray json_fields = new JSONArray();

            for (Field field : fields) {
                // Check if field is an array
                field.setAccessible(true);

                if (field.getType().isArray()) {
                    // Add to object list
                    object_tracking_map.put(field.get(source), object_tracking_map.size());
                    //json_fields.put(serializeField(source, field, object_tracking_map));
                    object_list.put(serializeArray(source, field, object_list, object_tracking_map));

                } else if (!field.getType().isPrimitive()) {    // Check if field is some other object
                    // Check if the specified field has been serialized
                    if (!object_tracking_map.containsKey(field.get(source))) {
                        serializeHelper(field.get(source), object_list, object_tracking_map);
                    }
                    json_fields.put(serializeField(source, field, object_tracking_map));

                } else {    // Field must be primitive at this point
                    json_fields.put(serializeField(source, field, object_tracking_map));
                }
            }

            object.put("fields", json_fields);
            object_list.put(object);
        }

    }


    private static JSONObject serializeField(Object source, Field field, Map object_tracking_map) {
        field.setAccessible(true);
        JSONObject jsonField = new JSONObject();
        jsonField.put("name", field.getName());
        jsonField.put("declaringclass", field.getDeclaringClass().getSimpleName());

        try {
            if (field.getType().isPrimitive()) {
                jsonField.put("value", field.get(source));
            } else {
                Object ref = object_tracking_map.get(field.get(source));
                if (ref == null)
                    jsonField.put("reference", "null");
                else
                    jsonField.put("reference", ref);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return jsonField;

    }

    private static JSONObject serializeArray(Object source, Field field, JSONArray object_list, Map object_tracking_map)
        throws  Exception {

        JSONObject jsonArray = new JSONObject();
        field.setAccessible(true);
        jsonArray.put("class", field.getType().getSimpleName());

        jsonArray.put("id", object_tracking_map.get(field.get(source)));
        jsonArray.put("type", "array");

        int length = Array.getLength(field.get(source));
        jsonArray.put("length", length);

        JSONArray entries = new JSONArray();

        for (int i = 0; i < length; i++) {
            JSONObject entry = new JSONObject();
            Object e = Array.get(field.get(source), i);

            if (e == null)
                entry.put("reference", "null");
            else if (field.get(source).getClass().getComponentType().isPrimitive())
                entry.put("value", e);
            else {
                serializeHelper(e, object_list, object_tracking_map);
                entry.put("reference", object_tracking_map.get(e));
            }

            entries.put(entry);
        }
        jsonArray.put("entries", entries);

        return jsonArray;

    }

}
