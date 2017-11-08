package npu.zunsql.ve;

public enum OpCode {
    Transaction,
    Commit,
    Rollback,

    Integer,
    String,
    Float,

    CreateDB,
    DropDB,

    CreateTable,
    AddCol,
    BeginPK,
    AddPK,
    EndPK,

    DropTable,

    Insert,
    BeginItem,
    AddItemCol,
    EndItem,

    BeginFilter,
    Filter,
    EndFilter,

    Delete,

    Select,
    BeginColSelect,
    AddColSelect,
    EndColSelect,

    Update,
    Set,

    BeginExpression,
    EndExpression,

    GT,
    GE,
    LT,
    LE,
    EQ,
    NE,
    Mul,
    Div,
    Neg,
    Sub,
    And,
    Or,
    Not,

    Operator,
    Operand,

    Execute,
}
