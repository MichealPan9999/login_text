package com.pan.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

public class LoginServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 * */
	private static final long serialVersionUID = 1L;
	// JDBC 驱动名及数据库 URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/login";
	// 数据库的用户名与密码，需要根据自己的设置
	static final String USER = "root";
	static final String PASS = "";

	public LoginServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		Statement stmt = null;
		// doGet(request, response);
		// 设置编码为utf-8
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		// 获取用户名和密码
		String name = request.getParameter("name");
		String pwd = request.getParameter("pwd");
		String type = request.getParameter("type");
		boolean matchFlag = false;
		System.out.println("name = " + name + " , pwd = " + pwd + " , type = "
				+ type);
		try {
			// 注册 JDBC 驱动器
			Class.forName(JDBC_DRIVER);
			// 打开一个连接
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			// 执行 SQL 查询
			stmt = conn.createStatement();
			String sql;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", type);
			if (Integer.parseInt(type) == 0) {
				sql = "SELECT name, pwd FROM user";
				ResultSet rsquery = stmt.executeQuery(sql);
				// 展开结果集数据库
				while (rsquery.next()) {
					// 通过字段检索
					String dbName = rsquery.getString("name");
					if (dbName.equals(name)) {
						matchFlag = true;
						break;
					}else{
						matchFlag = false;
					}
				}
				int rs = -1;
				if (!matchFlag) {
					sql = "INSERT INTO user (name , pwd) VALUES(\'" + name + "\',\'" + pwd
							+ "\')";
					rs = stmt.executeUpdate(sql);
				}
				System.out.println("insert into value  rs = "+rs);
				if (rs>0) {
					jsonObject.put("result", true);
				}else{
					jsonObject.put("result", false);
				}
			} else {
				sql = "SELECT name, pwd FROM user";
				ResultSet rs = stmt.executeQuery(sql);

				// 展开结果集数据库
				while (rs.next()) {
					// 通过字段检索
					String dbName = rs.getString("name");
					String dbPwd = rs.getString("pwd");
					if (dbName.equals(name) && dbPwd.equals(pwd)) {
						HttpSession session = request.getSession();// 获取session
						session.setAttribute("name", name);// 将用户名和密码保存在session中
						session.setAttribute("pwd", pwd);// 将用户名和密码保存在session中
						matchFlag = true;
						break;
					} else {
						matchFlag = false;
					}
				}
				// 校验用户名和密码是否正确
				if (matchFlag) {// 验证成功
					jsonObject.put("result", true);
					jsonObject.put("name", name);
					//可以携带 名字、性别、年龄、兴趣、爱好等相关信息
				} else {// 校验不成功，则留在跳转到login.jsp页面
					jsonObject.put("result", false);
				}
			}
			System.out.println("=========jsonObject==========="+jsonObject);
			out.println(jsonObject);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ClassNotFoundException!!!");
			e.printStackTrace();
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
