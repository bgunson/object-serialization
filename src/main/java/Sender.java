import org.json.JSONObject;
import org.json.JSONArray;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Sender {

    JSONObject json = new JSONObject();

    public String encode(Object[] objects) {
        JSONArray jsonObjArr = new JSONArray();

        // For each Object obj in objects add serialized string to jsonObjArr

        // Then add arr to json object; return json.toString()

        try {
            Class<?> clazz = Class.forName("Serializer");

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

        Sender sender = new Sender();
        String object = sender.encode(null);
        System.out.println(object);

        int port = 6868;

        try  {

            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                System.out.println("New client connected");

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(object);
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

}
