package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/29.
 */
public class Database
{
    //表示dataBase的名字
    private String dataBaseName;
    //数据库中表的list的集合
    private List<Table> tableList;

    //初始化数据库的名称
    public Database(String name)
    {
        dataBaseName = name;
    }


    public boolean drop()
    {
        return true;
    }

    //开始一个读事务操作
    public Transaction beginReadTrans()
    {
        ReadTran readTran = new ReadTran(1);
        return readTran; //0
    }

    //开始一个写事务
    public Transaction beginWriteTrans()
    {
        WriteTran WriteTran = new WriteTran(1);
        return WriteTran; //0
    }

    //根据传来的表名，主键以及其他的列名来新建一个表放入tableList中
    public Table createTable(String tableName, Column key, List<Column> otherColumnList, Transaction trans)
    {
        Table table = new Table(tableName, key, otherColumnList);
        tableList.add(table);
        return table;   //NULL
    }

    //根据传来的表名返回Table表对象
    public Table getTable(String tableName)
    {
        for(int i = 0; i < tableList.size(); i++)
        {
            if(tableList.get(i).getTableName().contentEquals(tableName))
            {
                return tableList.get(i);
            }
        }
        return null;
    }

    //给整个数据库中的表全部加锁
    public boolean lock()
    {
        if(tableList.get(0).isLocked())
        {
            return false;
        }
        else
        {
            for (int i = 0; i < tableList.size(); i++)
            {
                tableList.get(i).lock();    //给它上锁
            }
            return true;
        }
    }

    //给数据库中全部的表解锁
    public boolean unLock()
    {
        if(tableList.get(0).isLocked())
        {
            for (int i = 0; i < tableList.size(); i++)
            {
                tableList.get(i).unLock(); //给它解锁
            }
            return true;
        }
        else
        {
            return true;
        }
    }
}
