package org.hibernate.cfg;

import org.hibernate.jdbc.util.FormatStyle;
import org.hibernate.jdbc.util.SQLStatementLogger;

/**
 * Created by backend.
 */

public class MySQLStatementLogger extends SQLStatementLogger {

  public void logStatement(String statement, FormatStyle style) {
    // 取消控制台打印sql
    setLogToStdout(false);
    super.logStatement(statement, style);
    // 方法三：自定义SQLStatementLogger打印sql
    System.out.println("自定义SQLStatementLogger: " + statement);
  }
}
