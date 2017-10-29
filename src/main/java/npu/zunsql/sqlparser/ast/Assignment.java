package npu.zunsql.sqlparser.ast;

public final class Assignment extends FormatObject implements Expression {
    public final QualifiedName name;
    public final Expression value;

    public Assignment(QualifiedName name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
