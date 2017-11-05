package npu.zunsql.tree;

import javafx.scene.control.Cell;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Table
{

    public final static int TT_READ = 1;
    public final static int TT_WRITE = 2;

    public final static int CT_INT = 1;
    public final static int CT_DOUBLE = 2;
    public final static int CT_STRING = 3;

    public final static int LO_LockED = 1;
    public final static int LO_SHARED = 2;

    private Integer lock;
    private String tableName;
    private Column key;
    private List<Column> otherColumn;
    private Cell rootRow;

    public Table(String TName,Column keyColumn,List<Column> otherColumnPass)
    {
        tableName = TName;
        key = keyColumn;
        otherColumn = otherColumnPass;
    }

    public boolean drop()
    {
        return true;
    }

    public boolean clear()
    {
        return true;
    }

    public String getTableName()
    {
        return tableName;   //NULL
    }

    public Integer getLock()
    {
        return lock;   //NULL
    }

    public void setLock(Integer lockpass)
    {
        lock = lockpass;   //NULL
    }

    public Cursor createCursor(String tableName)
    {
        Table table = new Table(tableName, key, otherColumn);
        Cursor cursor = new Cursor(table);
        return cursor;  //NULL
    }
}
