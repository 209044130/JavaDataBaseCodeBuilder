package com.codedb.componentsHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import com.codedb.controller.AddTable;
import com.codedb.controller.FrameMain;
import com.codedb.model.HistoryItemData;
import com.codedb.model.TableInfoData;
import com.codedb.utils.DBTools;
import com.codedb.utils.FrameManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

/**
 * @Description Tools功能绑定
 **/
public class MainToolsFunctionBinder {
	// 点击数据库名称
	public static void dbName(String dbName) {
		MainStatusHandle.set("选中数据库 " + dbName);
	}

	// 创建表
	public static void addTable(Connection con, String dbName) {
		FXMLLoader loader = new FXMLLoader(MainToolsFunctionBinder.class.getResource("/com/codedb/fxml/addTable.fxml"));
		try {
			Parent p = loader.load();
			Tab tab = new Tab();
			tab.setText("添加表(" + dbName + ")");
			tab.setContent(p);
			MainTabPaneHandle.addTab(tab);
			AddTable addTable = loader.getController();
			addTable.init(con);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// 删除数据库
	public static void removeDB(Connection con, String dbName) {
		// 保存错误信息，以及确认是否报错.
		MainProgressHandle.set(0);
		String error = "";
		try {
			PreparedStatement preparedStatement = con.prepareStatement("DROP DATABASE " + dbName + ";");
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
			MainProgressHandle.set(0.5);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				preparedStatement.execute();
				List<String> list = new LinkedList<>();
				try {
					list = DBTools.getDatabasesName(con);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FrameMain parent = (FrameMain) FrameManager.getController("FrameMain");
				parent.init(list);
				MainHistoryHandle.add("删除数据库<" + dbName + ">成功", HistoryItemData.SUCCESS);
			}
		} catch (SQLException e) {
			error = e.getMessage();
		}
		if (!error.equals("")) {
			// 添加到历史记录
			MainHistoryHandle.add("删除数据库<" + dbName + ">失败", HistoryItemData.ERROR);
			// 显示异常信息
			Alert alert = new Alert(Alert.AlertType.ERROR, error);
			alert.show();
		}
		MainProgressHandle.set(1);
	}

	// 点击表名
	public static void tableName(String tableName) {
		MainStatusHandle.set("选中表 " + tableName);
	}

	// 显示表信息
	public static void showTableInfo(Connection con, String parentTitle, String title) {
		MainTabPaneHandle.showTableInfo(con, parentTitle, title);
		MainHistoryHandle.add("显示" + title + "(" + parentTitle + ")结构");
	}

	/**
	 * @Description 生成表的resultset到db的代码生成
	 * @Params [con:连接connection, dbName:数据库名称, tableName:表名, showAnnotation:显示注释,
	 *         useIndex:使用下标]
	 **/
	public static void resultSetToDB(Connection con, String dbName, String tableName, boolean showAnnotation,
			boolean useIndex) {
		try {
			ConcurrentMap<String, TableInfoData> fields = DBTools.getTableStructure(con, dbName, tableName);
			// 处理参数
			List<String> params = new LinkedList<>(fields.keySet());
			// 根据参数处理sql2语句的代码模板
			String tempSql2 = "";
			if (useIndex) {
				tempSql2 = "String sql2 = \"INSERT INTO " + tableName + " VALUES(";
			} else {
				tempSql2 = "String sql2 = \"INSERT INTO " + tableName + "(";
				for (int i = 0; i < params.size(); i++) {
					if (i == params.size() - 1)
						tempSql2 += params.get(i) + ")  VALUES(";
					else
						tempSql2 += params.get(i) + ",";
				}
			}
			for (int i = 0; i < params.size(); i++) {
				if (i == params.size() - 1)
					tempSql2 += "?)\";";
				else
					tempSql2 += "?,";
			}

			String res = "";
			// 基本模板
			res += "try {\n" + (showAnnotation ? "\t//编写sql1语句用于获取ResultSet对象\n" : "")
					+ "\tString sql1 = \"SELECT * FROM " + tableName + " WHERE <条件>\";\n"
					+ (showAnnotation ? "\t//通过sql1从con对象获取PreparedStatement对象\n" : "")
					+ "\tPreparedStatement statement = con.prepareStatement(sql1);\n"
					+ (showAnnotation ? "\t//通过statement进行数据库操作获取ResultSet对象\n" : "")
					+ "\tResultSet resultSet = statement.executeQuery();\n"
					+ (showAnnotation ? "\t//编写sql2语句用于将数据插入表" + tableName + "\n" : "") + "\t" + tempSql2 + "\n"
					+ (showAnnotation ? "\t//遍历ResultSet对象，将数据通过statement执行批量插入操作\n" : "")
					+ "\twhile (resultSet.next()) {\n";
			// 循环遍历字段名
			for (int i = 0; i < params.size(); i++) {
				if (useIndex) {
					res += "\t\tstatement.setObject(" + (i + 1) + ", resultSet.getObject(" + (i + 1) + "));\n";
				} else {
					res += "\t\tstatement.setObject(" + (i + 1) + ", resultSet.getObject(\"" + params.get(i)
							+ "\"));\n";
				}

			}
			res += "\t}\n" + (showAnnotation ? "\t//执行批量操作\n" : "") + "\tstatement.executeBatch();\n"
					+ "\tcon.close();\n" + "} catch (Exception e) {\n" + "\te.printStackTrace();\n" + "}\n";
			MainTabPaneHandle.create(tableName + "(" + dbName + ")" + "从数据集到数据库", res);
			MainHistoryHandle.add("生成" + tableName + "(" + dbName + ") 从数据集到数据库代码", HistoryItemData.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 从页面text输入框读入数据并且存入数据库 代码生成
	 * @Params [con:~, dbName:~, tableName:~, showAnnotation:~, useIndex:~]
	 **/
	public static void frameToDB(Connection con, String dbName, String tableName, boolean showAnnotation) {
		try {
			ConcurrentMap<String, TableInfoData> fields = DBTools.getTableStructure(con, dbName, tableName);
			// 处理参数
			List<String> params = new LinkedList<>(fields.keySet());
			// 根据参数处理sql1语句的代码模板
			String tempSql1 = "String sql1 = \"INSERT INTO " + tableName + "(";
			for (int i = 0; i < params.size(); i++) {
				if (i == params.size() - 1)
					tempSql1 += params.get(i) + ")  VALUES(";
				else
					tempSql1 += params.get(i) + ",";
			}
			for (int i = 0; i < params.size(); i++) {
				if (i == params.size() - 1)
					tempSql1 += "?)\";";
				else
					tempSql1 += "?,";
			}

			String res = "";
			// 基本模板
			res += "try {\n" + (showAnnotation ? "\t//编写sql1用于将页面数据插入数据库\n" : "") + "\t" + tempSql1 + "\n"
					+ (showAnnotation ? "\t//使用sql1通过con获取PreparedStatement对象\n" : "")
					+ "\tPreparedStatement statement = con.prepareStatement(sql1);\n"
					+ (showAnnotation ? "\t//input_*** 表示text文本框的对象\n" : "");
			// 循环遍历字段名
			for (int i = 0; i < params.size(); i++) {
				res += "\tstatement.setObject(" + (i + 1) + ", input_" + params.get(i) + ".getText().trim());\n";
			}
			res += (showAnnotation ? "\t//执行数据库操作\n" : "") + "\tstatement.executeUpdate();\n" + "\tcon.close();\n"
					+ "} catch (Exception e) {\n" + "\te.printStackTrace();\n" + "}\n";
			MainTabPaneHandle.create(tableName + "(" + dbName + ")" + "从页面到数据库", res);
			MainHistoryHandle.add("生成" + tableName + "(" + dbName + ") 从页面到数据库代码", HistoryItemData.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 从数据库获取数据到resultset
	 * @Params [con, dbName, tableName, showAnnotation]
	 **/
	public static void dbToResultSet(Connection con, String dbName, String tableName, boolean showAnnotation) {
		try {
			ConcurrentMap<String, TableInfoData> fields = DBTools.getTableStructure(con, dbName, tableName);
			// 处理参数
			List<String> params = new LinkedList<>(fields.keySet());
			// 根据参数处理sql1语句的代码模板
			String tempSql1 = "String sql1 = \"SELECT * FROM " + tableName + "\";";
			String res = "";
			// 基本模板
			res += "try {\n" + (showAnnotation ? "\t//编写sql1用于将数据库数据读入数据集\n" : "") + "\t" + tempSql1 + "\n"
					+ (showAnnotation ? "\t//使用sql1通过con获取PreparedStatement对象\n" : "")
					+ "\tPreparedStatement statement = con.prepareStatement(sql1);\n"
					+ (showAnnotation ? "\t//通过statement执行数据库查询操作，获取数据集\n" : "")
					+ "\tResultSet resultSet = statement.executeQuery();\n" + "\tcon.close();\n"
					+ "} catch (Exception e) {\n" + "\te.printStackTrace();\n" + "}\n";
			MainTabPaneHandle.create(tableName + "(" + dbName + ")" + "从数据库到数据集", res);
			MainHistoryHandle.add("生成" + tableName + "(" + dbName + ") 从数据库到数据集", HistoryItemData.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 从数据集到页面 代码生成
	 * @Params [con, dbName, tableName, showAnnotation]
	 **/
	public static void resultSetToFrame(Connection con, String dbName, String tableName, boolean showAnnotation,
			boolean useIndex) {
		try {
			ConcurrentMap<String, TableInfoData> fields = DBTools.getTableStructure(con, dbName, tableName);
			// 处理参数
			List<String> params = new LinkedList<>(fields.keySet());
			String res = "";

			// 基本模板
			res += "try {\n" + (showAnnotation ? "\t//从数据集获取数据到页面\n" : "");
			for (int i = 0; i < params.size(); i++) {
				if (useIndex) {
					res += "\tinput_" + params.get(i) + ".setText(resultSet.getObject(" + (i + 1) + "));\n";
				} else {
					res += "\tinput_" + params.get(i) + ".setText(resultSet.getObject(\"" + params.get(i) + "\"));\n";
				}
			}
			res += "} catch (Exception e) {\n" + "\te.printStackTrace();\n" + "}\n";

			MainTabPaneHandle.create(tableName + "(" + dbName + ")" + "从数据集到页面", res);
			MainHistoryHandle.add("生成" + tableName + "(" + dbName + ") 从数据集到页面", HistoryItemData.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
