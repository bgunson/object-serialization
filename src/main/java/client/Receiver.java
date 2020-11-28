package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Receiver {
    public static void main(String[] args) throws Exception {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter a host name:");
        String hostname = keyboard.nextLine();
        System.out.println("Enter a port: ");
        int port = keyboard.nextInt();

        try  {
            Socket socket = new Socket(hostname, port);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (true) {
                System.out.println("Waiting for serialized object...");
                String line = reader.readLine();

                System.out.println("\nReceived a serialized object: ");
                System.out.println(line);
                System.out.println(" ");
                Object object = Deserializer.deserializeObject(line);

                System.out.println("Visualizing object...");
                Visualizer visualizer = new Visualizer();
                visualizer.visualize(object);
                System.out.println(" ");
            }


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
