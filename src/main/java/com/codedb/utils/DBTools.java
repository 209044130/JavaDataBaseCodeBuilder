package com.codedb.utils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codedb.model.TableInfoData;

public class DBTools {
	/**
	 * @Description 获取mysql连接
	 * @Params [userName:用户名, password：密码]
	 * @Return Connection对象
	 **/
	public static Connection connectToDB(String userName, String password) throws ClassNotFoundException, SQLException {
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
		while (schemas.next()) {
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
	public static ConcurrentMap<String, TableInfoData> getTableStructure(Connection connection, String dbName,
			String tableName) throws Exception {
		DatabaseMetaData meta = connection.getMetaData();
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
