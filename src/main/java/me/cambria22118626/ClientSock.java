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

    //singleton call function returns the instance
    /*
     * Singleton is an object which only has one instance
     * the object itself has a private constructor that can only be created using the static fuction inside itself
     * the fucntuon will only create a new object if an instance does not alreadt exist
     * it it does then it returns the address of teh instance that can then be used bu the code that needs it
     */
    public static ClientSock getInstance() {
        if (instance == null) {
            instance = new ClientSock();
        }
        return instance;
    }
    //deprecated but kept it in for show
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
    // new version of me.cambria2211826.ClientSock:setServerAddress that overloads the original definition
    public ClientSock setServerAddress(String ip, int port) {
        this.host = ip;
        this.port = port;
        start();
        return this;
    }
    //creates the Socket and
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
                            // Trust all clients, can be added later for final production release
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Trust all clients, can be added later for final production release
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

    //this function can be used outside this class in the style ClientSock.GetInstance().sendMessage({MESSAGE})
    // and it will return the responce from the server
    public String sendMessage(String message) throws IOException {
        this.start();
        PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        System.out.println(message);
        out.println(message);
        System.out.println("Connected to " + sslSocket.getInetAddress().getHostAddress() + ":" + sslSocket.getPort());

        String response = in.readLine();
        // if the server connection got disrupted before the final connection terminated bits came in usually are \n\r
        if(response == null) {
            throw new IOException("Connection closed by server");
        }
        System.out.println("Response: " + response);
        return response;
    }
}
