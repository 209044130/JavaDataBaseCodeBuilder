package com.codedb.utils;
import java.sql.*;

public class DBTools
{
    public static boolean connectToDB()
    {
        Statement stmt = null;
        Connection conn = null;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //数据库名称
            String dbName = "ahutoj";
            String userName = "root";
            String password = "cz2002610";

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, userName, password);
            stmt = conn.createStatement();
            ResultSet count = stmt.executeQuery("select * from problem");
            System.out.println(count);
            stmt.close();
            conn.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
        }
        return true;
    }
}
