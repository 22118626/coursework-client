package me.cambria22118626;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Greeter extends me.cambria22118626.Window {
    //globals
    private final int  WIDTH = 600;
    private final int HEIGHT = 400;
    private final Config cfg = Config.getInstance();

    //main function
    public Greeter() {
        super("Greeter");
        //creates a frame in which long text is displayed goin g through the instructions how to use the program
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
                Thank you for choosing to use our program! 
                We truly appreciate your support and trust in our software. 
                Your feedback is invaluable to us, and we're always working to improve. 
                We hope you had a smooth experience and that our program meets your needs. 
                If you have any questions or suggestions, 
                feel free to reach out. 
                Thanks again, and happy using!""";


        JTextArea longTextArea = new JTextArea(longTextStr);
        longTextArea.setEditable(false);
        longTextArea.setFont(new Font("Fira Code", Font.PLAIN, 16));
        longTextArea.setForeground(cfg.windowThemingColours.get("TextColour"));
        longTextArea.setBackground(cfg.windowThemingColours.get("MainBG"));
        longTextArea.setLineWrap(true);

        OverridePanelLayout(layout);

        //confirmation button that will open the main login menu once pressed
        JButton confirmationButton = new JButton("I understand");
        confirmationButton.addActionListener(e -> {
           new me.cambria22118626.Login();
           dispose();
        });

        //adds all the comps it oa w teh window
        panel.add(greetingTitle, BorderLayout.NORTH);
        panel.add(longTextArea, BorderLayout.CENTER);
        panel.add(confirmationButton, BorderLayout.SOUTH);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));


        System.out.println(cfg.windowThemingColours.get("MainBG"));

        run();
    }
}
