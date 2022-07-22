package com.bing.lan;

import com.bing.lan.pojo.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws InterruptedException {

    SessionFactory sf = new Configuration().configure().buildSessionFactory();
    Session s1 = sf.openSession();
    Session s2 = sf.openSession();

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

    sf.close();
  }
}