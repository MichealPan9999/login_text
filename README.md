# login_text
使用Android与servlet通讯实现简单登录功能
v1.2服务器访问数据库：
添加一个数据库
  create database login;
  use login;
 创建表：
  CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(20) NOT NULL DEFAULT '' COMMENT 'username',
  `pwd` char(20) NOT NULL DEFAULT '' COMMENT 'password',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
效果如下：
mysql> CREATE TABLE `user` (
    ->   `id` int(11) NOT NULL AUTO_INCREMENT,
    ->   `name` char(20) NOT NULL DEFAULT '' COMMENT 'username',
    ->   `pwd` char(20) NOT NULL DEFAULT '' COMMENT 'password',
    ->   PRIMARY KEY (`id`)
    -> ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
Query OK, 0 rows affected (0.08 sec)

mysql> show tables;
+-----------------+
| Tables_in_login |
+-----------------+
| user            |
+-----------------+
1 row in set (0.00 sec)
插入两条数据：
insert into user values('1','admin','123');
insert into user values('2','pan','123456');
查询数据库得到
mysql> select * from user;
+----+-------+--------+
| id | name  | pwd    |
+----+-------+--------+
|  1 | admin | 123    |
|  2 | pan   | 123456 |
+----+-------+--------+
2 rows in set (0.10 sec)

下面是连接数据方法 
首先将数据库驱动文件mysql-connector-java-5.1.39-bin.jar拷贝到WebRoot/WEB-INF/lib目录下
添加如下代码：
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
  ...
  Connection conn = null;
		Statement stmt = null;
    ...
    try {
			// 注册 JDBC 驱动器
			Class.forName(JDBC_DRIVER);
			// 打开一个连接
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			// 执行 SQL 查询
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT name, pwd FROM user";
			ResultSet rs = stmt.executeQuery(sql);

			// 展开结果集数据库
			while (rs.next()) {
				// 通过字段检索
				String dbName = rs.getString("name");
				String dbPwd = rs.getString("pwd");
				if(dbName.equals(name)&&dbPwd.equals(pwd))
				{
					HttpSession session = request.getSession();// 获取session
					session.setAttribute("name", name);// 将用户名和密码保存在session中
					session.setAttribute("pwd", pwd);// 将用户名和密码保存在session中
					matchFlag = true;
					break;
				}else
				{
					matchFlag = false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ClassNotFoundException!!!");
			e.printStackTrace();
		} 
