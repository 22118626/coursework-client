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
    private String host;
    private int port;
    private Socket socket;



    private ClientSock() {
        this.host = "127.0.0.1";
        this.port = 25565;
    }

    public static ClientSock getInstance() {
        if (instance == null) {
            instance = new ClientSock();
        }
        return instance;
    }

    @Deprecated
    public void setServerAddress(SocketAddress soc) throws RuntimeException {
        for(String i3 : soc.toString().split(":")) {
            if(i3.contains(".")) {
                this.host = i3;
            }
            else {
                this.port = Integer.getInteger(i3);
            }
        }
    }

    public ClientSock setServerAddress(String ip, int port) {
        this.host = ip;
        this.port = port;
        start();
        return this;
    }


    private void start() {
        try {
            this.socket = new Socket(this.host, this.port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String message) throws IOException {
        System.out.println(host.contains("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!$)|$)){4}$"));
        System.out.println(port > 1024 && port < 49151);
        if (this.socket == null || socket.isClosed() || !host.contains("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!$)|$)){4}$")) {
            return null;
        }

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(message);
        String response = in.readLine();
        System.out.println(response);
        return response;
    }
}
