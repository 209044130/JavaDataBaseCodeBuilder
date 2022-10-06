package com.codedb.codedb;

import com.codedb.utils.DBTools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application
{

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        //登录数据库
        FXMLLoader loginXML = new FXMLLoader(this.getClass().getResource("login.fxml"));
        Parent p = loginXML.load();
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
    }
}
