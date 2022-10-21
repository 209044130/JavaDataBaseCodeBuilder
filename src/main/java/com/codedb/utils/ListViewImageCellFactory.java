package com.codedb.utils;

import com.codedb.model.StaticImage;
import com.codedb.model.ToolsNodeData;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ListViewImageCellFactory
{
    public static ListCell<ToolsNodeData> getListViewImageCell(){
        ListCell<ToolsNodeData> listCell = new ListCell<ToolsNodeData>() {
            @Override
            protected void updateItem(ToolsNodeData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setText(null);
                    this.setGraphic(null);
                    return;
                }
                HBox hBox = new HBox();
                Image image = null;
                switch (item.getType())
                {
                    case ToolsNodeData.DB_NAME ->  image = StaticImage.db;
                    case ToolsNodeData.ADD_TABLE ->   image = StaticImage.add;
                    case ToolsNodeData.REMOVE_DB,ToolsNodeData.REMOVE_TABLE ->  image = StaticImage.delete;
                    case ToolsNodeData.TABLE_NAME ->  image = StaticImage.table;
                    case ToolsNodeData.SHOW_TABLE_STRUCTURE ->  image = StaticImage.info;
					case ToolsNodeData.DB_TO_RESULTSET, ToolsNodeData.RESULTSET_TO_DB, ToolsNodeData.RESULTSET_TO_TEXT, ToolsNodeData.TEXT_TO_DB -> image = StaticImage.createCode;
                }
                hBox.getChildren().add(new ImageView(image));
                this.setFont(new Font(14));
                this.setText(item.getName());
                this.setGraphic(hBox);
            }
        };
        return listCell;
    }
}
