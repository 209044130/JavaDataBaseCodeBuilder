package com.codedb.utils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DBTools
{
	/**
	 * @Description 获取mysql连接
	 * @Params [userName:用户名, password：密码]
	 * @Return Connection对象
	 **/
	public static Connection connectToDB(String userName, String password) throws ClassNotFoundException, SQLException
    {
        Connection conn = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf-8",
				userName, password);
		return conn;
	}

	/**
	 * @Description 获取数据库列表
	 * @Params [connection]
	 * @Return 数据库名称列表
	 **/
	public static List<String> getDatabasesName(Connection connection) throws Exception {
		ResultSet schemas = connection.getMetaData().getCatalogs();
		List<String> dbNames = new LinkedList<>();
		while (schemas.next())
        {
			dbNames.add((String) schemas.getObject("TABLE_CAT"));
        }
		return dbNames;
    }

	/**
	 * @Description 获取表名列表
	 * @Params [connection, dbName]
	 * @Return
	 **/
	public static List<String> getTablesName(Connection connection, String dbName) throws Exception {
		DatabaseMetaData metaData = connection.getMetaData();
		List<String> ret = new LinkedList<>();

		ResultSet tableResultSet = metaData.getTables(dbName, "%", "%", new String[]{"TABLE"});
		ConcurrentMap<String, ConcurrentMap<String, String>> db = new ConcurrentHashMap<>();
		// 获取所有表
		while (tableResultSet.next()) {
			String tableName = tableResultSet.getString("TABLE_NAME");
			ret.add(tableName);
		}
		return ret;
	}

	/**
	 * @Description 获取数据库结构
	 * @Params [connection, dbName 数据库名称]
	 * @Return
	 **/
	public static ConcurrentMap<String, ConcurrentMap<String, String>> getDBStructure(Connection connection,
			String dbName) throws Exception {
		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet tableResultSet = metaData.getTables(dbName, "%", "%", new String[]{"TABLE"});
		ConcurrentMap<String, ConcurrentMap<String, String>> db = new ConcurrentHashMap<>();
		// 获取所有表
		while (tableResultSet.next()) {
			String tableName = tableResultSet.getString("TABLE_NAME");
			ConcurrentMap<String, String> table = new ConcurrentHashMap<>();

			// 获取表字段结构
			ResultSet columnResultSet = metaData.getColumns(dbName, "%", tableName, "%");
			while (columnResultSet.next()) {
				// 字段名称
				String columnName = columnResultSet.getString("COLUMN_NAME");
				// 数据类型
				String columnType = columnResultSet.getString("TYPE_NAME");
				table.put(columnName, columnType);
				db.put(tableName, table);
			}
		}
		return db;
	}

	/**
	 * @Description 获取表结构
	 * @Params [connection, dbName 数据库名称, tableName 表名]
	 * @Return
	 **/
	public static ConcurrentMap<String, String> getTableStructure(Connection connection, String dbName,
			String tableName) throws Exception {
		DatabaseMetaData metaData = connection.getMetaData();
		ConcurrentMap<String, String> ret = new ConcurrentHashMap<>();
		// 获取表字段结构
		ResultSet columnResultSet = metaData.getColumns(dbName, "%", tableName, "%");
		while (columnResultSet.next()) {
			// 字段名称
			String columnName = columnResultSet.getString("COLUMN_NAME");
			// 数据类型
			String columnType = columnResultSet.getString("TYPE_NAME");
			ret.put(columnName, columnType);
		}
		return ret;
	}

}
