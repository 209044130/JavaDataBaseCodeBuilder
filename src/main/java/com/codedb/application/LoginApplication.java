package com.codedb.application;

import java.io.IOException;

import com.codedb.utils.FrameManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		// 登录数据库
		FXMLLoader loginXML = new FXMLLoader(getClass().getResource("/com/codedb/fxml/login.fxml"));
		Parent p = null;
		try {
			p = loginXML.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(p);
		stage.setScene(scene);
		stage.show();
		FrameManager.setFrame("Login", loginXML.getController(), stage);
	}
}
