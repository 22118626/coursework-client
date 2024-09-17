package me.cambria22118626;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Greeter extends me.cambria22118626.Window {

    private final int  WIDTH = 600;
    private final int HEIGHT = 400;
    private final Config cfg = Config.getInstance();

    public Greeter() {
        super("Greeter");

        BorderLayout layout = new BorderLayout();
        JLabel greetingTitle = new JLabel("Greetings!");
        greetingTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        greetingTitle.setForeground(cfg.windowThemingColours.get("TextColour"));
        setMinimumSize(new Dimension((2*WIDTH/3), (2*HEIGHT)/3));

        greetingTitle.setHorizontalAlignment(JLabel.CENTER);

        String longTextStr = """
                This appears to be your first time using the app!
                1) make sure you enter the right IP into the database connection entry before logging in.
                2) login with your given credentials.
                ...
                this and that 
                blah blah""";
        JTextArea longTextArea = new JTextArea(longTextStr);
        longTextArea.setEditable(false);
        longTextArea.setFont(new Font("Fira Code", Font.PLAIN, 16));
        longTextArea.setForeground(cfg.windowThemingColours.get("TextColour"));
        longTextArea.setBackground(cfg.windowThemingColours.get("MainBG"));
        longTextArea.setLineWrap(true);

        OverridePanelLayout(layout);

        JButton confirmationButton = new JButton("I understand");
        confirmationButton.addActionListener(e -> {
           new me.cambria22118626.Login();
           dispose();
        });

        panel.add(greetingTitle, BorderLayout.NORTH);
        panel.add(longTextArea, BorderLayout.CENTER);
        panel.add(confirmationButton, BorderLayout.SOUTH);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));

        run();
    }
}
