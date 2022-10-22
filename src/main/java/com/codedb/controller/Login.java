package com.codedb.controller;

import java.sql.SQLException;

import com.codedb.application.MainApplication;
import com.codedb.utils.DBTools;
import com.codedb.utils.FrameManager;
import com.codedb.utils.ManagedConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Login {
	public static ManagedConnection con = null;

	@FXML
	private Label statusText;

	@FXML
	private TextField userName;

	@FXML
	private TextField passWord;

	@FXML
	private Button connection;

	private final String USERNAME_ERR = "请正确输入用户名";
	private final String PASSWORD_ERR = "请正确输入密码";

	// 开始连接数据库
	public void connect(MouseEvent e) {
		String userNameText = userName.getText();
		String passWordText = passWord.getText();
		if (!userNameText.matches("^\\w+$")) {
			statusText.setText(USERNAME_ERR);
			return;
		}
		if (!passWordText.matches("^\\w+$")) {
			statusText.setText(PASSWORD_ERR);
			return;
		}
		statusText.setText("");
		con = null;
		try {
			userNameText = "root";
			passWordText = "cz2002610";
			con = DBTools.init(userNameText, passWordText);
			if (con == null) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "连接池初始化失败");
				return;
			}
			FrameManager.closeFrame("Login");
			MainApplication mainApplication = new MainApplication();
			mainApplication.start(con);
		} catch (SQLException ex) {
			statusText.setText(ex.getMessage());
		}
	}
}
