package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
abstract class Transaction
{
    // transaction中包含一个事务号。
    protected Integer tranNum;

    // transaction初始化一个事务号
    protected Transaction(Integer number)
    {
        tranNum = number;
    }

    // 定义了两个抽象操作，提交和回滚。
    abstract boolean Commit();

    abstract boolean RollBack();
}

class WriteTran extends Transaction
{
    protected WriteTran(int num)
    {
        super(num);
    }

    public boolean Commit()
    {
        return false;
    }

    public boolean RollBack()
    {
        return false;
    }
}

class ReadTran extends Transaction
{
    protected ReadTran(int num)
    {
        super(num);
    }

    public boolean Commit()
    {
        return false;
    }

    public boolean RollBack()
    {
        return false;
    }
}