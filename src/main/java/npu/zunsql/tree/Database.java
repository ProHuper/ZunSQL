package npu.zunsql.tree;

import npu.zunsql.cache.Page;
import  npu.zunsql.cache.CacheMgr;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ed on 2017/10/29.
 */
public class Database
{
    //表示dataBase的名字
    private String dBName;

    private Table master;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;

    // 首先新建一个Page，然后进行相关Page写操作。
    protected Database(String name) throws IOException, ClassNotFoundException {
        dBName = name;
        cacheManager = new CacheMgr(dBName);

        // 判断数据库是否为新建数据库，即pageid为0的一页是否被填充
        boolean dbisNew = false;
        dbisNew=cacheManager.isNew();
        if(!dbisNew)
        {
            Transaction initTran = beginReadTrans();

            //从page[0]解析master。
            master = new Table(0, cacheManager, initTran);
            initTran.Commit();
        }
        else
        {
            //建立masterTable
            Transaction initTran = beginWriteTrans();
            addMaster(initTran);
            initTran.Commit();
        }
    }


    private boolean addMaster(Transaction initTran) throws IOException
    {
        // 添加master table
        Column keyColumn = new Column(BasicType.String,"tableName",0);
        Column valueColumn = new Column(BasicType.Integer,"pageNumber",1);
        List<String> sList = new ArrayList<String>();
        List<BasicType> tList = new ArrayList<BasicType>();
        sList.add("tableName");
        sList.add("pageNumber");
        tList.add(BasicType.String);
        tList.add(BasicType.Integer);
        master = createTable("master","tableName",sList,tList,initTran);
        return true;
    }

    public boolean dropTable(String tableName,Transaction thisTran)
    {
        // TODO：递归释放此Page

        return true;
    }

    public boolean dropTable(Table table,Transaction thisTran)
    {
        // TODO：递归释放此Page

        return true;
    }

    //开始一个读事务操作
    public Transaction beginReadTrans()
    {
        return new ReadTran(cacheManager.beginTransation("r"),cacheManager);
    }

    //开始一个写事务
    public Transaction beginWriteTrans()
    {
        return new WriteTran(cacheManager.beginTransation("w"),cacheManager);
    }

    //根据传来的表名，主键以及其他的列名来新建一个表
    public Table createTable(String tableName, String keyName, List<String> columnNameList,List<BasicType> tList, Transaction thisTran) throws IOException {
        ByteBuffer tempBuffer = ByteBuffer.allocate(Page.PAGE_SIZE);
        // 将表头信息和首节点信息存入ByteBuffer中 新建的表锁应该为什么锁
        LockType lock=LockType.Shared;

        Page tablePage = new Page(tempBuffer);
        cacheManager.writePage(thisTran.tranNum,tablePage);
        Integer pageID = tablePage.getPageID();

        List<String> masterRow_s = new ArrayList<String>();
        masterRow_s.add(tableName);
        masterRow_s.add(pageID.toString());

        Cursor masterCursor = master.createCursor(thisTran);
        masterCursor.insert(thisTran,masterRow_s);


        byte [] bytes=new byte[Page.PAGE_SIZE] ;
        ByteArrayOutputStream byt=new ByteArrayOutputStream();

        ObjectOutputStream obj=new ObjectOutputStream(byt);
        obj.writeObject(tableName);
        obj.writeObject(keyName);
        obj.writeObject(columnNameList);
        obj.writeObject(lock);
        obj.writeObject(-1);
        obj.writeObject(cacheManager);
        obj.writeObject(tablePage);
        bytes=byt.toByteArray();
        tablePage.getPageBuffer().put(bytes);

        return new Table(pageID,cacheManager,thisTran);   //NULL
    }

    //根据传来的表名，主键以及其他的列名来新建一个表放入tableList中
    public View createView(List<String> sList, List<BasicType> tList, List<List<String>> rowStringList, Transaction thisTran)
    {
        return new View(sList,tList,rowStringList);
    }

    //根据传来的表名返回Table表对象
    public Table getTable(String tableName, Transaction thisTran)
    {
        Cursor masterCursor = master.createCursor(thisTran);
        masterCursor.moveToUnpacked(thisTran,tableName);
        return new Table(masterCursor.getCell_i("pageNumber"),cacheManager,thisTran);
    }

    //给整个数据库中的表全部加锁
    public boolean lock(Transaction thisTran)
    {
        if(master.isLocked())
        {
            return false;
        }
        else
        {
            master.lock(thisTran);
            Cursor masterCursor = master.createCursor(thisTran);
            do
            {
                Table temp = new Table(masterCursor.getCell_i("pageNumber"),cacheManager,thisTran);
                temp.lock(thisTran);
            }while(masterCursor.moveToNext(thisTran));
            return true;
        }
    }

    //给数据库中全部的表解锁
    public boolean unLock(Transaction thisTran)
    {
        if(master.isLocked())
        {
            master.unLock(thisTran);
            Cursor masterCursor = master.createCursor(thisTran);
            do
            {
                Table temp = new Table(masterCursor.getCell_i("pageNumber"),cacheManager,thisTran);
                temp.unLock(thisTran);
            }while(masterCursor.moveToNext(thisTran));
            return true;
        }
        else
        {
            return true;
        }
    }
}
