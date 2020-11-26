package server;

import org.json.JSONObject;
import org.json.JSONArray;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Sender {

    public static void main(String[] args) throws Exception{

        int port = 6868;

        try  {

            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);

            Scanner keyboard = new Scanner(System.in);
            boolean finished = false;

            Socket socket = serverSocket.accept();

            System.out.println("New client connected");

            while (!finished) {

                ObjectCreator createObjs = new ObjectCreator();
                Object obj = createObjs.createObject();

                // Serialize the object to a JSON String
                System.out.println("Serializing the object...\n");
                String jsonString = Serializer.serializeObject(obj);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(jsonString);

                System.out.println("\nDo you wish to serialize another object? (y/n)");
                String input = keyboard.nextLine();
                if (input.equals("n")) {
                    finished = true;
                }

            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

}
