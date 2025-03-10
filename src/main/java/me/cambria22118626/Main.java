package me.cambria22118626;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class Main {

    //Global Definitions
    public static Map<String, Object> persistMemJson = new HashMap<String, Object>();
    public static Vector<Map<String, Object>> tables = new Vector<>();

    //start of the program \/ \/
    public static void main(String[] args) {

        //this code looks for the file in the userProfile environment directory usualy located in C:\Users\{USER}\ and is individual to the accoun logged in to the computer
        Path path = new File(System.getenv("USERPROFILE")+"\\.coursework-config").toPath();
        boolean firstLaunch = !Files.exists(path);
        //if the specified file exists then the greeter menu is displayed by calling me.cambria22118626.Greeter object
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
}
