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

        //this binds a function to the DBConnection entry window to validate that only numbers and IP specific characters are entered:(0-9 OR . OR :)
        JTextField DBConnectionIP = new JTextField();
        DBConnectionIP.setFont(new Font("Visitor TT1 BRK", Font.PLAIN, 16));
        DBConnectionIP.setForeground(Color.BLACK);
        DBConnectionIP.setToolTipText("example: 10.0.4.129:25565");
        DBConnectionIP.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                // not invoking later causes 'Attempt to mutate in notification' java.lang.IllegalStateException error
                SwingUtilities.invokeLater(() -> {
                    try{
                        char character = e.getDocument().getText(e.getOffset(),e.getLength()).charAt(0);
                        if(!(character == '.' || (  character >= '0' && character <= '9' ) || character ==':')) { //shows warning window if input wrong the first time then every 5 incorrect formats, if the character inputted was not '.' ':' or a range between 0:9
                            e.getDocument().remove(e.getOffset(), 1);
                            Toolkit.getDefaultToolkit().beep();
                            //shows an warning window if every 5 incorrect characters input into thefield
                            if (Login.IPInputError++ % 5 == 0) {
                                JOptionPane.showMessageDialog(new JFrame(), "Please only enter 0-9, '.' and ':'", "warning", JOptionPane.WARNING_MESSAGE);

                            }
                        }
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
            @Override public void removeUpdate(DocumentEvent e) {}
            @Override public void changedUpdate(DocumentEvent e) {}
        });
        DBConnectionIP.setBounds(5,150,100,20);


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
                    Main.persistMemJson.put("IPAddress", DBConnectionIP.getText());
                    Main.persistMemJson.put("loginCreds", resultJ.get("data"));
                    new MainMenu();
                    this.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), " Error has occured when sending packet to the server.\n\nHas the correct IP and port been entered for the database connection?", "Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        });

        // DBConnect button uses the IP entered into the DBConnect entry field to bind a listening socket to
        // a port where data will be sent to the server and listened from the server

        JButton DBConnect = new JButton("Connect");
        DBConnect.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        DBConnect.setForeground(cfg.windowThemingColours.get("TextColour"));
        DBConnect.setBackground((cfg.windowThemingColours.get("SecondaryBG")));
        DBConnect.addActionListener(e -> {
            System.out.println(DBConnectionIP.getText());
            ClientSock soc = ClientSock.getInstance();
            String ip = "";
            int port = 0;
            // split the text from the field into individual IP and Port variables and required by the WinSock function
            for(String i : DBConnectionIP.getText().split("\\:")) {
                if(i.contains(".") && i.length() >= 7) {
                    ip = i;
                }else if (! i.contains(".")) {
                    port = Integer.parseUnsignedInt(i);
                }
            }
            //ensure that the port is longer then 2 digits and is not trying to bind to any protected ranges and is not larger than the current standard states
            if (ip.length() > 2 && port >= 1080 && port <= 49151) {
                    soc.setServerAddress(ip, port);
            }
        });
        DBConnect.setBounds(150,150,75,20);

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
        panel.add(DBConnectionIP);
        panel.add(DBConnect);
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


    //setting menu in the login screen to modify vital settings.
    private class SettingMenu extends Window {
        public SettingMenu() {
            super("settings");
            this.Width = 300;
            this.Height = 400;

            JLabel title = new JLabel("Settings");
            title.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
            title.setForeground(cfg.windowThemingColours.get("TextColour") );
            title.setBounds(5,5,245,20);


            JLabel certificateLabel = new JLabel("Certificate");
            certificateLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            certificateLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
            certificateLabel.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            certificateLabel.setBounds(5,20,150,20);

            JTextField certificateIP = new JTextField();
            certificateIP.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
            certificateIP.setForeground(cfg.windowThemingColours.get("TextColour"));
            certificateIP.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            certificateIP.setBounds(5,50,100,20);

            JButton certificateRetrieve = new JButton("Retrieve Certificate");
            certificateRetrieve.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
            certificateRetrieve.setForeground(cfg.windowThemingColours.get("TextColour"));
            certificateRetrieve.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            certificateRetrieve.setBounds(100,50,150,20);
            certificateRetrieve.addActionListener(e -> {
                // The user specifies a port to probe the server into sending a new certificate through which communication will be encrypted

                String ip = "";
                int port = 0;
                // spliting into ip and port veriables
                for(String i : certificateIP.getText().split(":")) {
                    if(i.contains(".") && i.length() >= 7) {
                        ip = i;
                    }else if (! i.contains(".")) {
                        port = Integer.parseUnsignedInt(i);
                    }
                }
                // checks valid port range and sends details to me.cambria2211826.Certificate class to get data from server
                if (ip.length() > 2 && port >= 1080 && port <= 49151) {
                    Certificate cert = new Certificate();
                    boolean result = cert.createCertificateFromServer(ip, port);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Certificate created");
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Certificate not created successfully");
                    }
                }
            });
            //appends all the components to the frame
            panel.add(title);
            panel.add(certificateLabel);
            panel.add(certificateIP);
            panel.add(certificateRetrieve);
            panel.setBackground(cfg.windowThemingColours.get("MainBG"));

            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            run(); // <- java.swing version of mainloop in python
        }
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
