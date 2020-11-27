package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Receiver {
    public static void main(String[] args) throws Exception {

        String hostname = "localhost";
        int port = 6868;

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
                //Deserializer.deserializeObject(line);
            }


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
