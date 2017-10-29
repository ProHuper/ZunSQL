package npu.zunsql.sqlparser.ast;

public final class TableRelation extends FormatObject implements Relation {
  public final QualifiedName tableName;

  public TableRelation(QualifiedName tableName) {
    this.tableName = tableName;
  }
}
