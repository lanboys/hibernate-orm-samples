package org.hibernate.cfg;

import org.hibernate.EmptyInterceptor;

import java.util.Iterator;
import java.util.Properties;

/**
 * Created by backend.
 */

public class MyConfiguration extends Configuration {

  public MyConfiguration() {
    super(new SettingsFactory() {
      @Override
      public Settings buildSettings(Properties props) {
        Settings settings = super.buildSettings(props);
        // 方法三：自定义SQLStatementLogger打印sql
        settings.setSqlStatementLogger(new MySQLStatementLogger());
        return settings;
      }
    });
    // 方法二：拦截器，也可以用来打印sql
    setInterceptor(new EmptyInterceptor() {

      public void postFlush(Iterator entities) {
        System.out.println("postFlush(): ");
      }

      public void preFlush(Iterator entities) {
        System.out.println("preFlush(): ");
      }

      public String onPrepareStatement(String sql) {
        System.out.println("onPrepareStatement(): " + sql);
        return sql;
      }
    });
  }
}
