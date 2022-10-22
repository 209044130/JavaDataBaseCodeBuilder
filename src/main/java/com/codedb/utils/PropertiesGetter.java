package com.codedb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.control.Alert;

public class PropertiesGetter
{
    private static Properties properties = new Properties();

    public static boolean init(){
        try
        {
            InputStream in = PropertiesGetter.class.getResourceAsStream("/com/codedb/config.properties");
            properties.load(in);
            return true;
        } catch (IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR,"读取配置文件失败!");
            alert.show();
            e.printStackTrace();
            return false;
        }
    }

    public static Object get(String key){
        return properties.get(key);
    }
}
