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

    // 已有page，只需要加载其中的信息。
    // 新建table的工作在database中已经完成，因此，可能加载出只有表头的空表。
    protected Table(int pageID, CacheMgr cacheManager, Transaction thisTran)
    {
        super();
        this.cacheManager = cacheManager;
        pageOne = this.cacheManager.readPage(thisTran.tranNum,pageID);

        ByteBuffer thisBufer = pageOne.getPageBuffer();
        // TODO:读取Buffer。

    }

    protected Integer getTablePageID()
    {
        return pageOne.getPageID();
    }

    protected Column getKeyColumn()
    {
        return keyColumn;
    }

    protected Node getRootNode(Transaction thisTran)
    {
        return new Node(rootNodePage, cacheManager, thisTran);
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

    public List<String> getColumnsName()
    {
        List<String> sList = new ArrayList<String>();
        for(int i = 0; i < columns.size(); i++)
        {
            sList.add(columns.get(i).getName());
        }
        return sList;
    }

    public List<BasicType> getColumnsType()
    {
        List<BasicType> sList = new ArrayList<BasicType>();
        for(int i = 0; i < columns.size(); i++)
        {
            sList.add(columns.get(i).getType());
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

