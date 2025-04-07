package me.cambria22118626;

//necesarry imports
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;


public class Login extends Window {
    //object attributes
    public static int IPInputError = 0;
    private final Config cfg = Config.getInstance();

    // this method controls main login window that is displayed
    public Login(){

        //inheritence from me.cambria2211826.Window class
        // and makes the window with default width and height
        super("Login");
        this.Width=400;
        this.Height=250;

        //System.out.println(encryptString("3ae8be7283930bbad035636d1ca17f4d87050b248a83b1761f3c38901289b70f"+"1234")); //debugg line

        // created components in the window
        JLabel title = new JLabel("Bethany Books Authenticator");
        title.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
        title.setForeground(cfg.windowThemingColours.get("TextColour") );
        title.setBounds(5,5,350,40);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        usernameLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
        usernameLabel.setBounds(5,50,100,20);
        JTextField username = new JTextField();
        username.setFont(new Font("Fira Code", Font.PLAIN, 14));
        username.setForeground(Color.BLACK);
        username.setBounds(110,50,100,20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        passwordLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
        passwordLabel.setBounds(5,75,100,20);
        JPasswordField password = new JPasswordField();
        password.setForeground(Color.BLACK);
        password.setBounds(110,75,100,20);



        // create Buttons and bind them with their respective functions
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginButton.setForeground(cfg.windowThemingColours.get("TextColour"));
        loginButton.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        loginButton.setBounds(70,100,100,30);
        loginButton.addActionListener(e -> {
            System.out.println("encrypted: ("+encryptString(Arrays.toString(password.getPassword())+username.getText())+")");
            if(username.getText().length() < 8) { // validation of credentials
                Toolkit.getDefaultToolkit().beep();
                shake();
                JOptionPane.showMessageDialog(new JFrame(), "Username is too short", "warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                //this code will create a SHA256 hash of the Password+username strings concatenated
                // together and send them to the me.cambria2211826.ClientSock to send to the server and will await for the
                // response. if bad response show error

                String HashedP = encryptString(Arrays.toString(password.getPassword())+username.getText());
                String result = ClientSock.getInstance().sendMessage("{\"mode\":\"authenticate\",\"data\":{\"username\":\""+username.getText()+"\",\"password\":\""+HashedP+"\"}}");
                System.out.println(result);
                ObjectMapper OM = new ObjectMapper();
                Map<String, Object> resultJ = OM.readValue(result, Map.class);
                if ((Boolean) (resultJ.get("access"))) {
                    Main.persistMemJson.put("Username", username.getText());
                    Main.persistMemJson.put("HashedPassword", HashedP);
                    Main.persistMemJson.put("loginCreds", resultJ.get("data"));
                    new MainMenu();
                    this.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(new JFrame(), "incorrect credentials", "warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), " Error has occured when sending packet to the server.\n\nHas the correct IP and port been entered for the database connection?", "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        });


        //shows setting menu when pressed
        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        settingsButton.setForeground(cfg.windowThemingColours.get("TextColour"));
        settingsButton.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        settingsButton.setBounds(275,170,100,20);
        settingsButton.addActionListener(e -> {new SettingMenu();});


        //Appending all components to the main frame of the window
        panel.add(title);
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(passwordLabel);
        panel.add(password);
        panel.add(loginButton);
        panel.add(settingsButton);
        bgColour = cfg.windowThemingColours.get("MainBG");
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));

        run();
    }


    // fun method that will get the X and Y coordinates of the window and pick a new random position within the
    // displat and interpolate the window between 2 XY coordinates and then return it back to the original position.
    private void shake() {
        Point originalPosition = new Point(this.getX(), this.getY());
        int shakeCount = 10;
        Random rand = new Random();

        for (int i = 0; i <= shakeCount; i++) {
            if (i != shakeCount) {
                InterpolateWindow(new Point(getX()+(rand.nextInt(50)-25), getY()+rand.nextInt(10)-5), new Point(getX(), getY()), 0.33f);
            }
        }
        InterpolateWindow(originalPosition, new Point(getX(), getY()), 1f);
    }

    // makes a smooth transition moving the window a small amout per frame rendered
    private void InterpolateWindow(Point newPos, Point origPos, Float time) {
        int hz = 60;
        for (int frame = 0; frame < 60*time; frame++) {
            try {
                setLocation(bezier2PointFunction(newPos, origPos, frame/(hz*time)));
                Thread.sleep((long) ((time/hz)*100));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // yeah... does what it says.
    private Point bezier2PointFunction(Point newPos, Point origPos, double t) {
        /*
        * Formula for 2 point Bezier curve:
        * P = (1-t)P₁ + tP₂
         */
        int X = (int) (Math.round((1-t) * origPos.x) + (t*newPos.x));
        int Y = (int) (Math.round((1-t) * origPos.y) + (t*newPos.y));
        return new Point(X, Y);
    }


    // uses SHA 256 to encrypt  string by the parameter given and return the hashed string
    public static String encryptString(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes());
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hashText = new StringBuilder(number.toString(16));
            while (hashText.length() < 64) {
                hashText.insert(0, "0");
            }
            System.out.println(hashText.toString());
            return hashText.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.getCause();
            return "";
        }
    }


}
