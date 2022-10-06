package com.codedb.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Login {
  @FXML private TextField dbName;

  @FXML private TextField userName;

  @FXML private TextField passWord;

  @FXML private Button connection;

  public void connect(MouseEvent e) {
    String dbNameText = dbName.getText();
    String userNameText = userName.getText();
    String passWordText = passWord.getText();
  }
}
