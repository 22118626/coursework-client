package me.cambria22118626;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path path = new File(System.getenv("USERPROFILE")+"\\.coursework-config").toPath();
        boolean firstLaunch = Files.exists(path);
        if (firstLaunch) {
            if(!path.toFile().mkdir()) {
                System.out.println("Couldn't create directory: " + path.toAbsolutePath()+"\nPlease create the folder");
            }
            new Greeter();
        }
        System.out.print("Initializing thrusters!🎯📣\n");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}