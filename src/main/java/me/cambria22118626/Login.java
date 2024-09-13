package me.cambria22118626;

import javax.swing.*;
import java.awt.*;

public class Login extends Window {
    public Login(){
        super("Login");
        this.Width=400;
        this.Height=250;
        JLabel title = new JLabel("Bethany Books Login");
        title.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        title.setForeground(Config.windowThemingColours.get("TextColour"));
        title.setBounds(5,5,100,40);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        usernameLabel.setForeground(Config.windowThemingColours.get("TextColour"));
        usernameLabel.setBounds(5,50,100,20);
        JTextField username = new JTextField();
        username.setFont(new Font("FireCode", Font.PLAIN, 16));
        username.setForeground(Color.BLACK);
        username.setBounds(110,50,100,20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        passwordLabel.setForeground(Config.windowThemingColours.get("TextColour"));
        passwordLabel.setBounds(5,75,100,20);
        JPasswordField password = new JPasswordField();
        password.setFont(new Font("FireCode", Font.PLAIN, 16));
        password.setForeground(Color.BLACK);
        password.setBounds(110,75,100,20);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginButton.setForeground(Config.windowThemingColours.get("TextColour"));
        loginButton.setBackground(Config.windowThemingColours.get("SecondaryBg"));
        loginButton.setBounds(70,100,100,40);


        panel.add(title);
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(passwordLabel);
        panel.add(password);
        panel.add(loginButton);
        panel.setBackground(Config.windowThemingColours.get("MainBg"));

        run();
    }


}
