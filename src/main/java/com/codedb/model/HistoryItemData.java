package com.codedb.model;

public class HistoryItemData
{
    public static final int NORMAL = 0;
    public static final int SUCCESS = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    public static final int DB_OPERATION = 11;
    public static final int TABLE_OPERATION = 21;

    private String content = "";
    private int type = 0;

    public HistoryItemData(String content, int type)
    {
        this.content = content;
        this.type = type;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
