package com.codedb.controller;

import com.codedb.utils.FrameManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddDataBase
{

    @FXML
    private TextField textDataBaseName;

    @FXML
    private Button btnCreate;

    public void onBtnCreateAction(ActionEvent actionEvent){
        String dbName = textDataBaseName.getText();
        FrameMain parent = (FrameMain) FrameManager.getController("FrameMain");
        if (parent != null)
        {
            String error = parent.handleAddDataBaseFrame(dbName);
            if(error.equals("") )
            {
                FrameManager.closeFrame("AddDataBase");
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR,error);
                alert.show();
            }
        }
    }

}
