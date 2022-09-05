package com.bing.lan;

import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by lanbing at 2022/8/23 13:46
 */

public class PrepareStatementTest {

  Connection con;

  @Before
  public void setup() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      con = DriverManager.getConnection("jdbc:mysql://14.18.46.103:10000/hibernate-xx", "root", "KhWl40W@myqtrsqldb#180");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test1() throws SQLException {
    PreparedStatement p = con.prepareStatement("select * from ?");
    p.setString(1, "hotleave");
    // 打印完整真实的 sql
    System.out.println(p);
  }

  @Test
  public void test2() throws SQLException {
    PreparedStatement pp = con.prepareStatement("update product_ set ver=?, price=? where id=? and ver=?");
    pp.setInt(1, 77);
    pp.setFloat(2, 1.0f);
    pp.setInt(3, 1);
    pp.setInt(4, 76);

    pp.execute();
  }
}
