package me.cambria22118626;

import javax.swing.*;
import java.awt.*;


public class Window extends JFrame {

    protected int Width = 600;
    protected int Height = 400;
    public String name = "{None}";
    protected Color bgColor = new Color(248, 220, 220); //hee hee not pure white 😋
    protected JPanel panel = new JPanel();


    public Window(String name) {
        this.name = name;
        setTitle(this.name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(panel);
        panel.setLayout(null);
    }

    protected void run() {
        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Width / 2,Toolkit.getDefaultToolkit().getScreenSize().height / 2 - Height / 2);
        setBackground(this.bgColor);
        setSize(this.Width, this.Height);

        panel.setVisible(true);
        setVisible(true);

    }

    protected void OverridePanelLayout(LayoutManager layout) {
        panel.setLayout(layout);
    }
}

