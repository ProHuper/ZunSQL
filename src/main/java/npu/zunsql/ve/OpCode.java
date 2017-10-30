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
    Achieve,

    CreateDB,
    DropDB,

    CreateTable,
    DropTable,

    Insert,
    Delete,
    Select,
    Update,
    Set,

    Add,
    Sub,
    Mul,
    DIv,
    And,
    Not,
    Or,
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