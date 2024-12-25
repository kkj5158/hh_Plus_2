package org.example.hhplus.registrationsys.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStatementInspector implements StatementInspector {

  private static final Pattern SQL_FORMAT_PATTERN = Pattern.compile(
      "(SELECT|FROM|WHERE|GROUP BY|ORDER BY|HAVING|LEFT JOIN|INNER JOIN|OUTER JOIN|RIGHT JOIN|ON|AND|OR|LIMIT|OFFSET)",
      Pattern.CASE_INSENSITIVE);

  @Override
  public String inspect(String sql) {
    // SQL 포맷팅
    String formattedSql = formatSql(sql);
    System.out.println("Executing SQL:\n" + formattedSql); // 포맷팅된 SQL 출력
    return sql;
  }

  /**
   * SQL 쿼리를 사람이 읽기 쉽도록 포맷팅합니다.
   *
   * @param sql 원본 SQL 쿼리
   * @return 포맷팅된 SQL 쿼리
   */
  private String formatSql(String sql) {
    // 줄바꿈과 들여쓰기를 추가
    String formattedSql = SQL_FORMAT_PATTERN.matcher(sql)
                                            .replaceAll("\n$1");

    // 추가적인 공백 정리
    formattedSql = formattedSql.replaceAll("\\s{2,}", " ").trim();

    // 각 줄을 들여쓰기
    StringBuilder formattedWithIndentation = new StringBuilder();
    String[] lines = formattedSql.split("\n");
    for (String line : lines) {
      formattedWithIndentation.append("    ").append(line.trim()).append("\n");
    }
    return formattedWithIndentation.toString();
  }
}
