package com.sj.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.sj.utils.LogUtil;

public class JdbcPool implements DataSource {
	// static final String DB_URL = "#####################";
	// static final String USER = "#############";
	// static final String PASS = "##############";
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/dedecmsv57gbk";
	static final String USER = "root";
	static final String PASS = "123qwe";
	private static LinkedList<Connection> listConnections = new LinkedList<Connection>();
	static {
		try {
			Class.forName(JDBC_DRIVER);
			LogUtil.print("Connecting to database...");
			for (int i = 0; i < 10; i++) {
				Connection conn;
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				// ����ȡ�������ݿ����Ӽ��뵽listConnections�����У�listConnections���ϴ�ʱ����һ����������ݿ����ӵ����ӳ�
				listConnections.add(conn);
			}
			LogUtil.print("init listConnections���ݿ����ӳش�СΪ"
					+ listConnections.size());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		LogUtil.print("getConnection listConnections���ݿ����ӳش�СΪ"
				+ listConnections.size());
		if (listConnections.size() > 0) {
			// ��listConnections�����л�ȡһ�����ݿ�����
			final Connection conn = listConnections.removeFirst();
			LogUtil.print("listConnections���ݿ����ӳش�С��" + listConnections.size());
			// ����Connection����Ĵ������
			Class[] interfaces = conn.getClass().getInterfaces();
			if (interfaces == null || interfaces.length <= 0) {
				interfaces = new Class[1];
				interfaces[0] = Connection.class;
			}
			return (Connection) Proxy.newProxyInstance(JdbcPool.class
					.getClassLoader(), interfaces,
					new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							if (!method.getName().equals("close")) {
								return method.invoke(conn, args);
							} else {
								// ������õ���Connection�����close�������Ͱ�conn�������ݿ����ӳ�
								listConnections.add(conn);
								LogUtil.print(conn
										+ "������listConnections���ݿ����ӳ��ˣ���");
								LogUtil.print("listConnections���ݿ����ӳش�СΪ"
										+ listConnections.size());
								return null;
							}
						}
					});
		} else {
			throw new RuntimeException("�Բ������ݿ�æ");
		}
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
