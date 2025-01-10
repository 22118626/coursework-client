package me.cambria22118626;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.cambria22118626.ClientSock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;
import java.util.Random;


public class MainMenu extends Window{
    public MainMenu() {
        super("MainMenu");
        Config cfg = Config.getInstance();


        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Height = (int) Math.floor(scrSize.getHeight() * 0.85);
        this.Width = (int) Math.floor(scrSize.getWidth() * 0.85);
        this.setMinimumSize(new Dimension(875, Math.min(720, this.Width)));

        System.out.println("bingus");
        //TODO: add Socket call DB mode:"getTables"
        try{
            ObjectMapper OM = new ObjectMapper();
            String msg = "{\"mode\": \"getTables\", \"data\":{}, \"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}";
            System.out.println(msg);
            String response = ClientSock.getInstance().sendMessage(msg);
            if (response == null) {
                throw new Exception();
            }

            Map<String, Object> responseJ = OM.readValue(response, Map.class);
            if(responseJ == null) throw new Exception(); //just to sanity check that there will be no runtime errors
            if (!responseJ.containsKey("data") || !(responseJ.get("data") instanceof Map)) throw new Exception();

            List<Map<String, Object>> array= (List<Map<String, Object>>) ((Map<String, Object>) responseJ.get("data")).get("array");
            for (int i = 0; i < array.size(); i++) {
                Main.tables.add(array.get(i));
                System.out.println(array.get(i).get("tableName"));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }


        JPanel grid = new JPanel();
        grid.setLayout(new WrapLayout());
        grid.setOpaque(true);
        grid.setBackground(cfg.windowThemingColours.get("PrimaryBG"));

        for(Map<String, Object> table : Main.tables) {
            JButton btn = new JButton(table.get("tableName").toString());
            btn.setForeground(cfg.windowThemingColours.get("TextColour"));
            btn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            btn.setPreferredSize(new Dimension(200, 120));
            btn.setBackground(new Color(+((int) (Math.random()*20+5) << 16) + ((int) (Math.random()*20) << 8) + (int) (Math.random()*64+32) )); // R=5-25, G=0-20, B=32-96
            grid.add(btn);
        }
        for(int i = 1; i<=30; i++) {
            JButton btn = new JButton("Button "+i);
            btn.setForeground(cfg.windowThemingColours.get("TextColour"));
            btn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            btn.setPreferredSize(new Dimension(200, 120));
            btn.setBackground(new Color(+((int) (Math.random()*5+Math.sin(i/8)*10) << 16) + ((int) (Math.random()*10+Math.sin(i/8)*30) << 8) + (int) (Math.random()*16+Math.sin(i/10)*15+48) )); // R=5-25, G=0-20, B=32-96
            grid.add(btn);
            System.out.println("Button "+i);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setForeground(cfg.windowThemingColours.get("SecondaryBG"));
        scroll.setBackground(cfg.windowThemingColours.get("TertiaryBG"));

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BorderLayout());
        JLabel ttl = new JLabel("List of tables");
        ttl.setFont(new Font("Bahnschrift Light", Font.PLAIN, 30));
        ttl.setHorizontalAlignment(SwingConstants.CENTER);
        ttl.setOpaque(true);
        ttl.setForeground(cfg.windowThemingColours.get("TextColour"));
        ttl.setBackground(cfg.windowThemingColours.get("TertiaryBG"));
        System.out.println("tert-> "+cfg.windowThemingColours.get("TertiaryBG"));
        panelLeft.add(ttl, BorderLayout.NORTH);
        panelLeft.add(scroll, BorderLayout.CENTER);

        JPanel panelRight = new JPanel();
        panelRight.setLayout(new GridBagLayout());
        panelRight.setBackground(cfg.windowThemingColours.get("PrimaryBG"));
        //panelRight.setPreferredSize(new Dimension((int) (super.getWidth()*0.2+250), super.getHeight()));

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = (int) (getWidth() * 0.2 + 250);
                int height = getHeight();

                panelRight.setPreferredSize(new Dimension(width, height));
                panelRight.revalidate();
            }
        });


        /*panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = scroll.getWidth();
                int numberOfColumns = Math.max(1,frameWidth/120);  //round for cols, where each button is 120px

                //grid.setLayout(new GridLayout(0, numberOfColumns, 10, 10));
                grid.setMaximumSize(new Dimension(99999999 , frameWidth));
                panel.revalidate();
                panel.repaint();
                grid.revalidate();
                grid.repaint();
                scroll.revalidate();
                scroll.repaint();
            System.out.println("scroll: "+scroll.getSize());
            System.out.println("grid: "+grid.getSize());
            }
        });*/
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        if(Files.exists(Path.of(Config.getFile()+"\\logo.png"))) {
            System.out.println("adding logo: "+Config.getFile()+"\\logo.png");

            ImageIcon logoimg = new ImageIcon(Config.getFile()+"\\logo.png");
            JLabel logo = new JLabel(logoimg);
            logo.setSize((int)Math.floor(this.Width*0.2), (int)Math.floor(this.Height*0.2));
            System.out.println(logoimg.getImageLoadStatus());
            panelRight.add(logo, gbc);
        }

        JButton logout = new JButton("Logout");
        logout.setForeground(cfg.windowThemingColours.get("TextColour"));
        logout.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        gbc = new GridBagConstraints();
        gbc.gridy = 4; gbc.gridx = 1;

        panelRight.add(logout, gbc);

        OverridePanelLayout(new BorderLayout());
        panel.add(panelLeft, BorderLayout.CENTER);
        panel.add(panelRight, BorderLayout.EAST);
        panel.setBackground(cfg.windowThemingColours.get("MainBG"));
        run();

    }
}
