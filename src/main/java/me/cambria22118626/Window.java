package me.cambria22118626;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Window extends JFrame {

    protected int Width = 600;
    protected int Height = 400;
    public String name = "{None}";
    protected Color bgColour = new Color(248, 0, 0); //hee hee not pure white 😋
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
        setBackground(this.bgColour);
        setSize(this.Width, this.Height);

        System.out.println("Background ->"+this.getBackground()+"\nBGColour ->"+this.bgColour);

        panel.setVisible(true);
        panel.revalidate();
        panel.repaint();
        setVisible(true);

    }

    protected void OverridePanelLayout(LayoutManager layout) {
        panel.setLayout(layout);
    }
}

