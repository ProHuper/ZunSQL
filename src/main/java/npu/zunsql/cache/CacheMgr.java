package npu.zunsql.cache;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheMgr
{
    protected static final int CacheCapacity = 10;
    protected String dbName = null;


    //缓存页链表，按照LRU策略组织顺序
    protected List<Page> cacheList = null;
    //缓存页字典
    protected ConcurrentMap<Integer, Page> cachePageMap = null;
    //事务管理器
    protected Map<Integer, Transaction> transMgr = null;
    //事务写操作对应的页
    protected Map<Integer, List<Page>> transOnPage = null;
    private ReadWriteLock lock;



    public CacheMgr(String dbName)
    {
        this.dbName       = dbName;

        this.cacheList    = new ArrayList<Page>();
        this.cachePageMap = new ConcurrentHashMap<Integer, Page>();
        this.transMgr     = new HashMap<Integer, Transaction>();

        this.transOnPage  = new HashMap<Integer, List<Page>>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**开始一个新的事务，返回新事务的transID
     *
     * 生成一个新的事务对象，获得对应的锁
     * 记录到transMgr
     */
    public int beginTransation(String s)
    {
        Transaction trans = new Transaction(s, lock);
        trans.begin();
        this.transMgr.put(trans.transID, trans);
        return trans.transID;
    }

    /**提交transID对应的事务，将更新的副本页写回到cache中
     *
     * 如果当前页原始数据已经被缓存入cachePageMap，则命中页，更新之
     * 否则，页缺失，将副本页写入缓存中
     * 此时，若缓存已满，按照LRU策略将cacheList的第一页写入文件中
     */
    public boolean commitTransation(int transID) throws IOException {
        Transaction trans = transMgr.get(transID);
        List<Page> writePageList= transOnPage.get(transID);
        File journal_file = new File(Integer.toString(transID)+"-journal");
        File db_file = new File(this.dbName);

        for( int i = 0 ; i < writePageList.size() ; i++)
        {
            Page copyPage = writePageList.get(i);
            Page tempPage = this.cachePageMap.get(copyPage.pageID);
            //cache未命中
            if(tempPage == null)
            {
                //cache已满，按照LRU策略替换
                if (this.cachePageMap.size() >= CacheMgr.CacheCapacity) {
                    //按照LRU在cache的list和map中删除某一页的记录
                    tempPage = this.cacheList.get(0);
                    this.cacheList.remove(0);
                    this.cachePageMap.remove(tempPage.pageID);

                    //修改删除页对象的数据后，新加入cache的list和map中
                    tempPage.pageID = copyPage.pageID;
                    tempPage.pageBuffer.put(copyPage.pageBuffer);
                    this.cacheList.add(tempPage);
                    this.cachePageMap.put(copyPage.pageID, tempPage);
                }
                //cache还有空间
                else
                {
                    //新建页对象，记录在cache的list和map中
                    tempPage = new Page(copyPage);
                    this.cacheList.add(tempPage);
                    this.cachePageMap.put(tempPage.pageID, tempPage);
                }
            }
            //cache命中，修改cache中该页的数据
            else
            {
                for( int j = 0 ; j < cacheList.size() ; j++)
                {
                    Page jPage = cacheList.get(i);
                    if (jPage.pageID == copyPage.pageID)
                    {
                        cacheList.remove(i);
                    }
                }
                tempPage.pageID = copyPage.pageID;
                tempPage.pageBuffer.put(copyPage.pageBuffer);
                this.cacheList.add(tempPage);
            }

            //写日志文件
            if(journal_file.exists()&&journal_file.isFile())
            {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(journal_file));
                out.writeObject(tempPage);
            }
            //写直达，将该页同时写至数据库文件中
            if(db_file.exists()&&db_file.isFile())
            {
                this.setPageToFile(tempPage, db_file);
            }
        }

        trans.commit();
        this.transMgr.remove(transID);
        return true;
    }

    /**transID对应的事务回滚
     *
     * 释放对应的锁，cache不做任何操作
     */
    public boolean rollbackTransation(int transID)
    {
        Transaction trans = transMgr.get(transID);
        trans.rollback();
        this.transMgr.remove(transID);
        return true;
    }

    /**事务transID读取pageID对应的页，返回页的副本
     * 
     * 如果当前页已经被缓存入cachePageMap，则命中页，直接从缓存区中返回副本
     * 否则，页缺失，从文件中读取对应页到缓存区后返回副本
     * 此时，若缓存空间已满，按照LRU策略将一页写回文件，腾出空间后从文件中读取页
     * 在这个过程中，按照LRU策略更新cacheList
     */
    public Page readPage(int transID, int pageID)
    {

        Page tempPage = null;
        tempPage = this.cachePageMap.get(pageID);

        //cache未命中
        if(tempPage == null)
        {
            //cache已满，按照LRU策略替换，无需写回文件，删除cache中list和map记录即可
            if(this.cachePageMap.size() >= CacheMgr.CacheCapacity)
            {
                tempPage = this.cacheList.get(0);
                this.cacheList.remove(0);
                this.cachePageMap.remove(tempPage.pageID);

                tempPage = getPageFromFile(pageID);
                this.cacheList.add(tempPage);
                this.cachePageMap.put(tempPage.pageID, tempPage);
            }
            else
            {
                tempPage = getPageFromFile(pageID);
                this.cacheList.add(tempPage);
                this.cachePageMap.put(tempPage.pageID, tempPage);
            }

        }
        //cache命中
        else
        {
            //按照LRU策略更新cacheList链表
            for( int j = 0 ; j < cacheList.size() ; j++)
            {
                Page jPage = cacheList.get(j);
                if (jPage.pageID == tempPage.pageID)
                {
                    cacheList.remove(jPage);
                }
            }
            this.cacheList.add(tempPage);
        }
        return tempPage;
    }

    /**事务transID写pageID对应的页，只做记录，并未提交到Cache
     *
     *根据事务的transID获得写队列，将副本的引用添加到写列表中
     */
    public boolean writePage(int transID, Page tempBuffer)
    {
        List<Page> writePageList= transOnPage.get(transID);
        writePageList.add(tempBuffer);

        return true;
    }

    /**将指定的某一页写回至内存
     *
     */
    public boolean setPageToFile(Page tempPage, File file)
    {
        FileChannel fc = null;
        try
        {
            if(!file.exists())
            {
                file.createNewFile();
            }
            RandomAccessFile fin = new RandomAccessFile(file, "rw");
            fc = fin.getChannel();

            //独占锁
            FileLock lock = fc.lock();

            tempPage.pageBuffer.flip();
            fc.write(tempPage.pageBuffer, tempPage.pageID*Page.PAGE_SIZE);
            lock.release();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(fc != null)
                {
                    fc.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**读取文件中的指定PageID页
     *
     */
    public Page getPageFromFile(int pageID)
    {
        Page tempPage = null;
        FileChannel fc = null;
        try
        {
            File file = new File(this.dbName);
            RandomAccessFile fin = new RandomAccessFile(file, "rw");
            fc = fin.getChannel();

            //共享锁
            FileLock lock = fc.lock(0, Long.MAX_VALUE, true);

            ByteBuffer tempBuffer = ByteBuffer.allocate(Page.PAGE_SIZE);
            fc.read(tempBuffer, pageID*Page.PAGE_SIZE);
            tempPage = new Page(pageID, tempBuffer);

            lock.release();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(fc != null)
                {
                    fc.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return tempPage;
    }
}
