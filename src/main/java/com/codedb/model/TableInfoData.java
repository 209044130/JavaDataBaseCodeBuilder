package com.codedb.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableInfoData
{
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty type = new SimpleStringProperty("");

    public TableInfoData(String name, String type)
    {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
    }

    public String getName()
    {
        return name.get();
    }

    public void setName(String name)
    {
        this.name = new SimpleStringProperty(name);
    }

    public String getType()
    {
        return type.get();
    }

    public void setType(String type)
    {
        this.type = new SimpleStringProperty(type);
    }
}
