package com.codedb.componentsHandler;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.codedb.model.HistoryItemData;
import com.codedb.utils.DBTools;

/**
 * @Description Tools功能绑定
 **/
public class MainToolsFunctionBinder
{
    public static void dbName(String dbName)
    {
        MainStatusHandle.set("选中数据库 " + dbName);
    }

    public static void tableName(String tableName)
    {
        MainStatusHandle.set("选中表 " + tableName);
    }

    public static void showTableInfo(Connection con, String parentTitle, String title)
    {
        MainTabPaneHandle.showTableInfo(con, parentTitle, title);
        MainHistoryHandle.add("显示" + title + "(" + parentTitle + ")结构");
    }

    public static void resultSetToDB(Connection con, String dbName, String tableName)
    {
        try
        {
            ConcurrentMap<String, String> fields = DBTools.getTableStructure(con, dbName, tableName);
            // 处理参数
            List<String> params = new LinkedList<>(fields.keySet());
            // 根据参数处理sql2语句的代码模板
            String tempSql2 = "String sql2 = \"INSERT INTO " + tableName + " VALUES(";
            for (int i = 0; i < params.size(); i++)
            {
                if (i == params.size() - 1)
                    tempSql2 += params.get(i) + ")  VALUES(";
                else
                    tempSql2 += params.get(i) + ",";
            }
            for (int i = 0; i < params.size(); i++)
            {
                if (i == params.size() - 1)
                    tempSql2 += "?)\";";
                else
                    tempSql2 += "?,";
            }
            String res = "";
            // 基本模板
            res += "try {\n" + "\tString sql1 = \"SELECT * FROM " + tableName + " WHERE <条件>\";\n"
                    + "\tPreparedStatement statement = con.prepareStatement(sql1);\n"
                    + "\tResultSet resultSet = statement.executeQuery();\n" + "\t" + tempSql2 + "\n"
                    + "\twhile (resultSet.next()) {\n";
            for (int i = 0; i < params.size(); i++)
            {
                res += "\t\tstatement.setObject(" + (i + 1) + ", resultSet.getObject(\"" + params.get(i)
                        + "\"));\n";
            }
            res += "\t}\n" + "\tstatement.executeBatch();\n" + "} catch (Exception e) {\n"
                    + "\te.printStackTrace();\n" + "}\n";
            MainTabPaneHandle.create(tableName + "(" + dbName + ")" + "从ResultSet到数据库", res);
            MainHistoryHandle.add("生成" + tableName + "(" + dbName + ") 从ResultSet到数据库代码", HistoryItemData.SUCCESS);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
