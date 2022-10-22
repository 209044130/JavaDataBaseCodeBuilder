package com.codedb.controller;

import com.codedb.componentsHandler.MainHistoryHandle;
import com.codedb.exception.ConnectionPoolBuzy;
import com.codedb.utils.DBTools;
import com.codedb.utils.ManagedConnection;

import com.mysql.cj.protocol.Protocol;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSearch {

	private ManagedConnection mcon;

	private String DBName = "";
	// 最大显示行数
	private static long maxSearchRows = 100;

	@FXML
	private Button btnClear;
	@FXML
	private Button btnRun;
	@FXML
	private ChoiceBox<Integer> btnMaxSearchRows;
	@FXML
	private Button btnRunAll;
	@FXML
	private Button btnRunSearch;
	@FXML
	private Label labelNowDB;
	@FXML
	private TextArea textSQLCode;
	@FXML
	private TextArea textResult;

	public ManagedConnection init(String DBName) throws ConnectionPoolBuzy {
		this.DBName = DBName;
		// 连接到数据库
		mcon = DBTools.connectToDB(DBName);
		labelNowDB.setText("当前数据库：" + DBName);
		btnMaxSearchRows.getSelectionModel().select(0);
		btnMaxSearchRows.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> {
			maxSearchRows = newVal;
			MainHistoryHandle.add("查询最大行数限制由" + oldVal + "改变为" + newVal);
		});
		return mcon;
	}

	public void btnClearAction(ActionEvent actionEvent) {
		textSQLCode.setText("");
	}

	public void btnRunAction(ActionEvent actionEvent) {
		try {
			textResult.setWrapText(true);
			Connection con = mcon.con;
			String sql = textSQLCode.getSelectedText();
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			preparedStatement.execute();
			textResult.setText("执行完成！");
			textResult.setStyle("-fx-background-color: green");
		} catch (SQLException e) {
			String msg = e.getMessage();
			// 显示查询结果
			textResult.setText(msg);
			textResult.setStyle("-fx-background-color: red");
		}
	}

	public void btnRunAllAction(ActionEvent actionEvent) {
		try {
			textResult.setWrapText(true);
			Connection con = mcon.con;
			String sql = textSQLCode.getText();
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			preparedStatement.execute();
			textResult.setText("执行完成！");
			textResult.setStyle("-fx-background-color: green");
		} catch (SQLException e) {
			String msg = e.getMessage();
			// 显示查询结果
			textResult.setText(msg);
			textResult.setStyle("-fx-background-color: red");
		}
	}

	public void btnRunSearchAction(ActionEvent actionEvent) {
		try {
			textResult.setWrapText(false);
			Connection con = mcon.con;
			String sql = textSQLCode.getText();
			PreparedStatement preparedStatement = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = preparedStatement.executeQuery();
			StringBuilder res = new StringBuilder();
			res.append("查询完成！\n");
			List<Map<Integer, Object>> list = converResultSetToList(rs);
			rs.last();
			long rowCount = rs.getRow();
			res.append(rowCount > maxSearchRows
					? "一共有" + rowCount + "条结果，只显示前" + maxSearchRows + "条\n"
					: "一共有" + rowCount + "条结果\n");
			for (Map<Integer, Object> line : list) {
				res.append(line.values().toString());
				res.append("\n");
			}
			textResult.setText(res.toString());
			textResult.setStyle("-fx-background-color: green");
		} catch (SQLException e) {
			String msg = e.getMessage();
			// 显示查询结果
			textResult.setText(msg);
			textResult.setStyle("-fx-background-color: red");
		}
	}

	/**
	 * @Description
	 * @Params [resultSet]
	 **/
	private static List<Map<Integer, Object>> converResultSetToList(ResultSet resultSet) throws SQLException {
		if (null == resultSet) {
			return null;
		}
		List<Map<Integer, Object>> data = new ArrayList<>();
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<Integer, Object> rowData = new HashMap<Integer, Object>();
		// 将字段名放入结果返回
		for (int i = 0; i < columnCount; i++) {
			rowData.put(i + 1, rsmd.getColumnName(i + 1));
		}
		// 将每个字段的结果返回
		data.add(rowData);
		int count = 0;
		while (resultSet.next()) {
			if (count >= maxSearchRows)
				break;
			rowData = new HashMap<Integer, Object>();
			for (int i = 0; i < columnCount; i++) {
				rowData.put(i + 1, resultSet.getObject(i + 1));
			}
			data.add(rowData);
			count++;
		}
		return data;
	}

}