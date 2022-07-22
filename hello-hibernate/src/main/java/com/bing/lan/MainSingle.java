package com.bing.lan;

import com.bing.lan.pojo.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainSingle {

  static Logger logger = LoggerFactory.getLogger(MainSingle.class);

  public static void main(String[] args) throws InterruptedException {
    SessionFactory sf = new Configuration().configure().buildSessionFactory();
    Session s1 = sf.openSession();

    s1.beginTransaction();

    Product p2 = new Product();
    p2.setId(2);
    p2.setVersion(22);
    p2.setPrice(22);
    p2.setName("11112222");

    System.out.println("=========p2: " + p2);

    s1.update(p2);
    s1.getTransaction().commit();
    s1.close();

    sf.close();
  }
}