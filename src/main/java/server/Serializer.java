package server;

import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

public class Serializer {

    /**
     * This is the base method for the class. When called the method will take an object and begin serializing it to
     * a JSONObject as defined by the library org.json
     * @param source the object we are serializing
     * @return a string representing the JSONObject which was created
     */
    public static String serializeObject(Object source) throws Exception{

        JSONArray object_list = new JSONArray();
        JSONObject json_container = new JSONObject();

        serializeHelper(source, object_list, new IdentityHashMap());

        json_container.put("objects", object_list);

        System.out.println("Serialized Object:");
        System.out.println(json_container.toString(4));
        return json_container.toString();
    }

    /**
     * Helper method that can serialize other objects encountered while serializing the source object. The method
     * does not handle array objects.
     * @param source the object we are serializing
     * @param object_list a JSONArray where we are storing all encountered objects, later put in a json container object
     * @param object_tracking_map a map where we can store objects encountered and later check if an object has been
     *                            serialized or not already
     */
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
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    field.setAccessible(true);

                    if (field.getType().isArray()) {
                        // Add to object list
                        object_tracking_map.put(field.get(source), object_tracking_map.size());
                        json_fields.put(serializeField(source, field, object_tracking_map));
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
            }

            object.put("fields", json_fields);
            object_list.put(object);
        }

    }


    /**
     * This method creates a separate JSONObject for a given objects field
     * @param source the object whose field we are serializing
     * @param field the field we are serializing
     * @param object_tracking_map the map tracking encounters objects
     * @return a JSONObject representing the serialized field
     */
    private static JSONObject serializeField(Object source, Field field, Map object_tracking_map) throws  Exception {
        field.setAccessible(true);
        JSONObject jsonField = new JSONObject();
        jsonField.put("name", field.getName());
        jsonField.put("declaringclass", field.getDeclaringClass().getSimpleName());


        if (field.getType().isPrimitive()) {
            jsonField.put("value", field.get(source));
        } else {
            Object ref = object_tracking_map.get(field.get(source));
            if (ref == null)
                jsonField.put("reference", "null");
            else
                jsonField.put("reference", ref);

        }
        return jsonField;

    }

    /**
     * When an array object is encountered, this method will serialize it and its entries as a JSONObject
     * @param source the source object which contains this array field
     * @param field the field which is an array type
     * @param object_list the JSONArray storing serialized objects which we can add the array field to
     * @param object_tracking_map the map tracking all encountered objects
     * @return the JSONObject representing the serialized array
     */
    private static JSONObject serializeArray(Object source, Field field, JSONArray object_list, Map object_tracking_map)
        throws  Exception {

        JSONObject jsonArray = new JSONObject();
        field.setAccessible(true);
        jsonArray.put("class", field.get(source).getClass().getName());

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
