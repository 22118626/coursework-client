package me.cambria22118626;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



public class Config {
    private static Config instance = null;

    private static File loc = new File(System.getenv("USERPROFILE")+"\\.coursework-config");

    protected static Map<String,Color> windowThemingColours = new HashMap<>();
    protected static Map<String, Object> allNodes = new HashMap<>();

    private Config() {
        ObjectMapper mapper = new ObjectMapper();
        if(!Files.exists(Path.of(loc.getAbsolutePath() + "\\MainCFG.json"))) {
            try {
                createDefaultConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //parses data from mainCFG json file to allow customisation of the program
            Map<String, Object> map = mapper.readValue(new File(loc+"\\mainCFG.json"), Map.class);
            ArrayList<Map<String,Object>> colourlist = (ArrayList<Map<String,Object>>) map.get("colours");
            for (Map<String, Object> colourObject : colourlist) {
                this.windowThemingColours.put((String) colourObject.get("name"), new Color((Integer) colourObject.get("red"), (Integer) colourObject.get("green"), (Integer) colourObject.get("blue")));
            }
            this.allNodes = mapper.readValue(new File(loc+"\\mainCFG.json"), Map.class);
        }
        catch (IOException e) {
            System.out.println("oopsie poopsie");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }


    }
    //singleton get instance method
    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    // only ran if corrupted or the file does not exist
    private static synchronized void createDefaultConfig() throws IOException {
        File file = new File(System.getenv("USERPROFILE")+"\\.coursework-config\\mainCFG.json");
        if (!file.exists()) {
            HashMap<String, Object> MainBGcolour = new HashMap<>();
            MainBGcolour.put("name","MainBG");
            MainBGcolour.put("red", 28);
            MainBGcolour.put("green",29);
            MainBGcolour.put("blue",32);

            HashMap<String, Object> SecBGcolour = new HashMap<>();
            SecBGcolour.put("name","SecondaryBG");
            SecBGcolour.put("red", 45);
            SecBGcolour.put("green",46);
            SecBGcolour.put("blue",50);

            HashMap<String, Object> Textcolour = new HashMap<>();
            Textcolour.put("name","TextColour");
            Textcolour.put("red", 244);
            Textcolour.put("green", 244);
            Textcolour.put("blue",244);

            HashMap<String, Object> TertiaryBGcolour = new HashMap<>();
            TertiaryBGcolour.put("name","TertiaryBG");
            TertiaryBGcolour.put("red", 37);
            TertiaryBGcolour.put("green",38);
            TertiaryBGcolour.put("blue",42);


            ArrayList<Map<String, Object>> ColoursObjects = new ArrayList<>();
            ColoursObjects.add(MainBGcolour);
            ColoursObjects.add(SecBGcolour);
            ColoursObjects.add(Textcolour);

            Map<String, Object> ةشحش = new HashMap<>();
            ةشحش.put("colours",ColoursObjects);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, ةشحش);
        }

    }

    public static File getFile() {
        return loc;
    }

    protected static void commit() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File out = new File(loc.getAbsolutePath()+"\\mainCFG.json");
        mapper.writeValue(out, allNodes);
    }

}
