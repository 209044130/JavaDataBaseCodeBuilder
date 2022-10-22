package com.codedb.utils;

import java.util.Objects;

import com.codedb.model.StaticImage;
import com.codedb.model.TreeNodeData;

import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class TreeTableImageCellFactory
{
    public static TreeTableCell<TreeNodeData, TreeNodeData> getTreeTableImageCell( ) {
        TreeTableCell<TreeNodeData, TreeNodeData> treeTableCell = new TreeTableCell<TreeNodeData, TreeNodeData>() {
            @Override
            protected void updateItem(TreeNodeData s, boolean b) {
                super.updateItem(s, b);
                if (!b) {
                    HBox hBox = new HBox();
                    Image image = null;
                    if (Objects.equals(s.getType(), TreeNodeData.TABLE_NODE)) {
                        image = StaticImage.table;
                    } else {
                        image = StaticImage.db;
                    }
                    hBox.getChildren().add(new ImageView(image));
                    this.setText(s.getTitle());
                    this.setFont(new Font(14));
                    this.setGraphic(hBox);
                } else {
                    this.setText("");
                    this.setGraphic(null);
                }
            }
        };
        return treeTableCell;
    }

}
