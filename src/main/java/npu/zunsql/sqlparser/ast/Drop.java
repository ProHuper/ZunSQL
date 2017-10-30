package npu.zunsql.sqlparser.ast;

public final class Drop extends FormatObject implements Relation {
    public final TableRelation table;

    public Drop(
            TableRelation table) {
        this.table = table;
    }
}