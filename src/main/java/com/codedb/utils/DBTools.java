package com.codedb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", userName, password);
		return conn;
	}

	/**
	 * @Description 获取数据库列表
	 * @Params [connection]
	 * @Return 数据库名称列表
	 **/
	public static List<String> showDatabases(Connection connection) throws Exception {
		ResultSet schemas = connection.getMetaData().getCatalogs();
		List<String> dbNames = new LinkedList<>();
		while (schemas.next())
        {
			dbNames.add((String) schemas.getObject("TABLE_CAT"));
        }
		return dbNames;
    }
}
