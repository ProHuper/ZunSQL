package npu.zunsql.tree;

import npu.zunsql.cache.Page;
import  npu.zunsql.cache.CacheMgr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ed on 2017/10/29.
 */
public class Database
{
    //表示dataBase的名字
    private String dBName;

    // Mgr页放在第一页
    // 内容包括：database的对应页
    private Page pageOne;

    // page层的Mgr，用于对Page层进行操作。
    private CacheMgr cacheManager;

    // 数据库中的table集合，根据表名，映射页码
    private Map<String,Integer> tableList = new HashMap<String, Integer>();

    // 已经新建了一个Page，只需要进行相关Page写操作。
    protected Database(String name,Page newPage)
    {
        dBName = name;
        cacheManager = new CacheMgr(dBName);
        pageOne = newPage;

        addMasterTable();

        ByteBuffer thisBufer = pageOne.getPageBuffer();
        // TODO: 修改thisBufer.


        writeMyPage();
    }

    // 存在一个db，只需要读取即可。
    protected Database(String name,int pageID)
    {
        dBName = name;
        cacheManager = new CacheMgr(dBName);
        if(!loadMyPage(pageID))
        {
            if (newMyPage())
            {
                if(addMasterTable())
                {
                    ByteBuffer thisBufer = pageOne.getPageBuffer();
                    // TODO: 修改thisBufer.


                    writeMyPage();
                }
            }
        }
    }

    // 首先新建一个Page，然后进行相关Page写操作。
    protected Database(String name)
    {
        dBName = name;
        cacheManager = new CacheMgr(dBName);
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


    private boolean addMasterTable()
    {
        // 添加master table
        Column keyColumn = new Column(3,"tableName");
        Column valueColumn = new Column(1,"pageNumber");
        List<Column> columnList = new ArrayList<>();
        columnList.add(keyColumn);
        columnList.add(valueColumn);
        Transaction masterTran = beginWriteTrans();
        TableReader masterTable = createTable("master",keyColumn,columnList,masterTran);
        if(masterTable != null)
        {
            try {
                masterTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private boolean loadMyPage(int pageID)
    {
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

    private boolean writeMyPage()
    {
        // 写本页
        Transaction masterTran = beginWriteTrans();
        if(cacheManager.writePage(masterTran.tranNum,pageOne))
        {
            try {
                masterTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public Table createTable(String tableName, Column key, List<Column> columnList, Transaction thisTran)
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
    public View createView(String tableName, Column key, List<Column> columnList, Transaction thisTran)
    {
        return new View();
    }

    //根据传来的表名返回Table表对象
    public TableReader getTable(String tableName, Transaction thisTran)
    {
        if(tableList.get(tableName) == null)
        {
            return null;
        }
        else
        {
            return new TableReader(tableList.get(tableName),cacheManager,thisTran);
        }
    }

    //给整个数据库中的表全部加锁
    public boolean lock(Transaction thisTran)
    {
        TableReader master = getTable("master",thisTran);
        if(master.isLocked())
        {
            return false;
        }
        else
        {
            for(String s:tableList.keySet())
            {
                TableReader temp = getTable(s,thisTran);
                temp.lock(thisTran);
            }
            return true;
        }
    }

    //给数据库中全部的表解锁
    public boolean unLock(Transaction thisTran)
    {
        TableReader master = getTable("master",thisTran);
        if(master.isLocked())
        {
            for(String s:tableList.keySet())
            {
                TableReader temp = getTable(s,thisTran);
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
