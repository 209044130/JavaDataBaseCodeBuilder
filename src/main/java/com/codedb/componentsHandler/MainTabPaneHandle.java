package com.codedb.componentsHandler;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codedb.model.HistoryItemData;
import com.codedb.model.TableInfoData;
import com.codedb.utils.DBTools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class MainTabPaneHandle
{	public static TabPane tabPane;

    public static void register(TabPane tp) {
        tabPane = tp;
    }

    /**
     * @Description 显示表的结构信息
     * @Params [parentTitle 父节点（数据库）名称, title 表名]
     **/
    public static void showTableInfo(Connection con, String parentTitle, String title) {
        String tabName = title + "(" + parentTitle + ") 结构";
        // 检查当前是否已经有该标签
        for (Tab temp : tabPane.getTabs()) {
            if (temp.getText().equals(tabName)) {
                tabPane.getSelectionModel().select(temp);
                return;
            }
        }
        // 创建新的tab
        ConcurrentMap<String, String> table = new ConcurrentHashMap<>();
        try {
            table = DBTools.getTableStructure(con, parentTitle, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tab tableInfo = new Tab();
        tableInfo.setClosable(true);
        tableInfo.setText(tabName);
        VBox vBox = new VBox();
        TableView<TableInfoData> tableView = new TableView<TableInfoData>();
        TableColumn<TableInfoData, String> tableColumn1 = new TableColumn<TableInfoData, String>("字段名");
        TableColumn<TableInfoData, String> tableColumn2 = new TableColumn<TableInfoData, String>("类型");
        tableColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableColumn1.setMinWidth(180);
        tableColumn2.setMinWidth(180);
        tableView.getColumns().addAll(tableColumn1, tableColumn2);
        // 添加到数据源列表
        ObservableList<TableInfoData> data = FXCollections.observableArrayList();
        for (String k : table.keySet()) {
            TableInfoData d = new TableInfoData(k, table.get(k));
            data.add(d);
        }
        // 设置数据源
        tableView.setItems(data);
        tableInfo.setContent(tableView);
        tabPane.getTabs().add(tableInfo);
        tabPane.getSelectionModel().select(tableInfo);
    }

    /**
     * @Description 用于建立代码生成后的显示tab
     * @Params [title tab标题, content 文本内容]
     **/
    public static void create(String title, String content) {
        // 检查当前是否已经有该标签
        for (Tab temp : tabPane.getTabs()) {
            // 如果有则更新内容
            if (temp.getText().equals(title)) {
                TextArea tempTA = (TextArea) temp.getContent();
                tempTA.clear();
                tempTA.setText(content);
                tabPane.getSelectionModel().select(temp);
                return;
            }
        }
        Tab tab = new Tab();
        tab.setText(title);
        tab.setClosable(true);
        TextArea textArea = new TextArea();
        textArea.setText(content);
        tab.setContent(textArea);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

	// 清除所有页签
	public static void clearAll() {
		tabPane.getTabs().clear();
		MainHistoryHandle.add("关闭所有页签", HistoryItemData.WARNING);
	}

	public static void clearAllStructureTab() {
		List<Tab> tempTab = new LinkedList<>();
		for (Tab temp : tabPane.getTabs()) {
			if (temp.getText().contains("结构"))
				tempTab.add(temp);
		}
		for (Tab i : tempTab) {
			tabPane.getTabs().remove(i);
		}
		MainHistoryHandle.add("关闭所有表结构页签", HistoryItemData.WARNING);
	}

	public static void clearAllResultTab() {
		List<Tab> tempTab = new LinkedList<>();
		for (Tab temp : tabPane.getTabs()) {
			if (temp.getText().contains("从数据集到数据库") || temp.getText().contains("从页面到数据库")
					|| temp.getText().contains("从数据库到数据集") || temp.getText().contains("从数据集到页面"))
				tempTab.add(temp);
		}
		for (Tab i : tempTab) {
			tabPane.getTabs().remove(i);
		}
		MainHistoryHandle.add("关闭所有代码生成结果页签", HistoryItemData.WARNING);
	}
}
