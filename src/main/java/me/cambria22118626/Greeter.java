package me.cambria22118626;

import javax.swing.*;

public class Greeter extends JFrame {
    public Greeter() {
        setTitle("Greeter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);
        JLabel lblNewLabel = new JLabel("Greeting");
    }
}
