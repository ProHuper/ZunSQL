package npu.zunsql.ve;

public enum OpCode{

    Transaction,
    Commit,
    Rollback,

    Integer,
    String,
    Float,

    CreateDB,
    DropDB,

    CreateTable,
    DropTable,

    Insert,
    Delete,
    Select,
    Update,
    Set,

    And,
    Not,
    Or,

    AddCol,

    BeginPK,
    AddPK,
    EndPK,

    BeginItem,
    AddItemCol,
    EndItem,

    BeginColSelect,
    AddColSelect,
    EndColSelect,

    BeginFilter,
    Filter,
    EndFilter,

    Execute
}