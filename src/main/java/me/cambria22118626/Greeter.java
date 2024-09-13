package me.cambria22118626;

import javax.swing.*;
import java.awt.*;

public class Greeter extends JFrame {

    private final int  WIDTH = 600;
    private final int HEIGHT = 400;

    public Greeter() {
        System.out.println("Greetings!");
        setTitle("Greeter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH / 2,Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT / 2);
        setBackground(Config.windowThemingColours.get("mainBG"));
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);
        JLabel lblNewLabel = new JLabel("Greeting");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBounds(10, 10, 90, 30);
        panel.add(lblNewLabel);
        panel.setVisible(true);
        setVisible(true);
    }
}
