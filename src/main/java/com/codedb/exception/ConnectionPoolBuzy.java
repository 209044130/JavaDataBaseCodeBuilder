package com.codedb.exception;

public class ConnectionPoolBuzy extends  RuntimeException
{

    public ConnectionPoolBuzy(String message)
    {
        super(message);
    }
}
