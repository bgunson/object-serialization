package server;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class Serializer {

    public static String serializeObject(Object source) {

        JSONObject jsonContainer = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject object = new JSONObject();
        JSONArray fields = new JSONArray();

        object.put("id", source.hashCode());
        object.put("type", "object");
        object.put("class", source.getClass().getName());

        Field[] fs = source.getClass().getDeclaredFields();
        for (Field field : fs) {
            JSONObject jsonField = new JSONObject();
            jsonField.put("name", field.getName());
            jsonField.put("declaringclass", field.getDeclaringClass().getSimpleName());
            try {
                jsonField.put("value", field.get(source));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            fields.put(jsonField);
        }

        jArr.put(object);
        object.put("fields", fields);
        jsonContainer.put("objects", jArr);

        System.out.println("Serialized Object: ");
        System.out.println(jsonContainer.toString(4));

        return jsonContainer.toString();
    }

}
