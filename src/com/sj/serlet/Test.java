package com.sj.serlet;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sj.jdbc.JDBCHandler;
import com.sj.utils.LogUtil;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
	JDBCHandler mJDBCHandler;
	Map<String, String> attrMap = new HashMap<String, String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Test() {
		super();
		// TODO Auto-generated constructor stub
		mJDBCHandler = new JDBCHandler();
		String data = mJDBCHandler.query("SELECT *  FROM dede_arcatt");

		JSONArray jsonArray = JSONArray.fromObject(data);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			attrMap.put(object.getString("att"), object.getString("attname"));
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      http://localhost:8080/JavaWebApp/action/read?table=dede_archives
	 *      http://localhost:8080/JavaWebApp/action/read?arttype=0
	 *      http://localhost
	 *      :8080/JavaWebApp/action/read?arttype=1&start=0&end=20
	 *      http://localhost
	 *      :8080/JavaWebApp/action/read?arttype=1&start=0&end=20
	 * 
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = "hello server!!!!";
		System.out.println("原始数据的大小为：" + data.getBytes().length);
		Map<String, String[]> map = request.getParameterMap();
		LogUtil.print("getQueryString =" + request.getQueryString());
		LogUtil.print("getPathInfo =" + request.getPathInfo());
		LogUtil.print("getParameter =" + request.getParameter("table"));
		LogUtil.print("map.size() =" + map.size());
		String sql = "";
		if (request.getPathInfo().contains("read")) {
			if (request.getQueryString().contains("table")) {
				sql = "SELECT * FROM " + request.getParameter("table");
				data = mJDBCHandler.query(sql);
			} else if (request.getQueryString().contains("arttype")) {
				sql = createSQLForArcType(request.getParameter("arttype"),
						request.getParameter("start"),
						request.getParameter("end"));
				data = handleData(mJDBCHandler.query(sql),
						request.getParameter("arttype"));
			} else if (request.getQueryString().contains("aid")
					&& request.getQueryString().contains("typeid")) {
				sql = createSQLForAddonarticle(request.getParameter("aid"),
						request.getParameter("typeid"));
				data = mJDBCHandler.query(sql);
			}
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

	private String handleData(String data, String reid) {
		if (!reid.equals("0")) {
			JSONArray jsonArray = JSONArray.fromObject(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				object.put("flag", attrMap.get(object.getString("flag")));
			}
			data = jsonArray.toString();
		}
		return data;
	}

	private String createSQLForAddonarticle(String aid, String typeid) {
		String sql = "SELECT body  FROM dede_addonarticle Where aid=" + aid
				+ " AND " + "typeid=" + typeid;
		return sql;
	}

	private String createSQLForArcType(String reid, String param1, String param2) {
		String sql = "";
		switch (reid) {
		case "0":
			sql = "SELECT ID,typename  FROM dede_arctype Where reID = 0";
			break;
		default:
			if (param1 == null || param2 == null)
				sql = "SELECT dede_archives.id,dede_archives.typeid,dede_archives.title,dede_archives.senddate,"
						+ "dede_archives.shorttitle,dede_archives.writer,dede_archives.description,"
						+ "dede_archives.flag,dede_archives.keywords"
						+ " FROM dede_arctype,dede_archives"
						+ " Where "
						+ " dede_archives.typeid=dede_arctype.id AND"
						+ " dede_arctype.id IN (SELECT dede_arctype.id FROM dede_arctype WHERE dede_arctype.reID="
						+ reid
						+ " OR "
						+ " dede_arctype.id="
						+ reid
						+ ")"
						+ " ORDER BY dede_archives.senddate DESC";
			else
				sql = "SELECT dede_archives.id,dede_archives.typeid,dede_archives.title,dede_archives.senddate,"
						+ "dede_archives.shorttitle,dede_archives.writer,dede_archives.description,"
						+ "dede_archives.flag,dede_archives.keywords"
						+ " FROM dede_arctype,dede_archives"
						+ " Where "
						+ " dede_archives.typeid=dede_arctype.id AND"
						+ " dede_arctype.id IN (SELECT dede_arctype.id FROM dede_arctype WHERE dede_arctype.reID="
						+ reid
						+ " OR "
						+ " dede_arctype.id="
						+ reid
						+ ")"
						+ " ORDER BY dede_archives.senddate DESC"
						+ " LIMIT "
						+ param1 + "," + param2;
			break;
		}
		return sql;
	}


}
