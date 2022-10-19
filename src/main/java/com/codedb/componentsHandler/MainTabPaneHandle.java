package com.codedb.componentsHandler;

import java.sql.Connection;
import java.util.ArrayList;
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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

public class MainTabPaneHandle {
	public static TabPane tabPane;

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
		ConcurrentMap<String, TableInfoData> table = new ConcurrentHashMap<>();
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
		TableColumn<TableInfoData, String> tableColumn3 = new TableColumn<TableInfoData, String>("长度");
		TableColumn<TableInfoData, String> tableColumn4 = new TableColumn<TableInfoData, String>("小数点");
		TableColumn<TableInfoData, Boolean> tableColumn5 = new TableColumn<TableInfoData, Boolean>("非NULL");
		TableColumn<TableInfoData, Boolean> tableColumn6 = new TableColumn<TableInfoData, Boolean>("键");
		TableColumn<TableInfoData, String> tableColumn7 = new TableColumn<TableInfoData, String>("注释");
		tableColumn1.setCellValueFactory(TableInfoData.getStringValueCell(1));
		tableColumn2.setCellValueFactory(TableInfoData.getStringValueCell(2));
		tableColumn3.setCellValueFactory(TableInfoData.getStringValueCell(3));
		tableColumn4.setCellValueFactory(TableInfoData.getStringValueCell(4));
		tableColumn5.setCellValueFactory(TableInfoData.getBooleanValueCell(1));
		tableColumn5.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumn5));
		tableColumn6.setCellValueFactory(TableInfoData.getBooleanValueCell(2));
		tableColumn6.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumn6));
		tableColumn7.setCellValueFactory(TableInfoData.getStringValueCell(5));
		tableView.getColumns().addAll(tableColumn1, tableColumn2, tableColumn3, tableColumn4, tableColumn5,
				tableColumn6, tableColumn7);
		for (TableColumn<TableInfoData, ?> temp : tableView.getColumns()) {
			temp.setMinWidth(20);
		}
		// 添加到数据源列表
		ObservableList<TableInfoData> data = FXCollections.observableArrayList();
		for (String k : table.keySet()) {
			TableInfoData d = table.get(k);
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

	/**
	 * @Description 打开欢迎界面(介绍)
	 **/
	public static void createHelloTab() {
		String title = "欢迎使用";
		for (Tab temp : tabPane.getTabs()) {
			if (temp.getText().equals(title)) {
				tabPane.getSelectionModel().select(temp);
				return;
			}
		}
		String content = "1.数据库工具使用方法\n" + "\t点击右侧树形图的数据库，在左下方就会出现对应的数据库工具栏\n" + "\n2.表工具使用方法\n"
				+ "\t点击左侧树形图数据库后展开的表，在左下方就会出现表的工具栏，下面是对每一种工具使用方法的介绍。\n"
				+ "\t(1)\"显示表结构\"：点击后会生成一个新的页签用于展示该表的字段结构，包括字段名称以及字段类型。\n"
				+ "\t(2)\"从数据集到数据库\"：点击后会生成该表对应的将数据集写入数据库的Java代码模板。\n"
				+ "\t(3)\"从页面到数据库\"：点击后会生成该表对应的将页面文本框数据写入数据库的Java代码模板。\n"
				+ "\t(4)\"从数据库到数据集\"：点击后会生成该表对应的将数据库读入数据集的Java代码模板。\n"
				+ "\t(5)\"从数据集到页面\"：点击后会生成该表对应的将数据集写入页面文本框的Java代码模板。\n";

		Tab tab = new Tab();
		tab.setText(title);
		tab.setClosable(true);
		TextArea textArea = new TextArea();
		textArea.setText(content);
		tab.setContent(textArea);
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}

	public static void addTab(Tab tab) {
		// 防止出现重名
		String finalText = tab.getText();
		String preText = tab.getText();
		Integer index = 1;
		List<String> texts = new ArrayList<>();
		for (Tab temp : tabPane.getTabs()) {
			texts.add(temp.getText());
		}
		while (texts.contains(finalText)) {
			finalText = preText + "(" + index + ")";
			index++;
		}
		tab.setText(finalText);
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
