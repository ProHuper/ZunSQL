package npu.zunsql.sqlparser.ast;

public final class NumberExpression extends FormatObject implements Expression {
  public final String number;

  public NumberExpression(String number) {
    this.number = number;
  }
}
