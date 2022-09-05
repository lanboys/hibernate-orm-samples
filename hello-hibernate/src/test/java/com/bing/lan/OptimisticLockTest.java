package com.bing.lan;

import com.bing.lan.pojo.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by backend.
 */

public class OptimisticLockTest {

  SessionFactory sessionFactory;

  @Before
  public void setup() {
    sessionFactory = new Configuration().configure().buildSessionFactory();
  }

  @Test
  public void test() {
    Session s1 = sessionFactory.openSession();

    s1.beginTransaction();

    // 不查询 直接更新
    Product p2 = new Product();
    p2.setId(2);
    p2.setVersion(22);
    p2.setPrice(22);
    p2.setName("name");

    System.out.println("=========: " + p2);

    s1.update(p2);
    s1.getTransaction().commit();
    s1.close();

    sessionFactory.close();
  }

  @Test
  public void test1() {
    Session s1 = sessionFactory.openSession();
    s1.beginTransaction();

    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);
    System.out.println("main2(): " + p1);

    // 查询后，手动修改版本号 不生效, 还是获取前面查询时 一级缓存 中的版本号, 执行sql后, 版本号还是会恢复实际版本号
    p1.setVersion(p1.getVersion() + 5);
    System.out.println("main3(): " + p1);

    s1.update(p1);
    s1.getTransaction().commit();
    s1.close();

    sessionFactory.close();
  }

  @Test
  public void test2() throws InterruptedException {
    // 测试并发时间，乐观锁失败的问题
    Session s1 = sessionFactory.openSession();

    try {
      s1.beginTransaction();

      Product p1 = (Product) s1.get(Product.class, 1);
      p1.setPrice(p1.getPrice() + 1000);

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          Session s2 = sessionFactory.openSession();
          s2.beginTransaction();

          Product p2 = (Product) s2.get(Product.class, 1);
          p2.setPrice(p2.getPrice() + 1000);
          s2.update(p2);

          s2.getTransaction().commit();
          s2.close();
        }
      });
      t.start();
      t.join();

      // 为了保证第二个事务稍后执行
      Thread.sleep(1000);

      s1.update(p1);
      // 可以不 update 直接 flush();
      // s1.flush();

      s1.getTransaction().commit();

    } catch (Exception e) {
      e.printStackTrace();
      s1.getTransaction().rollback();
    } finally {
      s1.close();
    }

    sessionFactory.close();
  }

  /**
   * 测试事务自动提交
   * <p>
   * 控制事务是否自动提交 默认是false
   * <property name="hibernate.connection.autocommit">true</property>
   * <p>
   * s1.doWork(new Work() {
   *
   *    @Override
   *    public void execute(Connection connection) throws SQLException {
   *        connection.setAutoCommit(true);
   *    }
   * });
   */
  @Test
  public void test3() {
    Session s1 = sessionFactory.openSession();

    // 自动提交事务
    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);

    s1.update(p1);
    s1.flush();// 没flush, 不发送sql

    s1.close();
    sessionFactory.close();
  }

  @Test
  public void test4() {
    Session s1 = sessionFactory.openSession();

    // 自动提交事务
    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);

    s1.update(p1);
    s1.flush();// 没flush, 不发送sql

    System.out.println("test4(): ----------");

    Session s2 = sessionFactory.openSession();
    Product p2 = (Product) s2.get(Product.class, 1);
    p2.setPrice(p2.getPrice() + 1002);

    // 手动开启事务
    s2.beginTransaction();

    // 这里手动修改版本号不行，因为存在一级缓存了，除非前面加上 s1.clear();
    // 注意 一级缓存 跟 Session 有关，跟事务 没关，开不开启事务，都会有缓存，有缓存 版本号就优先使用缓存中的版本号
    p2.setVersion(p2.getVersion() + 5);

    s2.update(p2);
    s2.getTransaction().commit();
    s2.close();

    sessionFactory.close();
  }

  @Test
  public void test5() {
    Session s1 = sessionFactory.openSession();

    // 自动提交事务
    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);

    s1.update(p1);
    s1.flush();// 没flush, 不发送sql

    System.out.println("test5(): ----------");

    // 手动开启事务
    s1.beginTransaction();

    Product p2 = (Product) s1.get(Product.class, 1);
    p2.setPrice(p2.getPrice() + 1002);

    s1.update(p2);

    s1.getTransaction().commit();
    s1.close();
    sessionFactory.close();
  }

  @Test
  public void test6() {
    Session s1 = sessionFactory.openSession();
    s1.beginTransaction();

    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);
    System.out.println("test6(): " + p1);

    // 一个事务里面，更新两次，乐观锁版本号会增加两次
    s1.update(p1);
    s1.flush();
    System.out.println("test6(): --------");

    p1.setPrice(p1.getPrice() + 1003);
    s1.update(p1);
    s1.flush();
    System.out.println("test6(): " + p1);

    s1.getTransaction().commit();
    s1.close();

    sessionFactory.close();
  }
}
