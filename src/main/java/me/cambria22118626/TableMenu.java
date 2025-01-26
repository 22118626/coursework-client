package me.cambria22118626;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TableMenu extends Window{
    private Map<String, Object> table;
    private final Config cfg = Config.getInstance();

    public TableMenu(String name, Map<String, Object> table) {
        super(name);
        this.table = table;
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Height = (int) Math.floor(scrSize.getHeight() * 0.5);
        this.Width = (int) Math.floor(scrSize.getWidth() * 0.5);
        this.setMinimumSize(new Dimension(500, Math.min(500, this.Width)));

        run();
        OverridePanelClose(JFrame.DISPOSE_ON_CLOSE);
        Menu();
    }

    private void Menu() {
        panel.removeAll();
        OverridePanelLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        JButton create = new JButton("Create record");
        create.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        create.setPreferredSize(new Dimension(200, 120));
        create.setBackground(new Color(0x043304)); // RGB=32, 128, 32
        create.setForeground(cfg.windowThemingColours.get("TextColour"));
        create.addActionListener(e->CreateWindow());
        c.gridx = 1;
        c.gridy = 1;
        panel.add(create, c);

        JButton modview = new JButton("Modify or View record");
        modview.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        modview.setPreferredSize(new Dimension(200, 120));
        modview.setBackground(new Color(0x03033C)); // RGB=32, 32, 128
        modview.setForeground(cfg.windowThemingColours.get("TextColour"));
        modview.addActionListener(e->ViewModWindow());
        c.gridx = 3;
        c.gridy = 1;
        panel.add(modview, c);

        JButton delete = new JButton("Delete record");
        delete.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        delete.setPreferredSize(new Dimension(200, 120));
        delete.setBackground(new Color(0x330404)); // RGB=128, 32, 32
        delete.setForeground(cfg.windowThemingColours.get("TextColour"));
        c.gridx = 5;
        c.gridy = 1;
        panel.add(delete, c);
    }

    private void CreateWindow() {
        panel.removeAll();
        OverridePanelLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;

        ObjectMapper OM = new ObjectMapper();
        try {
            String array = OM.writeValueAsString(table.get("array"));
            JsonNode jArray = OM.readTree(array);
            c.gridy=1;
            GridBagConstraints labelcon = c;
            GridBagConstraints entrycon = c;
            entrycon.gridy = c.gridy+1;
            for (int i = 0; i < jArray.size(); i++) {
                JsonNode field = jArray.get(i);

                JLabel btn = new JLabel(field.get("name").asText());
                btn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                btn.setForeground(cfg.windowThemingColours.get("TextColour"));
                c.gridx = i;
                panel.add(btn, c);
                c.gridy++;
                JTextField entry = new JTextField(field.get("name").asText());
                entry.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                entry.setForeground(cfg.windowThemingColours.get("TextColour"));
                entry.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                entry.setName(field.get("name").asText());
                panel.add(entry, c);
                c.gridy--;
            }
            JButton ConfirmBtn = new JButton("append");
            ConfirmBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            ConfirmBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
            ConfirmBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            ConfirmBtn.addActionListener(e->{if (Confirmation()) {
                try {
                    ClientSock soc = ClientSock.getInstance();
                    Map<String, Object> data = getDataFromTextFields(panel, array);
                    String res = soc.sendMessage("{\"mode\":\"append\",\"tableName\":\""+this.name+"\",\"data\":"+OM.writeValueAsString(data)+",\"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}");
                    JsonNode Jres = OM.readTree(res);
                    if (Jres.get("code").asInt() == 0) {
                        System.out.println("Creating record success!");
                        this.Menu();
                        this.Width = getWidth();
                        this.Height = getHeight();
                        run();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});
            c.gridy += 2;
            c.gridx = 1;
            panel.add(ConfirmBtn, c);
            panel.setBackground(cfg.windowThemingColours.get("MainBG"));
            repaint();
            revalidate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean Confirmation() {
        return 0 == JOptionPane.showConfirmDialog(null, "DO you confirm everything is correct?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    private static Map<String,Object> getDataFromTextFields(JPanel panel, String array) throws IOException {
        Map<String,Object> data = new HashMap<>();
        ObjectMapper OM = new ObjectMapper();
        for (Component comp :  panel.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                String fieldName = field.getName();
                for(JsonNode entry : OM.readTree(array)) {
                    System.out.println("entry: "+entry.get("name")+" "+entry.get("type"));
                    if(!entry.get("name").asText().equals(fieldName)) {
                        continue;
                    }

                    System.out.println(field.getName()+": "+field.getText());
                    if (entry.get("type").asInt() == 1 || entry.get("type").asInt() == 2){data.put(fieldName, Integer.parseInt(field.getText()));}
                    else if(entry.get("type").asInt() == 3) {data.put(fieldName, field.getText());}
                    else if (entry.get("type").asInt() == 4){
                        Map<String, Object> nestedMap = new HashMap<>();
                        nestedMap.put("value", Integer.parseInt(field.getText()));
                        data.put(fieldName, nestedMap);
                    }
                }
            }
        }
        return data;
    }


    private void ViewModWindow() {
        panel.removeAll();
        OverridePanelLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;

        ObjectMapper OM = new ObjectMapper();
        try {
            String array = OM.writeValueAsString(table.get("array"));
            JsonNode jArray = OM.readTree(array);
            c.gridy=1;
            GridBagConstraints labelcon = c;
            GridBagConstraints entrycon = c;
            entrycon.gridy = c.gridy+1;
            for (int i = 0; i < jArray.size(); i++) {
                JsonNode field = jArray.get(i);

                JLabel btn = new JLabel(field.get("name").asText());
                btn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                btn.setForeground(cfg.windowThemingColours.get("TextColour"));
                btn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                c.gridx = i;
                panel.add(btn, c);
                c.gridy++;
                JTextField entry = new JTextField(field.get("name").asText());
                entry.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                entry.setForeground(cfg.windowThemingColours.get("TextColour"));
                entry.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                entry.setName(field.get("name").asText());
                panel.add(entry, c);
                c.gridy--;
            }
            JButton FindBtn = new JButton("find");
            FindBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            FindBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
            FindBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            FindBtn.addActionListener(e->{if (Confirmation()) {
                try {
                    ClientSock soc = ClientSock.getInstance();
                    Map<String, Object> data = new HashMap<>();

                    for (Component comp :  panel.getComponents()) {
                        if (comp instanceof JTextField) {
                            JTextField field = (JTextField) comp;
                            String fieldName = field.getName();
                            for(JsonNode entry : jArray) {
                                System.out.println("entry: "+entry.get("name")+" "+entry.get("type"));
                                if(!entry.get("name").asText().equals(fieldName) || field.getText().isEmpty()) {
                                    continue;
                                }
                                data.put("field", fieldName);
                                data.put("value", field.getText());

                                /*System.out.println(field.getName()+": "+field.getText());
                                if (entry.get("type").asInt() == 1 || entry.get("type").asInt() == 2){data.put(fieldName, Integer.parseInt(field.getText()));}
                                else if(entry.get("type").asInt() == 3) {data.put(fieldName, field.getText());}
                                else if (entry.get("type").asInt() == 4){
                                    Map<String, Object> nestedMap = new HashMap<>();
                                    nestedMap.put("value", Integer.parseInt(field.getText()));
                                    data.put(fieldName, nestedMap);
                                }*/
                            }
                        }
                    }
                    String res = soc.sendMessage("{\"mode\":\"search\",\"tableName\":\""+this.name+"\",\"data\":"+OM.writeValueAsString(data)+",\"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}");
                    JsonNode Jres = OM.readTree(res);
                    if (Jres.get("code").asInt() == 0) {
                        System.out.println("finding record success!\ndata:"+OM.writeValueAsString(Jres).indent(2));
                        String msg = "";
                        JsonNode rootNode = Jres.get("data");
                        boolean hasDeleteBtn = false;
                        boolean hasModifyBtn = false;
                        System.out.println("gonna check through components");
                        for (Component comp :  panel.getComponents()) {

                            if (comp instanceof JTextField) {
                                JTextField field = (JTextField) comp;
                                String fieldName = field.getName();
                                if(!rootNode.has(fieldName)) {
                                    continue;
                                } else {
                                    field.setText(rootNode.get(fieldName).asText());
                                }
                            }
                            else if (comp instanceof JButton) {
                                JButton button = (JButton) comp;
                                if(button.getText().equals("Delete")) {
                                    hasDeleteBtn = true;
                                }else if(button.getText().equals("Modify")) {
                                    hasModifyBtn = true;
                                }
                            }
                        }
                        System.out.println(hasModifyBtn+" "+hasDeleteBtn);
                        if(!hasDeleteBtn && !hasModifyBtn) {
                            JButton DelBtn = new JButton("Delete");
                            DelBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                            DelBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
                            DelBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                            DelBtn.addActionListener(e1 -> {
                                try {
                                    Map<String, Object> values = getDataFromTextFields(panel, array);
                                    String removeRes = soc.sendMessage("{\"mode\":\"remove\",\"tableName\":\""+this.name+"\",\"data\":"+OM.writeValueAsString(values)+",\"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}");
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }

                            });

                            GridBagConstraints gbc = new GridBagConstraints();
                            gbc.fill = GridBagConstraints.BOTH;
                            gbc.weightx = 1.0;
                            gbc.weighty = 1.0;
                            gbc.gridx = 3;
                            gbc.gridy = c.gridy+2;

                            panel.add(DelBtn, gbc);

                            JButton ModBtn = new JButton("Modify");
                            ModBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                            ModBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
                            ModBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                            ModBtn.addActionListener(e1 -> {
                                try {
                                    Map<String, Object> values = getDataFromTextFields(panel, array);
                                    String message = "{\"mode\":\"modify\",\"tableName\":\""+this.name+"\",\"data\":"+OM.writeValueAsString(values)+"\"oldData\":"+OM.writeValueAsString(data)+",\"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}";
                                    System.out.println(message);
                                    String removeRes = soc.sendMessage(message);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }

                            });
                            gbc.gridx++;
                            panel.add(ModBtn, gbc);
                            System.out.println("added buttons?");
                            panel.revalidate();
                            panel.repaint();
                        }



                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});
            c.gridy += 2;
            c.gridx = 1;
            panel.add(FindBtn, c);
            panel.setBackground(cfg.windowThemingColours.get("MainBG"));
            repaint();
            revalidate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
