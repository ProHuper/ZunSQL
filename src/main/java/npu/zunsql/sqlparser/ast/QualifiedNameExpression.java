package npu.zunsql.sqlparser.ast;

public final class QualifiedNameExpression extends FormatObject implements Expression {
  public final QualifiedName qname;

  public QualifiedNameExpression(QualifiedName qname) {
    this.qname = qname;
  }
  
  public static QualifiedNameExpression of(String... names) {
    return new QualifiedNameExpression(QualifiedName.of(names));
  }
}
