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

    public Database(String DBName)
    {
        dataBaseName = DBName;
    }

    public Transaction beginTrans(int transType)
    {
        Transaction transaction = new Transaction(1);
        return transaction; //0
    }

    public Table createTable(String tableName, List<Column> columnList)
    {
        Table table = new Table(tableName,columnList.get(0), columnList);
        tableList.add(table);
        return table;   //NULL
    }

    public Table getTable(String tableName)
    {
        int i;
        for(i = 0;i < tableList.size();i++)
        {
            if(tableName == tableList.get(i).getTableName())
            {
                break;
            }
        }
        return tableList.get(i);
    }

    public boolean lock()
    {
        if(tableList.get(0).getLock() == 1)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < tableList.size(); i++)
            {
                tableList.get(i).setLock(1);    //给它上锁
            }
            return true;
        }
    }

    public boolean unLock()
    {
        if(tableList.get(0).getLock() == 1)
        {
            for (int i = 0; i < tableList.size(); i++)
            {
                tableList.get(i).setLock(2); //给它解锁
            }
            return true;
        }
        else
        {
            return true;
        }
    }
}
