package me.cambria22118626;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import me.cambria22118626.Config;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static Map<String, Object> persistMemJson = new HashMap<String, Object>();

    public static void main(String[] args) {


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
