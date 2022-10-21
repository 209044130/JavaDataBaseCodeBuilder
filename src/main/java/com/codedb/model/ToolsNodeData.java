package com.codedb.model;

public class ToolsNodeData
{
	public static final int DB_NAME = 0;
	public static final int ADD_TABLE = 1;
	public static final int REMOVE_DB = 10;

	public static final int TABLE_NAME = 100;
	public static final int SHOW_TABLE_STRUCTURE = 101;
	public static final int RESULTSET_TO_DB = 102;
	public static final int TEXT_TO_DB = 103;
	public static final int DB_TO_RESULTSET = 104;
	public static final int RESULTSET_TO_TEXT = 105;
	public static final int REMOVE_TABLE = 110;

    private String name = "";
	private Integer type = 0;
	private String dbName = "";
	private String tableName = "";

	public ToolsNodeData(String name, Integer type, String dbName)
    {
        this.name = name;
        this.type = type;
		this.dbName = dbName;
    }

	public ToolsNodeData(String name, Integer type, String dbName, String tableName) {
		this.name = name;
		this.type = type;
		this.dbName = dbName;
		this.tableName = tableName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }
}
