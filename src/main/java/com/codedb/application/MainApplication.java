package com.codedb.application;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import com.codedb.controller.FrameMain;
import com.codedb.utils.DBTools;
import com.codedb.utils.FrameManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication {
	public Connection connection = null;
	public FrameMain frameMain = null;

	// 获得connection对象，启动主界面
	public void start(Connection con) {
		if (con == null) {
			return;
		}
		connection = con;
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/codedb/fxml/frameMain.fxml"));
		Parent p = null;
		try {
			// 加载并且保存控制类
			p = loader.load();
			frameMain = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(p);
		scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
		Stage stage = new Stage();
		stage.setScene(scene);
		init();
		stage.show();
		FrameManager.setFrame("FrameMain", loader.getController(), stage);
	}

	/**
	 * @Description 初始化
	 */
	public void init() {
		frameMain.setCon(connection);
		initDBTreeView();
	}

	/**
	 * @Description 初始化
	 * @Params []
	 * @Return
	 **/
	private void initDBTreeView() {
		List<String> list = new LinkedList<>();
		try {
			list = DBTools.getDatabasesName(this.connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		frameMain.init(list);
	}
}