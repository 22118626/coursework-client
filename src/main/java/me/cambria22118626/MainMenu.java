package me.cambria22118626;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainMenu extends Window{
    public MainMenu() {
        super("MainMenu");

        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Height = (int) Math.floor(scrSize.getHeight() * 0.85);
        this.Width = (int) Math.floor(scrSize.getWidth() * 0.85);

        if(Files.exists(Path.of(Config.getFile()+"\\logo.png"))) {
            ImageIcon logoimg = new ImageIcon(System.getProperty("USERPROFILE") + "\\logo.png");
            JLabel logo = new JLabel(logoimg);
            logo.setSize((int)Math.floor(this.Width*0.2), (int)Math.floor(this.Height*0.2));
            panel.add(logo);
        }


        bgColour = Config.getInstance().windowThemingColours.get("MainBG");
        run();

    }
}
