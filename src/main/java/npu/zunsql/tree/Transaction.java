package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
public class Transaction
{
    public final static int TT_READ = 1;
    public final static int TT_WRITE = 2;

    private Integer tranNum;

    public Transaction(Integer number)
    {
        tranNum = number;
    }


    public boolean Commit()
    {
        return true;
    }

    public boolean Rollback()
    {
        return true;
    }

}
