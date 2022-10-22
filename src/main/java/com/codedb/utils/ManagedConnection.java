package com.codedb.utils;

import java.sql.Connection;
import java.util.UUID;

public class ManagedConnection
{
    public Connection con;
    public UUID uuid;

    public ManagedConnection(UUID uuid, Connection con)
    {
        this.con = con;
        this.uuid = uuid;
    }
}
