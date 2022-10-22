package com.codedb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codedb.exception.ConnectionPoolBuzy;
import javafx.scene.control.Alert;

/**
 * @Description 数据库连接池
 **/
public class ConnectionPool {
	// 最大连接数
	protected Integer maxNum = 1;

	// 当前正在空闲状态的连接
	private ConcurrentMap<UUID, ManagedConnection> idleStateConnctions = new ConcurrentHashMap<UUID, ManagedConnection>();
	// 当前正在忙碌状态的连接
	private ConcurrentMap<UUID, ManagedConnection> buzyStateConnctions = new ConcurrentHashMap<UUID, ManagedConnection>();

	/**
	 * @Description 初始化连接池
	 * @Params [username 用户名, password 密码]
	 **/
	public boolean init(String username, String password) {
		int num = Integer.parseInt((String) PropertiesGetter.get("maxConnectionNum"));
		if (num < 1) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "读取配置文件出错！");
			alert.show();
			num = 10;
		}
		this.maxNum = num;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			for (int i = 0; i < maxNum; i++) {
				Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf-8", username, password);
				UUID uuid = UUID.randomUUID();
				ManagedConnection managedConnection = new ManagedConnection(uuid, con);
				idleStateConnctions.put(uuid, managedConnection);
			}
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean canIGet() {
		return idleStateConnctions.size() > 0;
	}

	public ManagedConnection get(String dbName, String username, String password) throws RuntimeException
	{
		if (canIGet()) {
			ArrayList<ManagedConnection> temp = new ArrayList<>(idleStateConnctions.values());
			ManagedConnection managedConnection = temp.get(0);
			try
			{
				Class.forName("com.mysql.cj.jdbc.Driver");
				String handleDBName = dbName.equals("") ? "" : "/" + dbName;
				managedConnection.con = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306" + handleDBName + "?useUnicode=true&characterEncoding=utf-8", username,
						password);
				buzyStateConnctions.put(managedConnection.uuid,managedConnection);
				idleStateConnctions.remove(managedConnection.uuid);
				System.out.println("获取连接:" + managedConnection.uuid.toString() + " 成功!\n当前可用连接数:" + getIdleStateCount() + "\n忙碌连接数:" + getBuzyStateCount());
				return managedConnection;
			} catch (ClassNotFoundException | SQLException e)
			{
				throw new RuntimeException(e);
			}
		}
		throw new ConnectionPoolBuzy("无可用连接");
	}

	public void close(UUID uuid) {
		if (buzyStateConnctions.containsKey(uuid)) {
			try {
				ManagedConnection mcon = buzyStateConnctions.get(uuid);
				buzyStateConnctions.remove(uuid);
				mcon.con.close();
				idleStateConnctions.put(mcon.uuid, mcon);
        		System.out.println("关闭连接:" + mcon.uuid.toString() + " 成功!\n当前可用连接数:" + getIdleStateCount() + "\n忙碌连接数:" + getBuzyStateCount());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getIdleStateCount (){
		return idleStateConnctions.size();
	}

	public int getBuzyStateCount(){
		return buzyStateConnctions.size();
	}
}

