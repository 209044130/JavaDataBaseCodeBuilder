package com.codedb.application;

import java.io.IOException;
import java.sql.Connection;

import com.codedb.componentsHandler.MainTabPaneHandle;
import com.codedb.controller.FrameMain;
import com.codedb.utils.FrameManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApplication {
	// 保存连接类
	public Connection connection = null;
	// 保存主页面控制类
	public FrameMain frameMain = null;

	/**
	 * @Description 启动主界面
	 **/
	public void start(Connection con) {
		if (con == null) {
			return;
		}
		connection = con;
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/codedb/fxml/frameMain.fxml"));
		try {
			// 加载并且保存控制类
			Parent p = loader.load();
			frameMain = loader.getController();
			Scene scene = new Scene(p);
			// 加载css文件
			// scene.getStylesheets().add(getClass().getResource("/com/codedb/css/main.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			init();
			stage.show();
			FrameManager.setFrame("FrameMain", loader.getController(), stage);
		} catch (IOException e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR, "主界面加载失败,可能是文件损坏,请尝试重新安装。");
			alert.show();
		}
	}

	/**
	 * @Description 初始化
	 */
	public void init() {
		// 控制类保存连接类
		frameMain.setCon(connection);
		// 控制类界面数据初始化
		frameMain.init();
		// 显示欢迎介绍页面
		MainTabPaneHandle.createHelloTab();
	}


}
