package com.linked.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bd {
	private String connectionUrl = "jdbc:mysql://localhost:3306/linked";
	private String connectionUsername = "root";
	private String connectionPassword = "1234";
	public Connection connection;
	public PreparedStatement preparedStatement = null;
	public ResultSet resultSet = null;

	public static void main(String[] args) {
		Bd bd = new Bd();
		bd.connect();
		bd.close();
	}

	public boolean connect() {
		try {
			connection = DriverManager.getConnection(connectionUrl, connectionUsername, connectionPassword);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
