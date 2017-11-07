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
    And,
    Or,
    Not,
    Filter,
    EndFilter,

    Delete,

    Select,
    BeginColSelect,
    AddColSelect,
    EndColSelect,

    Update,
    Set,

    Execute,
}
