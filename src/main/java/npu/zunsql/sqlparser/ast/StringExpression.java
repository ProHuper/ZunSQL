package npu.zunsql.sqlparser.ast;

public final class StringExpression extends FormatObject implements Expression {
  public final String string;

  public StringExpression(String string) {
    this.string = string;
  }
}
