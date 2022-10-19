package com.codedb.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.codedb.model.TableInfoData;
import com.codedb.utils.DBTools;
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
	private Connection con;

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

	public void init(Connection con) {
		this.con = con;
	}

	public void btnAddAction(ActionEvent actionEvent) {
		table.setEditable(true);
		observableList.add(new TableInfoData());
		table.setItems(observableList);
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

	public void btnShowSQLAction(ActionEvent actionEvent) {
		textPreviewSQL.setText(createSQL());
	}

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
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			preparedStatement.execute();
			FrameMain frameMain = (FrameMain) FrameManager.getController("FrameMain");
			List<String> list = new LinkedList<>();
			try {
				list = DBTools.getDatabasesName(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
			frameMain.init(list);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public String createSQL() {
		String tableName = textTableName.getText();
		if (tableName.equals("")) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "请设置表名!");
			alert.show();
			return "";
		}
		String sql = "CRAETE TABLE " + tableName + " (\n";
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
					+ " " + comment + "\n";
		}
		sql += ")";
		return sql;
	}

}
