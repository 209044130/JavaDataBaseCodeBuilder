package com.codedb.utils;

import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;

public class FrameManager
{
    private static Map<String,Stage> stages = new HashMap<>();
    private static Map<String, Object> controllers = new HashMap<>();

    public static void setFrame(String name, Object controller, Stage stage){
        stages.put(name,stage);
        controllers.put(name,controller);
    }

    public static Stage getStage(String name){
        if (!stages.containsKey(name))
        {
            controllers.remove(name);
            return null;
        }
        return stages.get(name);
    }

    public static Object getController(String name){
        if (!controllers.containsKey(name))
        {
            stages.remove(name);
            return null;
        }
        return controllers.get(name);
    }

    public static void closeFrame(String name){
        if(stages.containsKey(name))
        {
            Stage stage = stages.get(name);
            stage.close();
            stages.remove(name);
        }
        controllers.remove(name);
    }
}
