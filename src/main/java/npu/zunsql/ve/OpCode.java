package npu.zunsql.ve;

/**
 * Created by Huper on 2017/10/30.
 */
public enum OpCode{

    Transaction,
    Commit,
    Rollback,

    Next,
    Goto,
    Jump,
    Achieve,

    CreateDB,
    DropDB,
    OpenDB,
    CloseDB,

    CreateTable,
    DropTable,
    OpenTable,
    CloseTable,

    Insert,
    Delete,
    Select,
    Update,
    Set,

    Add,
    Sub,
    Mul,
    Div,

    And,
    Not,
    Or,

    GT,
    LT,
    LE,
    GE,
    EQ,
    NE,

    In,
    Is,
    Exists,

    AddConst,
    SubConst,
    MulConst,
    DivConst,
    AndConst,
    OrConst,

    BeginAssemble,
    AddItem,
    EndAssemble

}