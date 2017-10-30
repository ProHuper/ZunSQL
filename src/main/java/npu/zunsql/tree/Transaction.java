package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
public class Transaction
{

    private Integer tranNum;

    public Transaction(Integer tranNum)
    {

    }

//    public boolean BeginTrans(Integer TransType)
//    {
//        return true;
//    }

    public boolean Commit()
    {
        return true;
    }

    public boolean Rollback()
    {
        return true;
    }

//    public Cursor CreateCursor(Table Mytree)
//    {
//        Cursor cursor = new Cursor();
//        return cursor;
//    }
}
