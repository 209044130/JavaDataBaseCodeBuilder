package com.codedb.controller;

import java.sql.Connection;
import java.sql.SQLException;

import com.codedb.application.MainApplication;
import com.codedb.utils.DBTools;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Login {
	public static Connection con = null;

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
			con = DBTools.connectToDB(userNameText, passWordText);
			Stage stage = (Stage) statusText.getScene().getWindow();
			stage.close();
			MainApplication mainApplication = new MainApplication();
			mainApplication.start(con);
		} catch (SQLException ex) {
			statusText.setText(ex.getMessage());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
}
