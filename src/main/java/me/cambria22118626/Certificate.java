package me.cambria22118626;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Certificate {

    String filePath;

    public Certificate() {
        filePath = "certificate.crt";
    }

    boolean createCertificateFromServer(String serverAddress, int port) {
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
                System.err.println("Failed to write certificate to file("+this.filePath+"): " + e.getMessage());
            }

            // Close the streams and socket
            out.close();
            socket.close();
            System.out.println("Connection closed.");

            return true;

        } catch (java.net.SocketException e) {
            // Handle connection reset error
            System.err.println("Connection reset error: " + e.getMessage());
        } catch (Exception e) {
            // Handle other potential exceptions
            e.printStackTrace();
        }
        return false;
    }

}
