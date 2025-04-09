package me.cambria22118626;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;

import java.awt.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TableMenu extends Window{
    private Map<String, Object> table;
    private final Config cfg = Config.getInstance();

    // initial settings for the menu
    public TableMenu(String name, Map<String, Object> table) {
        super(name);
        this.table = table;
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.Height = (int) Math.floor(scrSize.getHeight() * 0.5);
        this.Width = (int) Math.floor(scrSize.getWidth() * 0.5);
        this.setMinimumSize(new Dimension(500, Math.min(500, this.Width)));

        run();
        OverridePanelClose(JFrame.DISPOSE_ON_CLOSE);
        Menu(); // runs the main section
    }

    private void Menu() {
        // clear all the previous items in the window then add new ones in their place Advantages:: all windows settings re intact such as size and position
        panel.removeAll();
        OverridePanelLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        // create main control buttions for each table Creade view and remove
        JButton create = new JButton("Create record");
        create.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        create.setPreferredSize(new Dimension(200, 120));
        create.setBackground(new Color(0x043304)); // RGB=32, 128, 32
        create.setForeground(cfg.windowThemingColours.get("TextColour"));
        create.addActionListener(e->CreateWindow());
        c.gridx = 1;
        c.gridy = 1;
        panel.add(create, c);

        JButton modview = new JButton("Modify/View/delete record");
        modview.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        modview.setPreferredSize(new Dimension(200, 120));
        modview.setBackground(new Color(0x03033C)); // RGB=32, 32, 128
        modview.setForeground(cfg.windowThemingColours.get("TextColour"));
        modview.addActionListener(e->ViewModWindow());
        c.gridx = 3;
        c.gridy = 1;
        panel.add(modview, c);

    }

    private void CreateWindow() { //create window that controlls all the creating of a new record
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

            //this gets all the names of the columns in the database table
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
                if(field.get("type").asInt() == 4) {
                    System.out.println(field.get("type").asInt());
                    c.gridy++;
                    JButton foreignBtn = new JButton("find Foreign key");
                    foreignBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                    foreignBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
                    foreignBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                    foreignBtn.addActionListener(e -> {
                        Map<String, Object>map = OM.convertValue(field, Map.class);
                        map.put("value", entry.getText());
                        this.foreignKey(map).thenAccept(results -> {
                            System.out.println("Foreign return:"+results);
                            if (results >= 0) {
                                entry.setText(results+"");
                            }
                        });



                    });
                    panel.add(foreignBtn, c);
                    c.gridy--;
                }
                c.gridy--;
            }

            //shows a confirmation window
            JButton ConfirmBtn = new JButton("append");
            ConfirmBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            ConfirmBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
            ConfirmBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            ConfirmBtn.addActionListener(e->{if (Confirmation()) {
                try {
                    //creates a json data that will be sent to the server and will show if it was a success it not
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
            panel.add(ConfirmBtn, new GridBagConstraints(0,5,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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

    //works similarly to the create window
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
            //add all the names of columns in the table in the database
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
                if(field.get("type").asInt() == 4) {
                    System.out.println(field.get("type").asInt());
                    c.gridy++;
                    JButton foreignBtn = new JButton("find Foreign key");
                    foreignBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                    foreignBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
                    foreignBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                    foreignBtn.addActionListener(e -> {
                        Map<String, Object>map = OM.convertValue(field, Map.class);
                        map.put("value", entry.getText());
                        this.foreignKey(map).thenAccept(results -> {
                            System.out.println("Foreign return:"+results);
                            if (results >= 0) {
                                entry.setText(results+"");
                            }
                        });



                    });
                    panel.add(foreignBtn, c);
                    c.gridy--;
                }
                c.gridy--;
            }
            //uses the data entered to query the databse if the record exists and places the retrieved data into the correct boxes
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
                        // the record was foung and will be placed it the boxes
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
                                    try {
                                        field.setText(rootNode.get(fieldName).get("value").asText());
                                    }catch (Exception ex) {
                                        field.setText(rootNode.get(fieldName).asText());
                                    }
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
                            //create and send a json string to the server that will delete the record
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
                            //initialize gridbag constrintts
                            GridBagConstraints gbc = new GridBagConstraints();
                            gbc.fill = GridBagConstraints.BOTH;
                            gbc.weightx = 1.0;
                            gbc.weighty = 1.0;
                            gbc.gridx = 3;
                            gbc.gridy = c.gridy+2;

                            panel.add(DelBtn, new GridBagConstraints(2,5,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                            JButton ModBtn = new JButton("Modify");
                            ModBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
                            ModBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
                            ModBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
                            ModBtn.addActionListener(e1 -> {
                                //creates and sends a modify command to the server that will modify the record based on the primary key of the record
                                try {
                                    Map<String, Object> values = getDataFromTextFields(panel, array);
                                    String message = "{\"mode\":\"modify\",\"tableName\":\""+this.name+"\",\"data\":"+OM.writeValueAsString(values)+",\"oldData\":"+OM.writeValueAsString(data)+",\"authentication\":"+OM.writeValueAsString(Main.persistMemJson.get("loginCreds"))+"}";
                                    System.out.println(message);
                                    String removeRes = soc.sendMessage(message);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }

                            });
                            gbc.gridx++;
                            panel.add(ModBtn, new GridBagConstraints(3,5,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                            System.out.println("added buttons?");
                            panel.revalidate();
                            panel.repaint();
                        }



                    }
                    else {
                        JOptionPane.showMessageDialog(rootPane, "error code: "+Jres.get("code").asText()+"\n"+"Description: "+Jres.get("description").asText(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});
            c.gridy += 2;
            c.gridx = 1;
            panel.add(FindBtn, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            panel.setBackground(cfg.windowThemingColours.get("MainBG"));
            repaint();
            revalidate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected CompletableFuture<Integer> foreignKey(Map<String, Object> foreingDataType) {
        System.out.println("foreignKey");
        if (!foreingDataType.containsKey("TableName")) {return CompletableFuture.completedFuture(-1);}
        if (!foreingDataType.containsKey("value")) {return CompletableFuture.completedFuture(-2);}
        Window root = new Window(foreingDataType.get("TableName").toString());
        root.setBackground(cfg.windowThemingColours.get("MainBG"));
        root.OverridePanelLayout(new GridBagLayout());
        root.OverridePanelClose(DISPOSE_ON_CLOSE);

        Map<String, Object> table = null;
        for (Map<String,Object> tableobject : Main.tables) {
            if (tableobject.get("tableName").toString().equals(foreingDataType.get("TableName").toString())) {
                table = tableobject;
            }
        }
        if(table == null) {return CompletableFuture.completedFuture(-3);}
        System.out.println("TABLE: "+table.keySet()+"\n"+table.values()+"\n"+table.get("tableName").toString());

        Map<String, Object> primaryField = null;
        ArrayList<Map<String, Object>> fields = (ArrayList<Map<String, Object>>) table.get("array");
        for(Map<String, Object>field : fields) {if ((Boolean) field.get("isPrimary") == true) {primaryField = field;}}

        ClientSock socket = ClientSock.getInstance();
        ObjectMapper OM = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("field", primaryField.get("name").toString());
        data.put("value", foreingDataType.get("value").toString());
        Map<String, Object> result = null;
        try {
            result = !foreingDataType.get("value").toString().isEmpty() ? OM.readValue(socket.sendMessage("{\"mode\":\"search\",\"tableName\":\"" + table.get("tableName") + "\",\"data\":" + OM.writeValueAsString(data) + ",\"authentication\":" + OM.writeValueAsString(Main.persistMemJson.get("loginCreds")) + "}"), Map.class) : null;
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        for (int i = 0; i < fields.size(); i++) {
            System.out.println("Fields"+i+"  "+fields.get(i).values().toString());
            Label Namelabel = new Label(fields.get(i).get("name").toString());
            Namelabel.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            Namelabel.setForeground(cfg.windowThemingColours.get("TextColour"));
            Namelabel.setBackground(cfg.windowThemingColours.get("MainBG"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.gridx = i;
            gbc.gridy = 0;
            root.panel.add(Namelabel, gbc);
            JTextField entryField = new JTextField();
            entryField.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
            entryField.setForeground(cfg.windowThemingColours.get("TextColour"));
            entryField.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
            entryField.setName(fields.get(i).get("name").toString());
            entryField.setText((result==null || ((Integer) result.get("code"))!=0) ? null : ((Map<String,Object>) result.get("data")).get(fields.get(i).get("name").toString()).toString());
            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.weightx = 1.0;
            gbc1.weighty = 1.0;
            gbc1.gridx = i;
            gbc1.gridy = 1;
            root.panel.add(entryField, gbc1);
            System.out.println();
        }
        JButton SearchBtn = new JButton("search");
        SearchBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        SearchBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
        SearchBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> futureResult = new CompletableFuture<>();
        SearchBtn.addActionListener(e1 -> {
            try {
                Map<String, Object> data1 = new HashMap<>();

                for (Component comp :  root.panel.getComponents()) {
                    if (comp instanceof JTextField) {
                        JTextField textField = (JTextField) comp;
                        String fieldName = textField.getName();
                        System.out.println(fieldName);
                        for(Map<String, Object> field1 : fields) {
                            System.out.println("entry: "+field1.get("name")+" "+field1.get("type"));
                            if(!field1.get("name").toString().equals(fieldName) || textField.getText().isEmpty()) {
                                continue;
                            }
                            System.out.println(fieldName+" "+textField.getText());
                            data.put("field", fieldName);
                            data.put("value", textField.getText());
                        }
                    }
                }
                String btnsearch = socket.sendMessage("{\"mode\":\"search\",\"tableName\":\"" + foreingDataType.get("TableName") + "\",\"data\":" + OM.writeValueAsString(data) + ",\"authentication\":" + OM.writeValueAsString(Main.persistMemJson.get("loginCreds")) + "}");
                System.out.println(btnsearch);
                Map<String,Object> btnsearchtemp = OM.readValue(btnsearch, Map.class);
                Map<String,Object> rootNode = (Map<String,Object>) btnsearchtemp.get("data");
                for (Component comp :  root.panel.getComponents()) {
                    System.out.println(1+"\t"+comp);
                    if (comp instanceof JTextField) {
                        System.out.println(2);

                        JTextField field2 = (JTextField) comp;
                        String fieldName = field2.getName();

                        if(!rootNode.containsKey(fieldName)) {
                            continue;
                        } else {
                            System.out.println(3);
                            field2.setText(rootNode.get(fieldName).toString());
                        }
                    }
                }
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        root.panel.add(SearchBtn, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        JButton SetBtn = new JButton("set");
        SetBtn.setFont(new Font("Corbel Regular", Font.PLAIN, 20));
        SetBtn.setForeground(cfg.windowThemingColours.get("TextColour"));
        SetBtn.setBackground(cfg.windowThemingColours.get("SecondaryBG"));
        Map<String, Object> finalPrimaryField = primaryField;
        SetBtn.addActionListener(e1 -> {
            executor.submit(() -> {
                for (Component comp :  root.panel.getComponents()) {
                    if (comp instanceof JTextField && ((JTextField) comp).getName().equals(finalPrimaryField.get("name").toString())) {
                        try {
                            futureResult.complete(Integer.parseInt(((JTextField) comp).getText().toString()));
                            root.dispose();
                            return;
                        }catch (NumberFormatException ex) {
                            futureResult.completeExceptionally(new IllegalArgumentException("Invalid number format"));
                        }

                    }
                }
                finalPrimaryField.get("name").toString();
            });
        });
        root.panel.add(SetBtn, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        root.run();
        return futureResult;
    }
}
