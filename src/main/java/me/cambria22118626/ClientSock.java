package me.cambria22118626;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

/*public class ClientSock {
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

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(message);
        System.out.println("cozybear "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
        String response = in.readLine();
        System.out.println(response);
        return response;
    }
}*/

import javax.net.ssl.*;
import java.io.*;
import java.security.cert.X509Certificate;

public class ClientSock {
    private static ClientSock instance;
    private String host;
    private int port;
    private SSLSocket sslSocket; // Change to SSLSocket

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
        for (String i3 : soc.toString().split(":")) {
            if (i3.contains(".")) {
                this.host = i3;
            } else {
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
            // Create SSL context and socket factory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null; // Accept all issuers
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Trust all clients (for demonstration purposes, not secure)
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Trust all servers (for demonstration purposes, not secure)
                        }
                    }
            }, new java.security.SecureRandom());

            SSLSocketFactory factory = sslContext.getSocketFactory();
            this.sslSocket = (SSLSocket) factory.createSocket(this.host, this.port);
            sslSocket.startHandshake(); // Start SSL handshake

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String message) throws IOException {
        PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        out.println(message);
        System.out.println("Connected to " + sslSocket.getInetAddress().getHostAddress() + ":" + sslSocket.getPort());

        String response = in.readLine();
        System.out.println("Response: " + response);
        return response;
    }
}
