package com.bing.lan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by backend.
 */

public class PrepareStatementTest {

  public static void main(String[] args) throws Exception {

    Class.forName("com.mysql.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://14.18.46.103:10000/hibernate-xx", "root", "KhWl40W@myqtrsqldb#180");
    PreparedStatement p = con.prepareStatement("select * from ?");
    p.setString(1, "hotleave");
    // 打印完整真实的 sql
    System.out.println(p.toString());
  }
}
