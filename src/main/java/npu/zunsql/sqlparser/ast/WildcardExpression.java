package npu.zunsql.sqlparser.ast;

public final class WildcardExpression extends FormatObject implements Expression {
  public final QualifiedName owner;

  public WildcardExpression(QualifiedName owner) {
    this.owner = owner;
  }
}
