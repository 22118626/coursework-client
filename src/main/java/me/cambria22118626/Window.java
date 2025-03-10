package me.cambria22118626;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Window extends JFrame {

    //used as a root class to structure a window

    //globals
    protected int Width = 600;
    protected int Height = 400;
    public String name = "{None}";
    protected Color bgColour = new Color(248, 0, 0); //hee hee not pure white 😋
    protected JPanel panel = new JPanel();

    //constructor
    public Window(String name) {
        this.name = name;
        setTitle(this.name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(panel);
        panel.setLayout(null);

    }

    // mainloop like function from tkinter
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
    //change options after constructor called
    protected void OverridePanelLayout(LayoutManager layout) {panel.setLayout(layout);}
    protected void OverridePanelClose(int option) {setDefaultCloseOperation(option);}
}

