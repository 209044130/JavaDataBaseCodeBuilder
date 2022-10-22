package com.codedb.utils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codedb.exception.ConnectionPoolBuzy;
import com.codedb.model.TableInfoData;

import javafx.scene.control.Alert;

public class DBTools {
	private static String nowUsername = "";

	private static String nowPassword = "";

	public static ConnectionPool connectionPool = new ConnectionPool();

	public static String getNowUsername() {
		return nowUsername;
	}

	public static void setNowUsername(String nowUsername) {
		DBTools.nowUsername = nowUsername;
	}

	public static String getNowPassword() {
		return nowPassword;
	}

	public static void setNowPassword(String nowPassword) {
		DBTools.nowPassword = nowPassword;
	}

	public static ManagedConnection init(String username, String password) throws SQLException {
		boolean res = connectionPool.init(username, password);
		if (res) {
			System.out.println("连接池初始化成功");
			nowUsername = username;
			nowPassword = password;
			ManagedConnection mcon = connectionPool.get("", username, password);
			return mcon;
		}
		return null;
	}

	/**
	 * @Description 获取mysql连接 @Params [userName:用户名, password：密码]
	 * @Return ManagedConnection
	 */
	public static ManagedConnection connectToDB() throws ConnectionPoolBuzy {
		return connectToDB("", nowUsername, nowPassword);
	}

	public static ManagedConnection connectToDB(String dbName) throws ConnectionPoolBuzy {
		return connectToDB(dbName, nowUsername, nowPassword);
	}

	public static ManagedConnection connectToDB(String username, String password) throws ConnectionPoolBuzy {
		return connectToDB("", username, password);
	}

	public static ManagedConnection connectToDB(String dbName, String username, String password)
			throws ConnectionPoolBuzy {
		ManagedConnection mcon = connectionPool.get(dbName, username, password);
		return mcon;
	}

	/**
	 * @Description 获取数据库列表 @Params [connection] @Return 数据库名称列表
	 */
	public static List<String> getDatabasesName(ManagedConnection connection) throws Exception {
		ResultSet schemas = connection.con.getMetaData().getCatalogs();
		List<String> dbNames = new LinkedList<>();
		while (schemas.next()) {
			dbNames.add((String) schemas.getObject("TABLE_CAT"));
		}
		return dbNames;
	}

	/**
	 * @Description 获取表名列表 @Params [connection, dbName] @Return
	 */
	public static List<String> getTablesName(ManagedConnection connection, String dbName) throws Exception {
		DatabaseMetaData metaData = connection.con.getMetaData();
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
	 * @Description 获取数据库结构 @Params [connection, dbName 数据库名称] @Return
	 */
	public static ConcurrentMap<String, ConcurrentMap<String, String>> getDBStructure(ManagedConnection connection,
			String dbName) throws Exception {
		DatabaseMetaData metaData = connection.con.getMetaData();

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
	 * @Description 获取表结构 @Params [connection, dbName 数据库名称, tableName 表名] @Return
	 */
	public static ConcurrentMap<String, TableInfoData> getTableStructure(ManagedConnection connection, String dbName,
			String tableName) throws Exception {
		DatabaseMetaData meta = connection.con.getMetaData();
		ConcurrentMap<String, TableInfoData> ret = new ConcurrentHashMap<String, TableInfoData>();
		// 获取表字段结构
		ResultSet columnResultSet = meta.getColumns(null, dbName, tableName, "%");
		while (columnResultSet.next()) {
			// 字段名称
			String columnName = columnResultSet.getString("COLUMN_NAME");
			// 数据类型
			String typeName = columnResultSet.getString("TYPE_NAME");
			// 数据长度
			String charOctetLength = columnResultSet.getString("CHAR_OCTET_LENGTH");
			// 小数位数
			String numPrecRadix = columnResultSet.getString("NUM_PREC_RADIX");
			// 获取字段是否可以为空
			boolean isNullable = columnResultSet.getString("IS_NULLABLE").equals("YES") ? true : false;
			// 获取表字段注解信息
			String remarks = columnResultSet.getString("REMARKS");
			// 封装数据
			TableInfoData tempData = new TableInfoData(columnName, typeName, charOctetLength, numPrecRadix, isNullable,
					false, remarks);
			ret.put(columnName, tempData);
		}
		columnResultSet = meta.getPrimaryKeys(null, dbName, tableName);
		while (columnResultSet.next()) {
			String columnName = columnResultSet.getString("COLUMN_NAME");
			ret.get(columnName).setKey(true);
		}
		return ret;
	}
}
