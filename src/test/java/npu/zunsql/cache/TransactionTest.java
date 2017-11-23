  package npu.zunsql.cache;
import junit.framework.TestCase;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionTest extends TestCase {
    private ReadWriteLock readLock = new ReentrantReadWriteLock();
    private ReadWriteLock writeLock = new ReentrantReadWriteLock();
    private Transaction readTrans = new Transaction("r",readLock);
    private Transaction writeTrans = new Transaction("w",writeLock);
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testBegin() throws Exception {
        //测试读事务
        readTrans.begin();
        File journal = new File(Integer.toString(readTrans.transID)+readTrans.SUFFIX_JOURNAL);
        assertEquals(false, journal.exists());
        assertEquals(false, readTrans.lock.writeLock().tryLock());
        //测试写事务
        writeTrans.begin();
        journal = new File(Integer.toString(writeTrans.transID)+writeTrans.SUFFIX_JOURNAL);
        assertEquals(1, writeTrans.transID);
        assertEquals(true, journal.exists());
        assertEquals(false, writeTrans.lock.writeLock().tryLock());
    }

    public void testCommit() throws Exception {

        //测试读事务
        readTrans.commit();
        journal = new File(Integer.toString(readTrans.transID)+readTrans.SUFFIX_JOURNAL);
        assertEquals(false, journal.exists());
        assertEquals(true, readTrans.lock.writelock().tryLock());
        //测试写事务
        writeTrans.commit();
        journal = new File(Integer.toString(writeTrans.transID)+writeTrans.SUFFIX_JOURNAL);
        assertEquals(false, journal.exists());
        assertEquals(true, writeTrans.lock.writelock().tryLock());
    }

    public void testRollback() throws Exception {
        //测试读事务
        readTrans.rollback();
        journal = new File(Integer.toString(readTrans.transID)+readTrans.SUFFIX_JOURNAL);
        assertEquals(false, journal.exists());
        assertEquals(true, readTrans.lock.writelock().tryLock());
        //测试写事务
        writeTrans.rollback();
        journal = new File(Integer.toString(writeTrans.transID)+writeTrans.SUFFIX_JOURNAL);
        assertEquals(false, journal.exists());
        assertEquals(true, writeTrans.lock.writelock().tryLock());
    }

}