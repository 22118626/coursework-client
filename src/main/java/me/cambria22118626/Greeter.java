package me.cambria22118626;

import javax.swing.*;
import java.awt.*;

public class Greeter extends me.cambria22118626.Window {

    private final int  WIDTH = 600;
    private final int HEIGHT = 400;
    private final Config cfg = Config.getInstance();

    public Greeter() {
        super("Greeter");

        SpringLayout layout = new SpringLayout();
        JLabel greetingTitle = new JLabel("Greetings!");
        greetingTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        greetingTitle.setForeground(cfg.windowThemingColours.get("TextColour"));

        JTextArea longTextArea = new JTextArea("peepee Poopoo");
        longTextArea.setEditable(false);
        longTextArea.setFont(new Font("Fira Code", Font.PLAIN, 16));
        longTextArea.setForeground(cfg.windowThemingColours.get("TextColour"));
        longTextArea.setBackground(cfg.windowThemingColours.get("MainBG"));
        longTextArea.setLineWrap(true);

        layout.putConstraint(SpringLayout.WEST, greetingTitle, 10, SpringLayout.WEST, longTextArea);
        layout.putConstraint(SpringLayout.SOUTH, greetingTitle, 10, SpringLayout.NORTH, longTextArea);
        OverridePanelLayout(layout);
        panel.add(greetingTitle);
        panel.add(longTextArea);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));

        run();
    }
}
