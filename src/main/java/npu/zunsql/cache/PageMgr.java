package npu.zunsql.cache;

import com.sun.xml.internal.fastinfoset.tools.TransformInputOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PageMgr
{
    protected static final String PREFIX_UNUSED_PAGE = "unused_page_";

    protected static final String PREFIX_DATA = "data_";

    protected String dbName = null;

    //未被使用的页的字典：<页号，页>
    protected Map<Integer, Page> unusedMap = null;
    //未被使用的页队列
    protected Queue<Page> unusedQueue = null;


    //已被使用的页队列
    protected List<Page> usedList = null;
    //已被使用的页字典：<页号，页>
    protected ConcurrentMap<Integer, Page> pageMap = null;
    protected Transaction trans;

    protected Map<Integer, Transaction> transMgr = null;
    private ReadWriteLock lock;



    public PageMgr(String dbName)
    {
        this.dbName      = dbName;
        //this.fileMgr     = fileMgr;

        this.unusedQueue = new LinkedBlockingDeque<Page>();
        this.usedList    = new ArrayList<Page>();
        this.pageMap     = new ConcurrentHashMap<Integer, Page>();
        this.unusedMap   = new HashMap<Integer, Page>();
        this.transMgr    = new HashMap<Integer, Transaction>();
        this.lock = new ReentrantReadWriteLock();
    }

    //
    public int beginTransation(String s)
    {
        Transaction trans = new Transaction(s, lock);
        trans.begin();
        this.transMgr.put(trans.transID, trans);
        return trans.transID;
    }

    public boolean commitTransation(int transID)
    {
        Transaction trans = transMgr.get(transID);
        trans.commit();
        this.transMgr.remove(transID);
        return true;
    }

    public boolean rollbackTransation(int transID)
    {
        Transaction trans = transMgr.get(transID);
        trans.rollback();
        this.transMgr.remove(transID);
        return true;
    }

    /**根据pageID，从磁盘中读取对应页
     //如果当前页已经被缓存入pageMap，直接获取
     //否则，从磁盘总读入
     *
     */
    public Page readPage(int pageID)
    {
        Page tempPage = null;
        tempPage = this.pageMap.get(pageID);


        if(tempPage == null)
        {
            FileChannel fc = null;
            try
            {
                String homedir = System.getProperty("user.home");
                File file = new File(homedir, PREFIX_DATA+this.dbName);
                RandomAccessFile f = new RandomAccessFile(file, "rw");
                fc = f.getChannel();

                if(fc != null && fc.isOpen())
                {
                    int limit = (int)(fc.size()/Page.PAGE_SIZE);
                    if(pageID <= limit)
                    {
                        ByteBuffer tempBuffer = ByteBuffer.allocate(Page.PAGE_SIZE);
                        tempPage = new Page(tempBuffer);

                        int read = fc.read(tempBuffer, (tempPage.pageID*Page.PAGE_SIZE));
                        if(Page.PAGE_SIZE == read)
                        {
                            this.pageMap.put(tempPage.pageID, tempPage);
                        }
                    }
                }
            }
            catch (FileNotFoundException e)
            {
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
        }
        return tempPage;
    }



    /**将缓存区所有已被写入的页写回至文件
     *
     */
    public void flushData()
    {
        if(usedList.size() > 0)
        {
            for(int i = 0; i < usedList.size(); ++i)
            {
                Page page = usedList.get(i);
                if(page != null)
                {
                    FileChannel fc = null;
                    try
                    {
                        String homedir = System.getProperty("user.home");
                        File file = new File(homedir, PREFIX_DATA+this.dbName);
                        RandomAccessFile f = new RandomAccessFile(file, "rw");
                        fc = f.getChannel();

                        int limit = (int)(fc.size()/Page.PAGE_SIZE);
                        if(page.pageID < limit)
                        {
                            page.pageBuffer.flip();
                            fc.write(page.pageBuffer, page.pageID*Page.PAGE_SIZE)
                        }

                    }
                    catch (FileNotFoundException e)
                    {
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
                }

                this.pageMap.remove(page.pageID);
            }
            usedList.clear();
        }
    }


}
