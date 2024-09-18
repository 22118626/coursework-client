package me.cambria22118626;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSock {
    private static ClientSock instance;
    private SocketAddress serverAddress;


    private ClientSock() {
        this.serverAddress = new InetSocketAddress("0.0.0.0", 42069);
    }

    public static ClientSock getInstance() {
        if (instance == null) {
            instance = new ClientSock();
        }
        return instance;
    }

    public void setServerAddress(SocketAddress soc) {
        this.serverAddress = soc;
    }

    public void setServerAddress(String ip, int port) {
        this.serverAddress = new InetSocketAddress(ip, port);
        start();
    }

    private void start() {
        try {
            PrintWriter out;
            BufferedReader in;

            Socket clientSocket = new Socket("10.0.4.69", 25565);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Hello World");
            String response = in.readLine();
            System.out.println(response);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
