package com.codedb.model;

public class ToolsNodeData
{
	public static final int DB_NAME = 0;

    private String name = "";
    private Integer type = 0;

    public ToolsNodeData(String name, Integer type)
    {
        this.name = name;
        this.type = type;
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
