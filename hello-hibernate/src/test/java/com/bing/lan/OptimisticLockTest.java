package com.bing.lan;

import com.bing.lan.pojo.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.MyConfiguration;
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

    // 查询后，手动修改版本号 不生效, 还是获取前面查询时 缓存 中的版本号
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
    Session s2 = sessionFactory.openSession();

    s1.beginTransaction();
    s2.beginTransaction();

    Product p1 = (Product) s1.get(Product.class, 1);
    p1.setPrice(p1.getPrice() + 1000);

    Product p2 = (Product) s2.get(Product.class, 1);
    p2.setPrice(p2.getPrice() + 1000);

    s1.update(p1);
    s1.getTransaction().commit();
    s1.close();

    // 为了保证第二个事务稍后执行, 否则容易竞争锁
    Thread.sleep(3000);
    s2.update(p2);
    s2.getTransaction().commit();
    s2.close();

    sessionFactory.close();
  }
}
