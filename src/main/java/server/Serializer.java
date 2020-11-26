package server;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class Serializer {

    public static String serializeObject(Object source) {

        JSONObject jsonContainer = new JSONObject();
        JSONArray objArray = new JSONArray();
        JSONObject object = new JSONObject();

        object.put("id", source.hashCode());
        object.put("type", "object");
        object.put("class", source.getClass().getName());

        // Serialize source's fields
        Field[] fs = source.getClass().getDeclaredFields();
        JSONArray fields = new JSONArray();
        for (Field field : fs) {
            // Handle an array field
            if (field.getType().isArray()) {
                fields.put(serializeField(source, field));
                objArray.put(serializeArray(source, field));

            } else if (field.getType().equals(source.getClass())) {

                try {
                    fields.put(serializeField(source, field));
                    objArray.put(objectToJson(field.get(source)));
                } catch (IllegalAccessException iae) {
                    System.out.println("Could not serialize ");
                }

            } else {
                fields.put(serializeField(source, field));
            }
        }

        object.put("fields", fields);
        objArray.put(object);
        jsonContainer.put("objects", objArray);

        System.out.println("Serialized Object: ");
        System.out.println(jsonContainer.toString(4));

        return jsonContainer.toString();
    }

    private static JSONObject objectToJson(Object source) {

        JSONObject objectJson = new JSONObject();

        objectJson.put("id", source.hashCode());
        objectJson.put("type", "object");
        objectJson.put("class", source.getClass().getName());

        // Serialize source's fields
        Field[] fs = source.getClass().getDeclaredFields();
        JSONArray fields = new JSONArray();
        for (Field field : fs) {
            JSONObject aField = serializeField(source, field);
            //System.out.println(aField.toString(4));
            fields.put(aField);

        }

        objectJson.put("fields", fields);

        return objectJson;
    }

    private static JSONObject serializeField(Object source, Field field) {

        JSONObject jsonField = new JSONObject();
        jsonField.put("name", field.getName());
        jsonField.put("declaringclass", field.getDeclaringClass().getSimpleName());

        try {
            if (field.getType().isPrimitive())
                jsonField.put("value", field.get(source));
            else
                jsonField.put("reference", field.get(source).hashCode());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return jsonField;

    }

    private static JSONObject serializeArray(Object source, Field field) {
        JSONObject jsonArray = new JSONObject();

        jsonArray.put("class", field.getType().getSimpleName());
        try {
            jsonArray.put("id", field.get(source).hashCode());
            jsonArray.put("type", "array");

            int length = Array.getLength(field.get(source));
            jsonArray.put("length", length);

            JSONArray entries = new JSONArray();

            for (int i = 0; i < length; i++) {
                JSONObject entry = new JSONObject();
                entry.put("value", Array.get(field.get(source), i));
                entries.put(entry);
            }
            jsonArray.put("entries", entries);

        }  catch (IllegalAccessException iae) {
            System.out.println("Serialization error: could not get length of array field...");
        }


        return jsonArray;


    }

}
