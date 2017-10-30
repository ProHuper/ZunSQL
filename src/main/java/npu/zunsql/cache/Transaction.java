package npu.zunsql.cache;

import java.util.concurrent.locks.ReadWriteLock;

public class Transaction
{
    protected static int transCount = 0;
    protected int transID;

    //标记是读事务还是写事务，WR是true为写，WR是false为读
    private boolean WR;

    protected ReadWriteLock lock;

    public Transaction(String s, ReadWriteLock lock)
    {
        if(s == "r")
            this.WR = false;
        else
            this.WR = true;
        this.lock = lock;
        this.transID = transCount++;
    }

    //
    public void begin()
    {
        if(this.WR)
        {
            this.lock.writeLock().lock();
        }
        else {
            this.lock.readLock().lock();
        }
    }
    public void commit()
    {
        if(this.WR)
        {
            this.lock.writeLock().unlock();
        }
        else
        {
            this.lock.readLock().unlock();
        }
    }
    public void rollback()
    {
        if(this.WR)
        {
            this.lock.writeLock().unlock();
        }
        else
        {
            this.lock.readLock().unlock();
        }
    }
}
