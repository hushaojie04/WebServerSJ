package com.sj.serlet;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sj.jdbc.JDBCHandler;
import com.sj.utils.LogUtil;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
	JDBCHandler mJDBCHandler;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
		// TODO Auto-generated constructor stub
		mJDBCHandler = new JDBCHandler();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      http://localhost:8080/JavaWebApp/action/read?table=dede_archives
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = "hello server!!!!";
		System.out.println("原始数据的大小为：" + data.getBytes().length);
		Map<String, String[]> map = request.getParameterMap();
		LogUtil.print("getQueryString =" + request.getQueryString());
		LogUtil.print("getPathInfo =" + request.getPathInfo());
		LogUtil.print("getParameter =" + request.getParameter("app"));
		LogUtil.print("map.size() =" + map.size());
		if (request.getPathInfo().contains("read")) {
			data = mJDBCHandler.query(request.getParameter("table"));
		}
		byte g[] = data.getBytes();
		// response.setHeader("Content-Encoding", "gzip");
		response.setHeader("Content-Length", g.length + "");
		response.getOutputStream().write(g);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
