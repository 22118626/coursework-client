package me.cambria22118626;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import me.cambria22118626.Config;

import java.net.Socket;


public class Main {

    public static void main(String[] args) {

        String serverAddress = "localhost"; // Change this to the server's address if needed
        int port = 25566; // Change this to the server's port
        String filePath = "certificate.crt"; // Path to save the certificate

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to the server: " + serverAddress + " on port: " + port);

            // Create input and output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            InputStream in = socket.getInputStream();

            // Send a request message to the server
            String messageToSend = String.valueOf(0x0);
            out.println(messageToSend);
            System.out.println("Sent to server: " + messageToSend);


            // Create a buffer to hold the certificate data
            byte[] certificateBuffer = new byte[1302];
            int totalBytesRead = 0;

            // Read the actual certificate data
            while (totalBytesRead < 1302) {
                int bytesRead = in.read(certificateBuffer, totalBytesRead, 1302 - totalBytesRead);
                if (bytesRead == -1) {
                    // Connection was closed by the server
                    break;
                }
                totalBytesRead += bytesRead;
            }

            System.out.println("Total bytes read: " + totalBytesRead);

            // Write the certificate to a file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(certificateBuffer, 0, totalBytesRead);
                System.out.println("Certificate written to " + filePath);
            } catch (Exception e) {
                System.err.println("Failed to write certificate to file: " + e.getMessage());
            }

            // Close the streams and socket
            out.close();
            socket.close();
            System.out.println("Connection closed.");

        } catch (java.net.SocketException e) {
            // Handle connection reset error
            System.err.println("Connection reset error: " + e.getMessage());
        } catch (Exception e) {
            // Handle other potential exceptions
            e.printStackTrace();
        }



        Path path = new File(System.getenv("USERPROFILE")+"\\.coursework-config").toPath();
        boolean firstLaunch = !Files.exists(path);
        if (firstLaunch) {
            System.out.println("first launch");
            if(!path.toFile().mkdir()) {
                System.out.println("Couldn't create directory: " + path.toAbsolutePath()+"\nPlease create the folder");
            }
            new Greeter();
        }
        else {
            // Switch temporarely to Greeter for debugging
            //new Login();
            new Greeter();

        }
        System.out.println("Initializing thrusters!🎯📣\n");

    }
    private static int byteArrayToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8  |
                (bytes[3] & 0xFF);
    }
}
