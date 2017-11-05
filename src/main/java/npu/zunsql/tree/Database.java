package npu.zunsql.tree;

import javafx.scene.control.Tab;

import java.util.List;

/**
 * Created by Ed on 2017/10/29.
 */
public class Database
{

    private String dataBaseName;
    private List<Table> tableList;

    public Database(String name)
    {
        dataBaseName = name;
    }

    public Transaction beginTrans(int transType)
    {
        Transaction transaction = new Transaction(1);
        return transaction; //0
    }

    public Table createTable(String tableName, Column key, List<Column> otherColumnList, Transaction trans)
    {
        Table table = new Table(tableName, key, otherColumnList);
        tableList.add(table);
        return table;   //NULL
    }

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
