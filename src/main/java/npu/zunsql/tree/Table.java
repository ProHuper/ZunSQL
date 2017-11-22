package npu.zunsql.tree;

import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Table implements TableReader
{
    protected String tableName;

    protected Column keyColumn;
    protected List<Column> columns;

    protected LockType lock;

    protected int rootNodePage;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;

    private Page pageOne;

    private boolean writeMyPage(Transaction myTran)
    {
        // 写本页
        return cacheManager.writePage(myTran.tranNum, pageOne);
    }

    // 已经新建好了一个page，只需要填写相关table信息
    protected Table(String name, Column key, List<Column> coList, int pageID, CacheMgr cacheMagr, Transaction thistran)
    {
        super();
        tableName = name;
        keyColumn = key;
        columns = coList;
        lock = LockType.Shared;

        // TODO: 初始化rootNodePage
        cacheManager = cacheMagr;

        pageOne = cacheManager.readPage(thistran.tranNum,pageID);

        ByteBuffer thisBufer = pageOne.getPageBuffer();
        // TODO:写入buffer。


        while(!writeMyPage(thistran));

    }

    // 已有page，只需要加载其中的信息。
    protected Table(int pageID, CacheMgr cacheMagr, Transaction thistran)
    {
        super();
        cacheManager = cacheMagr;
        pageOne = cacheManager.readPage(thistran.tranNum,pageID);

        ByteBuffer thisBufer = pageOne.getPageBuffer();
        // TODO:读取buffer。

    }

    // 需要自己新建Page，并填写相关table信息
    protected Table(String name, Column key, List<Column> coList, CacheMgr cacheMagr, Transaction thistran)
    {
        super();
        tableName = name;
        keyColumn = key;
        columns = coList;
        lock = LockType.Shared;

        // TODO: 初始化rootNodePage
        cacheManager = cacheMagr;

        // TODO: 此处存在问题，1024没有意义。
        ByteBuffer tempBuffer = ByteBuffer.allocate(1024);

        // TODO：QUE:不需要事务编号吗？
        pageOne = new Page(tempBuffer);


        // TODO:写入buffer。


        while(!writeMyPage(thistran)) ;
    }

    protected Integer getTablePageID()
    {
        return pageOne.getPageID();
    }

    protected Column getKeyColumn()
    {
        return keyColumn;
    }

    protected Node getRootNode(Transaction thistran)
    {
        Page nodePage = cacheManager.readPage(thistran.tranNum,rootNodePage);
        return new Node(nodePage);
    }

    protected Column getColumn(String columnName)
    {
        for(int i = 0; i < columns.size(); i++)
        {
            if(columns.get(i).getName().equals(columnName))
            {
                return columns.get(i);
            }
        }
        return null;
    }

    public Cursor createCursor(Transaction thistran)
    {
        return new TableCursor(this,thistran);  //NULL
    }

    public List<String> getColumns()
    {
        List<String> sList = new ArrayList<String>();
        for(int i = 0; i < columns.size(); i++)
        {
            sList.add(columns.get(i).getName());
        }
        return sList;
    }

    public String getTableName()
    {
        return tableName;   //NULL
    }

    public boolean isLocked()
    {
        if (lock == LockType.Locked)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean lock(Transaction thistran)
    {
        lock = LockType.Locked;   //NULL

        // TODO:更新pageOne。

        while(!writeMyPage(thistran));
        return true;
    }

    public boolean unLock(Transaction thistran)
    {
        lock = LockType.Shared;   //NULL

        // TODO:更新pageOne。

        while(!writeMyPage(thistran));
        return true;
    }
}

