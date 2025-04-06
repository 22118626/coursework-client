package me.cambria22118626;

import me.cambria22118626.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingMenu extends Window {
    Config cfg = Config.getInstance();
    //setting menu in the login screen to modify vital settings.
    public SettingMenu() {
        super("settings");
        this.Width = 300;
        this.Height = 400;

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        title.setForeground(cfg.windowThemingColours.get("TextColour") );
        title.setBounds(5,5,this.Width,20);
        title.setHorizontalAlignment(SwingConstants.CENTER);



        JLabel certificateLabel = new JLabel("Certificate");
        certificateLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        certificateLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
        certificateLabel.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        certificateLabel.setBounds(5,30,150,20);

        JTextField certificateIP = new JTextField();
        certificateIP.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        certificateIP.setForeground(cfg.windowThemingColours.get("TextColour"));
        certificateIP.setBounds(5,60,120,20);

        JButton certificateRetrieve = new JButton("Retrieve");
        certificateRetrieve.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        certificateRetrieve.setForeground(cfg.windowThemingColours.get("TextColour"));
        certificateRetrieve.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        certificateRetrieve.setBounds(150,60,75,20);
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


        // seperate the window to show below is database
        JLabel dbLabel = new JLabel("Database");
        dbLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        dbLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
        dbLabel.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        dbLabel.setBounds(5,90,150,20);

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
        DBConnectionIP.setBounds(5,110,120,20);

        // DBConnect button uses the IP entered into the DBConnect entry field to bind a listening socket to
        // a port where data will be sent to the server and listened from the server
        JButton DBConnect = new JButton("Connect");
        DBConnect.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
        DBConnect.setForeground(cfg.windowThemingColours.get("TextColour"));
        DBConnect.setBackground((cfg.windowThemingColours.get("SecondaryBG")));
        DBConnect.addActionListener(e -> {
            System.out.println(DBConnectionIP.getText());
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
            Map<String,String> connectIPmap = new HashMap<>();
            connectIPmap.put("connectIP", ip);
            connectIPmap.put("connectPort", String.valueOf(port));

            cfg.allNodes.put("ConnectAddress", connectIPmap);
            try {
                Config.commit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ClientSock soc = ClientSock.getInstance();
            if (ip.length() > 2 && port >= 1080 && port <= 49151) {
                soc.setServerAddress(ip, port);
                Main.persistMemJson.put("IPAddress", DBConnectionIP.getText());
            }
        });
        DBConnect.setBounds(150,110,75,20);
        int i = 1;
        for(String colourName : Config.windowThemingColours.keySet()) {
            JLabel colour = new JLabel(colourName);
            colour.setForeground(cfg.windowThemingColours.get("TextColour"));
            colour.setBounds(5,140+25*i,150,20);
            panel.add(colour);
            JButton colourButton = new JButton("edit");
            colourButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 10));
            colourButton.setForeground(cfg.windowThemingColours.get("TextColour"));
            colourButton.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            colourButton.setBounds(160,140+25*i,150,20);
            colourButton.addActionListener(e -> {
                    Window win = new Window(colourName);
                    JPanel panel1 = new JPanel();
                    win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    win.setLayout(new BorderLayout(5, 5));
                    JLabel colourLabel = new JLabel(colourName);
                    colourLabel.setForeground(Config.windowThemingColours.get("TextColour"));
                    colourLabel.setBackground(Config.windowThemingColours.get("SecondaryBG"));
                    panel1.add(colourLabel);
                    JLabel instructionsLabel = new JLabel("Click on the colour in the image to select the colour");
                    instructionsLabel.setForeground(cfg.windowThemingColours.get("TextColour"));
                    instructionsLabel.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                    panel1.add(instructionsLabel);
                    BufferedImage image = null;
                    try {
                        InputStream is = SettingMenu.class.getResourceAsStream("/images/colourpicker.jpg");
                        if (is != null) {
                            image = ImageIO.read(is);
                        } else {
                            System.out.println("image in resources not found");
                        }
                    } catch (IOException e2) {
                        throw new RuntimeException(e2);
                    }
                    Graphics g = image.createGraphics();
                    g.drawImage(image, 0, 0, null);
                    g.dispose();
                    BufferedImage finalImage = image;
                    JPanel imagePanel = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.drawImage(finalImage, 0, 0, this);
                        }
                    };
                    imagePanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int x = e.getX();
                            int y = e.getY();
                            int rgb = finalImage.getRGB(x, y);
                            Color colour = new Color(rgb);
                            JPanel optionPanePanel = new JPanel();
                            optionPanePanel.setLayout(new BoxLayout(optionPanePanel, BoxLayout.Y_AXIS));
                            JLabel colourLabel = new JLabel("is this the new colour you want to set for "+colourName+"? \n"+colour.toString());
                            colourLabel.setForeground(Color.BLACK);
                            optionPanePanel.add(colourLabel);
                            JPanel ColourPanel = new JPanel();
                            ColourPanel.setPreferredSize(new Dimension(100, 100));
                            ColourPanel.setBackground(colour);
                            optionPanePanel.add(ColourPanel);

                            Integer result = JOptionPane.showOptionDialog(imagePanel, optionPanePanel, "confirm", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, new Object[]{"yes","no"}, 0);
                            if(result == JOptionPane.YES_OPTION) {
                                ArrayList<Map<String,Object>> listOfAllColours = (ArrayList<Map<String,Object>>) Config.allNodes.get("colours");
                                for (int i = 0; i < listOfAllColours.size(); i++) {
                                    Map<String, Object> colourMap = listOfAllColours.get(i);

                                    // Check if contains the name its looking for
                                    if (colourMap.get("name").equals(colourName)) {
                                        // Replace color with new ones
                                        try {
                                            colourMap.put("red", colour.getRed());
                                            colourMap.put("green", colour.getGreen());
                                            colourMap.put("blue", colour.getBlue());
                                            listOfAllColours.set(i, colourMap);
                                            Config.allNodes.put("colours", listOfAllColours);
                                            Config.commit();
                                            JOptionPane.showMessageDialog(imagePanel, "colour updated\nRestart the program for changes to take effect");
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    });
                    imagePanel.setPreferredSize(new Dimension(finalImage.getWidth(), finalImage.getHeight()));
                    panel1.add(imagePanel, BorderLayout.CENTER);
                    panel1.setBackground(cfg.windowThemingColours.get("MainBG"));
                    win.add(panel1);
                    win.run();

                    for(Component component : win.getComponents()) {
                        component.setBackground(cfg.windowThemingColours.get("MainBG"));
                        System.out.println(component.getName());
                    }

            });
            panel.add(colourButton);
            i++;

        }


        //appends all the components to the frame
        panel.add(title);
        panel.add(certificateLabel);
        panel.add(certificateIP);
        panel.add(certificateRetrieve);
        panel.add(dbLabel);
        panel.add(DBConnectionIP);
        panel.add(DBConnect);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        run(); // <- java.swing version of mainloop in python
    }
}
