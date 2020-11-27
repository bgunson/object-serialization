package client;

import org.json.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;



public class Deserializer {

    /**
     * The base method for this class which starts the work of deserializing a given object
     * @param source the object which we are deserializing
     * @return the deserialized object
     * @throws Exception
     */
    public static Object deserializeObject(String source) throws Exception {

        JSONObject jsonObject = new JSONObject(source);
        JSONArray object_list = jsonObject.getJSONArray("objects");
        Map object_map  = new HashMap();
        createInstances(object_map, object_list);
        return object_map.get("0");

    }

    /**
     * This method will iterate through the JSONArray storing the serialized objects and create an instance accordingly.
     * Regular objects are handled differently from arrays and uses different methods.
     * @param object_map a map tracking the objects we have instantiated
     * @param object_list the JSONArray from the base JSONObject that contains the base object and its referenced objects
     *                    (if any)
     * @throws Exception
     */
    private static void createInstances(Map object_map, JSONArray object_list) throws Exception {

        for (int i = 0; i < object_list.length(); i++) {
            JSONObject object_info = object_list.getJSONObject(i);
            if (object_info.get("type").equals("array")) {
                Class type  = Class.forName(object_info.getString("class")).getComponentType();
                int length = object_info.getInt("length");
                Object array_instance = Array.newInstance(type, length);
                object_map.put(object_info.get("id"), array_instance);
            } else {    // It must be some object if not array
                Class object_class = Class.forName(object_info.getString("class"));
                Constructor constructor = object_class.getDeclaredConstructor();
                Object object_instance = constructor.newInstance();
                object_map.put(object_info.get("id"), object_instance);
            }
        }
        // We need to loop again to assign fields since a field may be a ref to an object that hasnt been added to
        // the map yet
        for (int i = 0; i < object_list.length(); i++) {
            JSONObject object_info = object_list.getJSONObject(i);
            if (object_info.get("type").equals("array"))
                assignArrayValues(object_map, object_info);
            else
                assignFieldValues(object_map, object_info);
        }

    }

    /**
     * This method will set a newly instantiated object's fields, if that object is not an array
     * @param object_map the map storing the created objects
     * @param object_info the specific JSONObject whose fields we are setting
     * @throws Exception
     */
    private static void assignFieldValues(Map object_map, JSONObject object_info) throws Exception {

        JSONArray object_fields = object_info.getJSONArray("fields");

        Object object = object_map.get(object_info.get("id"));

        for (int j = 0; j < object_fields.length(); j++) {

            JSONObject json_field = object_fields.getJSONObject(j);
            String field_name = json_field.getString("name");

            Field field = object.getClass().getDeclaredField(field_name);
            field.setAccessible(true);

            if (field.getType().isPrimitive()) {
                // Found a primitive type field to be set
                assignPrimitiveField(object, field, json_field);
            } else {    // Must be object since we have checked for arrays alreadys
                field.set(object, object_map.get(json_field.get("reference")));
            }
        }
    }

    /**
     * When a primitive type field is encountered, this method will try to set the field to the correct type from
     * a given json object
     * @param object The object whose field we are setting
     * @param field the particular field we are setting
     * @param json_field the JSONObject where we are getting the value to be set from
     * @throws Exception
     */
    private static void assignPrimitiveField(Object object, Field field, JSONObject json_field) throws Exception {
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

    /**
     * When an array type field or object is encountered, this method will create and set the array and its entries
     * for the object
     * @param object_map the map tracking the objects that have been deserialized
     * @param object_info the JSONObject where we are getting the array's values or references from
     * @throws Exception
     */
    private static void assignArrayValues(Map object_map, JSONObject object_info) throws Exception {
        Object array_instance = object_map.get(object_info.get("id"));
        JSONArray entries = object_info.getJSONArray("entries");

        int length = Array.getLength(array_instance);

        for (int i = 0; i < length; i++) {

            if (array_instance.getClass().getComponentType().isPrimitive()) {
                Array.set(array_instance, i, entries.getJSONObject(i).get("value"));
            } else {
                if (entries.getJSONObject(i).get("reference").equals("null"))
                    Array.set(array_instance, i, null);
                else
                    Array.set(array_instance, i, object_map.get(entries.getJSONObject(i).get("reference")));
            }

        }

    }


}
