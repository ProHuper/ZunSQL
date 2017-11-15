package npu.zunsql.tree;

import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Table
{
    public final static int LO_LOCKED = 1;
    public final static int LO_SHARED = 2;
    private Integer lock;
    private String tableName;
    private Column keyColumn;
    private List<Column> columns;
    private int rootNodePage;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;
    Page pageOne;

    // 已经新建好了一个page，只需要填写相关table信息
    public Table(String name,Column key,List<Column> coList,int pageID,CacheMgr cacheMagr,Transaction thistran)
    {
        tableName = name;
        keyColumn = key;
        columns = coList;
        lock = LO_SHARED;

        // TODO: 初始化rootNodePage
        cacheManager = cacheMagr;

        pageOne = cacheManager.readPage(thistran.tranNum,pageID);
        ByteBuffer thisBufer = pageOne.getPageBuffer();

        // TODO:写入buffer。


        writeMyPage(thistran);

    }

    // 已有page，只需要加载其中的信息。
    public Table(int pageID,CacheMgr cacheMagr,Transaction thistran)
    {
        pageOne = cacheManager.readPage(thistran.tranNum,pageID);
        ByteBuffer thisBufer = pageOne.getPageBuffer();

        // TODO:读取buffer。

    }

    // 需要自己新建Page，并填写相关table信息
    public Table(String name,Column key,List<Column> coList,CacheMgr cacheMagr,Transaction thistran)
    {
        tableName = name;
        keyColumn = key;
        columns = coList;
        lock = LO_SHARED;

        // TODO: 初始化rootNodePage
        cacheManager = cacheMagr;

        // TODO: 此处存在问题，1024没有意义。
        ByteBuffer tempBuffer = ByteBuffer.allocate(1024);

        // TODO：QUE:不需要事务编号吗？
        pageOne = new Page(tempBuffer);


        // TODO:写入buffer。


        writeMyPage(thistran);
    }


    private boolean writeMyPage(Transaction myTran)
    {
        // 写本页
        if(cacheManager.writePage(myTran.tranNum,pageOne))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public boolean drop(Transaction thistran)
    {
        // TODO: 递归处理page。

        return true;
    }

    public boolean clear(Transaction thistran)
    {
        // TODO：仅保留本Page，处理所有数据。
        return true;
    }


    public String getTableName()
    {
        return tableName;   //NULL
    }

    public boolean isLocked()
    {
        if (lock == LO_LOCKED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected Node getRootNode(Transaction thistran)
    {
        Page nodePage = cacheManager.readPage(thistran.tranNum,rootNodePage);
        return new Node(nodePage);
    }

    public boolean lock(Transaction thistran)
    {
        lock = LO_LOCKED;   //NULL

        // TODO:更新pageOne。

        writeMyPage(thistran);
        return true;
    }

    public boolean unLock(Transaction thistran)
    {
        lock = LO_SHARED;   //NULL

        // TODO:更新pageOne。

        writeMyPage(thistran);
        return true;
    }

    public Cursor createCursor()
    {
        return new Cursor(this);  //NULL
    }
    public Column getKeyColumn()
    {
        return keyColumn;
    }
    public List<Column> getColumns()
    {
        return columns;
    }
    public Column getColumn(String columnName)
    {
        for(int i = 0; i < columns.size(); i++)
        {
            if(columns.get(i).getColumnName().equals(columnName))
            {
                return columns.get(i);
            }
        }
        return null;
    }
}
