package me.cambria22118626;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;


public class Login extends Window {
    public static int IPInputError = 0;
    private final Config cfg = Config.getInstance();

    public Login(){

        super("Login");
        this.Width=400;
        this.Height=250;


        // Labels + texts
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


        // Buttons
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        loginButton.setForeground(cfg.windowThemingColours.get("TextColour"));
        loginButton.setBackground(new Color(45, 45, 45));
        loginButton.setBounds(70,100,100,30);
        loginButton.addActionListener(e -> {
            if(username.getText().length() < 8) { // quick incorrect credentials
                Toolkit.getDefaultToolkit().beep();
                shake();
            }
        });

        JButton DBConnect = new JButton("Connect");
        DBConnect.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        DBConnect.setForeground(cfg.windowThemingColours.get("TextColour"));
        DBConnect.setBackground((cfg.windowThemingColours.get("SecondaryBg")));
        DBConnect.addActionListener(e -> {
            System.out.println(DBConnectionIP.getText());
        });
        DBConnect.setBounds(150,150,75,20);


        /*
        Appending them all to the main frame of the window
         */

        panel.add(title);
        panel.add(usernameLabel);
        panel.add(username);
        panel.add(passwordLabel);
        panel.add(password);
        panel.add(loginButton);
        panel.add(DBConnectionIP);
        panel.add(DBConnect);
        //Color cfgColour = cfg.windowThemingColours.get("MainBG");
        //panel.setBackground(cfgColour);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));

        run();
    }

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

    private Point bezier2PointFunction(Point newPos, Point origPos, double t) {
        /*
        * Formula for 2 point Bezier curve:
        * P = (1-t)P₁ + tP₂
         */
        int X = (int) (Math.round((1-t) * origPos.x) + (t*newPos.x));
        int Y = (int) (Math.round((1-t) * origPos.y) + (t*newPos.y));
        return new Point(X, Y);
    }

}
