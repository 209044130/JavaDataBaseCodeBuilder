package com.codedb.componentsHandler;

import com.codedb.model.HistoryItemData;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MainHistoryHandle
{	public static ListView<HistoryItemData> historyList;

    public static void register(ListView<HistoryItemData> lv) {
        historyList = lv;
        historyList.setCellFactory(new Callback<ListView<HistoryItemData>, ListCell<HistoryItemData>>() {
            @Override
            public ListCell<HistoryItemData> call(ListView<HistoryItemData> historyItemDataListView) {
                ListCell<HistoryItemData> listCell = new ListCell<>() {
                    @Override
                    protected void updateItem(HistoryItemData historyItemData, boolean b) {
                        super.updateItem(historyItemData, b);
                        this.setStyle("-fx-font-size: 14");
                        if (b) {
                            this.setText("");
                            this.setGraphic(null);
                            this.setStyle("-fx-background-color: null");
                        } else {
                            this.setText(historyItemData.getContent());
                            switch (historyItemData.getType()) {
                                case HistoryItemData.NORMAL :
                                    this.setStyle("-fx-background-color: #e9ecef");
                                    break;
                                case HistoryItemData.SUCCESS :
                                    this.setStyle("-fx-background-color: #5ebd00");
                                    break;
                                case HistoryItemData.WARNING :
                                    this.setStyle("-fx-background-color: #ff9933");
                                    break;
                                case HistoryItemData.ERROR :
                                    this.setStyle("-fx-background-color: #ff3300");
                                    break;
                            }
                        }
                    }
                };
                return listCell;
            }
        });
    }

    public static void add(String content) {
        add(0, content, HistoryItemData.NORMAL);
    }

    public static void add(String content, int type) {
        add(0, content, type);
    }

    public static void add(int index, String content, int type) {
        HistoryItemData historyItemData = new HistoryItemData(content, type);
        historyList.getItems().add(index, historyItemData);
    }
}
