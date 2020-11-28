package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class's structure is derived from https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
 */

public class Sender {

    public static void main(String[] args) throws Exception{

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter a port:");
        int port = keyboard.nextInt();

        try  {

            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server is listening on port " + port);

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
