package me.cambria22118626;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String,Color> windowThemingColours = new HashMap<>();

    public Config() {
        Path path = new File(System.getenv("USERPROFILE")+"\\.coursework-config").toPath();
        if(Files.exists(new File(System.getenv("USERPROFILE")+"\\.coursework-config").toPath())) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> map = mapper.readValue(new File((path+"\\mainCFG.json")), Map.class);
                ArrayList<Map<String,Object>> colourlist = (ArrayList<Map<String,Object>>) map.get("colours");
                for (Map<String, Object> colourObject : colourlist) {
                    windowThemingColours.put((String) colourObject.get("name"), new Color((Integer) colourObject.get("red"), (Integer) colourObject.get("green"), (Integer) colourObject.get("blue")));
                }
            }
            catch (IOException e) {
                System.out.println("oopsie poopsie");
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
}
