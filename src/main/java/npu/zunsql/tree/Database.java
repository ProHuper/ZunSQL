package npu.zunsql.tree;

import javafx.scene.control.Tab;

import java.util.List;

/**
 * Created by Ed on 2017/10/29.
 */
public class Database
{

    private String DataBaseName;
    private List<Table> TableList;

    public Database(String DBName)
    {

    }

    public Transaction BeginTrans(int TransType)
    {
        Transaction transaction = new Transaction(1);
        return transaction; //0
    }

    public Table CreateTable(String TableName, List<Column> ColumnList)
    {
        Table table = new Table(TableName,ColumnList.get(0), ColumnList);
        TableList.add(table);
        return table;   //NULL
    }

    public Table GetTable(String TableName)
    {
        return TableList.get(TableList.size());
    }

    public boolean Lock()
    {
        return true;
    }

    public boolean UnLock()
    {
        return true;
    }
}
