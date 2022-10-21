package com.codedb.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.codedb.componentsHandler.MainHistoryHandle;
import com.codedb.model.HistoryItemData;
import com.codedb.model.TableInfoData;
import com.codedb.utils.FrameManager;
import com.codedb.utils.TableCheckBoxCellFactory;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class AddTable {
	// 存放当前连接的名称
	private Connection con;
	// 存放当前数据库的名称
	private String dbName;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnDelete;

	@FXML
	private TextField textTableName;

	@FXML
	private TableView<TableInfoData> table;

	@FXML
	private TextArea textPreviewSQL;

	@FXML
	private TableColumn<TableInfoData, String> columnName, typeName, charOctetLength, numPrecRadix, remarks;

	@FXML
	private TableColumn<TableInfoData, Boolean> isNullable, isKey;

	// 用于列表的数据
	private ObservableList<TableInfoData> observableList = FXCollections.observableArrayList();

	// 初始化
	public void init(Connection con, String dbName) {
		this.con = con;
		this.dbName = dbName;
		columnName.setCellFactory(TextFieldTableCell.forTableColumn());
		columnName.setCellValueFactory(TableInfoData.getStringValueCell(1));
		columnName.setOnEditCommit(event -> {
			TableView<TableInfoData> tempTable = event.getTableView();
			TableInfoData tableInfoData = tempTable.getItems().get(event.getTablePosition().getRow());
			tableInfoData.setColumnName(event.getNewValue());// 放置新值
		});
		typeName.setCellFactory(TextFieldTableCell.forTableColumn());
		typeName.setCellValueFactory(TableInfoData.getStringValueCell(2));
		typeName.setOnEditCommit(event -> {
			TableView<TableInfoData> tempTable = event.getTableView();
			TableInfoData tableInfoData = tempTable.getItems().get(event.getTablePosition().getRow());
			tableInfoData.setTypeName(event.getNewValue());// 放置新值
		});
		charOctetLength.setCellFactory(TextFieldTableCell.forTableColumn());
		charOctetLength.setCellValueFactory(TableInfoData.getStringValueCell(3));
		charOctetLength.setOnEditCommit(event -> {
			TableView<TableInfoData> tempTable = event.getTableView();
			TableInfoData tableInfoData = tempTable.getItems().get(event.getTablePosition().getRow());
			tableInfoData.setCharOctetLength(event.getNewValue());// 放置新值
		});
		numPrecRadix.setCellFactory(TextFieldTableCell.forTableColumn());
		numPrecRadix.setCellValueFactory(TableInfoData.getStringValueCell(4));
		numPrecRadix.setOnEditCommit(event -> {
			TableView<TableInfoData> tempTable = event.getTableView();
			TableInfoData tableInfoData = tempTable.getItems().get(event.getTablePosition().getRow());
			tableInfoData.setNumPrecRadix(event.getNewValue());// 放置新值
		});
		remarks.setCellFactory(TextFieldTableCell.forTableColumn());
		remarks.setCellValueFactory(TableInfoData.getStringValueCell(5));
		remarks.setOnEditCommit(event -> {
			TableView<TableInfoData> tempTable = event.getTableView();
			TableInfoData tableInfoData = tempTable.getItems().get(event.getTablePosition().getRow());
			tableInfoData.setRemarks(event.getNewValue());// 放置新值
		});
		isNullable.setCellFactory(
				TableCheckBoxCellFactory.tableCheckBoxColumn(new Callback<Integer, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(Integer index) {
						final TableInfoData tableInfoData = table.getItems().get(index);
						ObservableValue<Boolean> retval = new SimpleBooleanProperty(tableInfoData.isNullable());
						retval.addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								tableInfoData.setNullable(newValue);
							}
						});
						return retval;
					}
				}));
		isNullable.setCellValueFactory(TableInfoData.getBooleanValueCell(1));
		isKey.setCellFactory(
				TableCheckBoxCellFactory.tableCheckBoxColumn(new Callback<Integer, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(Integer index) {
						final TableInfoData tableInfoData = table.getItems().get(index);
						ObservableValue<Boolean> retval = new SimpleBooleanProperty(tableInfoData.isKey());
						retval.addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								tableInfoData.setKey(newValue);
							}
						});
						return retval;
					}
				}));
		isKey.setCellValueFactory(TableInfoData.getBooleanValueCell(2));
	}

	// 添加字段
	public void btnAddAction(ActionEvent actionEvent) {
		table.setEditable(true);
		observableList.add(new TableInfoData());
		table.setItems(observableList);
	}

	// 删除当期字段
	public void btnDeleteAction(ActionEvent actionEvent) {
		// 获取当前选中的项目
		Integer index = table.getSelectionModel().getSelectedIndex();
		if (index == -1) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "您未选中任何字段!");
			alert.show();
			return;
		}
		observableList.remove(index.intValue());
		table.setItems(observableList);
	}

	// 显示当前 SQL 代码
	public void btnShowSQLAction(ActionEvent actionEvent) {
		textPreviewSQL.setText(createSQL());
	}

	// 执行
	public void btnCreateAction(ActionEvent actionEvent) {
		String tableName = textTableName.getText();
		if (tableName.equals("")) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "请设置表名!");
			alert.show();
			return;
		}
		String sql = createSQL();
		if (sql.equals("")) {
			return;
		}
		try {
			PreparedStatement preparedStatement1 = con.prepareStatement("use " + dbName + ";");
			preparedStatement1.execute();
			PreparedStatement preparedStatement2 = con.prepareStatement(sql);
			preparedStatement2.execute();
			// 刷新表
			FrameMain frameMain = (FrameMain) FrameManager.getController("FrameMain");
			frameMain.initLeftTreeView();
		} catch (SQLException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
			alert.show();
		}
		// 历史记录
		MainHistoryHandle.add("创建表<" + tableName + ">成功", HistoryItemData.SUCCESS);

	}

	/**
	 * @Description 生成当前的sql代码
	 * @Return 返回当前即将执行的slq代码
	 **/
	public String createSQL() {
		String tableName = textTableName.getText();
		if (tableName.equals("")) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "请设置表名!");
			alert.show();
			return "";
		}
		String sql = "CREATE TABLE " + tableName + " (\n";
		Integer count = table.getItems().size();
		for (TableInfoData temp : table.getItems()) {
			if (temp.getColumnName().equals("") || temp.getTypeName().equals("")) {
				Alert alert = new Alert(Alert.AlertType.WARNING, "请填写完成!");
				alert.show();
				return "";
			}
			// 数据长度
			String typeAfter = temp.getCharOctetLength().equals("")
					? ""
					: "(" + temp.getCharOctetLength()
							+ (temp.getNumPrecRadix().equals("") ? "" : "," + temp.getNumPrecRadix()) + ")";
			// 是否为空
			String nullFlag = temp.isNullable() ? "DEFAULT NULL" : "NOT NULL";
			// 是否是主键
			String keyFlag = temp.isKey() ? "PRIMARY KEY" : "";
			// 注释
			String comment = temp.getRemarks().equals("") ? "" : "COMMENT '" + temp.getRemarks() + "'";
			sql += "\t" + temp.getColumnName() + " " + temp.getTypeName() + typeAfter + " " + nullFlag + " " + keyFlag
					+ " " + comment + (table.getItems().indexOf(temp) == count - 1 ? "\n" : ",\n");
		}
		sql += ");";
		// 返回sql语句
		return sql;
	}

}
