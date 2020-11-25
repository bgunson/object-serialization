package server;

import org.json.JSONObject;
import org.json.JSONArray;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender {

    public static String encode(Object object) {
        JSONObject json = new JSONObject();
        JSONArray jsonObjArr = new JSONArray();

        // For each Object obj in objects add serialized string to jsonObjArr

        // Then add arr to json object; return json.toString()

        try {
            Class<?> clazz = Class.forName("server.Serializer");

            Method method = clazz.getMethod("serializeObject", Object.class);

            Object test = method.invoke(null, "test");

            return test.toString();
            //System.out.println(test.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public static void main(String[] args) {

        int port = 6868;

        try  {

            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                ObjectCreator createObjs = new ObjectCreator();
                Object obj = createObjs.createObject();

                // Serialize the object to a JSON String
                System.out.println("Serializing the object...\n");
                String jsonString = Serializer.serializeObject(obj);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(jsonString);
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

}
