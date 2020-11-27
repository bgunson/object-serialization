package client;

import org.json.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Deserializer {

    public static Object deserializeObject(String source) throws Exception {

        JSONObject jsonObject = new JSONObject(source);
        JSONArray object_list = jsonObject.getJSONArray("objects");
        Map object_map  = new HashMap();
        createInstances(object_map, object_list);
        assignFieldValues(object_map, object_list);

        return object_map.get("0");

    }

    private static void createInstances(Map object_map, JSONArray object_list) throws Exception {

        for (int i = 0; i < object_list.length(); i++) {
            JSONObject object_info = object_list.getJSONObject(i);
            if (object_info.get("type").equals("array"))
                System.out.println("We found an array");
            else {
                Class object_class = Class.forName(object_info.getString("class"));
                Constructor constructor = object_class.getDeclaredConstructor();
                Object object_instance = constructor.newInstance();
                object_map.put(object_info.get("id"), object_instance);
            }
        }

    }

    private static void assignFieldValues(Map object_map, JSONArray object_list) throws Exception {

        for (int i = 0; i < object_list.length(); i++) {
            JSONObject object_info = object_list.getJSONObject(i);
            JSONArray object_fields = object_info.getJSONArray("fields");

            Object object = object_map.get(object_info.get("id"));

            for (int j = 0; j < object_fields.length(); j++) {

                JSONObject json_field = object_fields.getJSONObject(j);
                String field_name = json_field.getString("name");

                Field field = object.getClass().getDeclaredField(field_name);
                field.setAccessible(true);

                if (field.getType().isPrimitive()) {
                    // Found a primitive type field to be set
                    assignPrimitiveFields(object, field, json_field);
                } else if (field.getType().isArray()) {
                    assignArrayField();
                } else {
                    field.set(object, object_map.get(json_field.get("reference")));
                }


            }
        }

    }

    private static void assignPrimitiveFields(Object object, Field field, JSONObject json_field) throws Exception {
        Class field_type = field.getType();
        if (field_type.equals(int.class)) {
            field.set(object, json_field.getInt("value"));
        } else if (field_type.equals(boolean.class)) {
            field.setBoolean(object, json_field.getBoolean("value"));
        } else if (field_type.equals(float.class)) {
            field.setFloat(object, json_field.getFloat("value"));
        } else if (field_type.equals(long.class)) {
            field.setLong(object, json_field.getLong("value"));
        } else if (field_type.equals(double.class)) {
            field.setDouble(object, json_field.getDouble("value"));
        }

    }

    private static void assignArrayField() throws Exception {
    }


}
