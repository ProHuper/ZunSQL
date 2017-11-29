package npu.zunsql.tree;

import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Table implements TableReader ,Serializable
{
    //表名
    protected String tableName;

    //主键
    protected Column keyColumn;

    //各列
    protected List<Column> columns;

    //锁类型
    protected LockType lock;

    //根节点
    protected int rootNodePage;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;

    private Page pageOne;

    // 写本页
    private boolean writeMyPage(Transaction myTran)
    {
        return cacheManager.writePage(myTran.tranNum, pageOne);
    }

    // 维护page信息
    private void writePageOne(Transaction thisTran) throws IOException {
        byte [] bytes=new byte[Page.PAGE_SIZE] ;
        ByteArrayOutputStream byt=new ByteArrayOutputStream();

        ObjectOutputStream obj=new ObjectOutputStream(byt);
        obj.writeObject(tableName);
        obj.writeObject(keyColumn);
        obj.writeObject(columns);
        obj.writeObject(lock);
        obj.writeObject(rootNodePage);
        bytes=byt.toByteArray();
        pageOne.getPageBuffer().put(bytes);

        writeMyPage(thisTran);
    }

    private boolean readPageOne() throws IOException, ClassNotFoundException {
        ByteBuffer thisBufer = pageOne.getPageBuffer();
        byte [] bytes=new byte[Page.PAGE_SIZE] ;
        thisBufer.get(bytes,0,thisBufer.remaining());

        ByteArrayInputStream byteTable=new ByteArrayInputStream(bytes);
        ObjectInputStream objTable=new ObjectInputStream(byteTable);

        this.tableName=(String)objTable.readObject();
        this.keyColumn=(Column) objTable.readObject();
        this.columns=(List<Column>)objTable.readObject();
        this.lock=(LockType)objTable.readObject();
        this.rootNodePage=(int)objTable.readObject();
        return true;
    }

    // 已有page，只需要加载其中的信息。
    // 新建table的工作在database中已经完成，因此，可能加载出只有表头的空表。
    protected Table(int pageID, CacheMgr cacheManager, Transaction thisTran) throws IOException, ClassNotFoundException
    {
        super();
        this.cacheManager = cacheManager;
        pageOne = this.cacheManager.readPage(thisTran.tranNum,pageID);

        readPageOne();
    }

    //获取表首页
    protected Integer getTablePageID()
    {
        return pageOne.getPageID();
    }

    //获取主键
    protected Column getKeyColumn()
    {
        return keyColumn;
    }

    //获取根节点
    protected Node getRootNode(Transaction thisTran) throws IOException, ClassNotFoundException {
        return new Node(rootNodePage, cacheManager, thisTran);
    }

    //给定列名，获取该列
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

    //创建游标
    public Cursor createCursor(Transaction thistran)
    {
        return new TableCursor(this,thistran);  //NULL
    }

    //获取列名列表
    public List<String> getColumnsName()
    {
        List<String> sList = new ArrayList<String>();
        for(int i = 0; i < columns.size(); i++)
        {
            sList.add(columns.get(i).getName());
        }
        return sList;
    }

    //获取列类型列表
    public List<BasicType> getColumnsType()
    {
        List<BasicType> sList = new ArrayList<BasicType>();
        for(int i = 0; i < columns.size(); i++)
        {
            sList.add(columns.get(i).getType());
        }
        return sList;
    }

    // 获取表名
    public String getTableName()
    {
        return tableName;   //NULL
    }

    //判断是否上锁
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

    //上写锁
    public boolean lock(Transaction thistran) throws IOException {
        lock = LockType.Locked;   //NULL

        writePageOne(thistran);

        while(!writeMyPage(thistran));
        return true;
    }

    //解写锁，上读锁
    public boolean unLock(Transaction thistran) throws IOException {
        lock = LockType.Shared;   //NULL

        writePageOne(thistran);
        while(!writeMyPage(thistran));
        return true;
    }
}

