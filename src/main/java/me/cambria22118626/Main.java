package me.cambria22118626;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import me.cambria22118626.Config;

public class Main {
    public static void main(String[] args) {
        new Config();
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
            new Login();
        }
        System.out.println("Initializing thrusters!🎯📣\n");

    }
}