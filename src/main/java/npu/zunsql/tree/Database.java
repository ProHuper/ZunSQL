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

    Table master;

    // Mgr页放在第一页
    // 内容包括：database的对应页
    private Page pageOne;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;

    private Table getMaster()
    {
        return null;
    }

    // 首先新建一个Page，然后进行相关Page写操作。
    protected Database(String name) throws IOException
    {
        dBName = name;
        cacheManager = new CacheMgr(dBName);
        if()
        {

        }
        Transaction initTran = beginReadTrans();
        pageOne = cacheManager.readPage(initTran.tranNum,0);
        initTran.Commit();
        master = getMaster();
        if (newMyPage())
        {
            if(addMasterTable())
            {
                ByteBuffer thisBufer = pageOne.getPageBuffer();
                // TODO: 修改thisBufer.


                while(!writeMyPage());
            }
        }
    }


    private boolean addMasterTable() throws IOException {
        // 添加master table
        Column keyColumn = new Column(BasicType.String,"tableName",0);
        Column valueColumn = new Column(BasicType.Integer,"pageNumber",1);
        List<Column> columnList = new ArrayList<>();
        columnList.add(keyColumn);
        columnList.add(valueColumn);
        Transaction masterTran = beginWriteTrans();
        TableReader masterTable = createTable("master",keyColumn,columnList,masterTran);
        if(masterTable != null)
        {
            masterTran.Commit();
            return true;
        }
        else
        {
            masterTran.RollBack();
            return false;
        }
    }

    // 新建一个Page用于存储新的DB
    private boolean newMyPage()
    {
        // TODO: 此处存在问题，1024没有意义。
        ByteBuffer tempBuffer = ByteBuffer.allocate(1024);

        // TODO：QUE:不需要事务编号吗？
        pageOne = new Page(tempBuffer);

        return pageOne != null;
    }

    private boolean loadMyPage(int pageID) throws IOException {
        Transaction readTran = beginReadTrans();
        pageOne = cacheManager.readPage(readTran.tranNum,pageID);

        ByteBuffer thisBufer = pageOne.getPageBuffer();
        // TODO: 读取thisBuffer.


        if (pageOne != null)
        {
            try {
                readTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            readTran.RollBack();
            return false;
        }
    }

    private boolean writeMyPage() throws IOException {
        // 写本页
        Transaction masterTran = beginWriteTrans();
        if(cacheManager.writePage(masterTran.tranNum,pageOne))
        {
            masterTran.Commit();
            return true;
        }
        else
        {
            masterTran.RollBack();
            return false;
        }
    }

    public boolean drop(Transaction thisTran)
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

    //根据传来的表名，主键以及其他的列名来新建一个表放入tableList中
    public Table createTable(String tableName, String key, List<String> columnNameList,List<BasicType> columnTypeList, Transaction thisTran)
    {
        // TODO: 此处存在问题，1024没有意义。
        ByteBuffer tempBuffer = ByteBuffer.allocate(1024);
        // TODO：QUE:不需要事务编号吗？
        Page tablePage = new Page(tempBuffer);
        int pageID = tablePage.getPageID();
        tableList.put(tableName,pageID);
        return new Table(tableName, key, columnList, pageID,cacheManager,thisTran);   //NULL
    }

    //根据传来的表名，主键以及其他的列名来新建一个表放入tableList中
    public View createView(String tableName, List<String> columnNameList,List<BasicType> columnTypeList, Transaction thisTran)
    {
        return new View();
    }

    //根据传来的表名返回Table表对象
    public Table getTable(String tableName, Transaction thisTran)
    {
        if(tableList.get(tableName) == null)
        {
            return null;
        }
        else
        {
            return new Table(tableList.get(tableName),cacheManager,thisTran);
        }
    }

    //给整个数据库中的表全部加锁
    public boolean lock(Transaction thisTran)
    {
        Table master = getTable("master",thisTran);
        if(master.isLocked())
        {
            return false;
        }
        else
        {
            for(String s:tableList.keySet())
            {
                Table temp = getTable(s,thisTran);
                temp.lock(thisTran);
            }
            return true;
        }
    }

    //给数据库中全部的表解锁
    public boolean unLock(Transaction thisTran)
    {
        Table master = getTable("master",thisTran);
        if(master.isLocked())
        {
            for(String s:tableList.keySet())
            {
                Table temp = getTable(s,thisTran);
                temp.unLock(thisTran);
            }
            return true;
        }
        else
        {
            return true;
        }
    }
}
