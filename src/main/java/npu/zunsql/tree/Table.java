package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Table
{
    private final static int LO_LOCKED = 1;
    private final static int LO_SHARED = 2;
    private Integer lock;
    private String tableName;
    private Column keyColumn;
    private List<Column> columns;
    private Node rootNode;

    public Table(String name,Column key,List<Column> colist)
    {
        tableName = name;
        keyColumn = key;
        columns = colist;
        lock = LO_SHARED;
        rootNode = new Node();
    }

    public boolean drop()
    {
        lock = null;
        tableName = null;
        keyColumn = null;
        columns.clear();
        rootNode = new Node();
        return true;
    }

    public boolean clear()
    {
        rootNode = new Node();
        return true;
    }


    public String getTableName()
    {
        return tableName;   //NULL
    }

    public boolean isLocked()
    {
        if (lock == LO_LOCKED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected Node getRootNode()
    {
        return rootNode;
    }

    public boolean getLock()

    {
        if(lock==1)
        {
            return  true;
        }
        else{
            return false;
        }
    }

    public boolean lock()
    {
        lock = LO_LOCKED;   //NULL
        return true;
    }

    public boolean unLock()
    {
        lock = LO_SHARED;   //NULL
        return true;
    }

    public Cursor createCursor()
    {
        Cursor cursor = new Cursor(this);
        return cursor;  //NULL
    }

    public Column getKeyColumn()
    {
        return keyColumn;
    }

    public List<Column> getColumns()
    {
        return columns;
    }

    public Column getColumn(String columnName)
    {
        for(int i = 0; i < columns.size(); i++)
        {
            if(columns.get(i).getColumnName().equals(columnName))
            {
                return columns.get(i);
            }
        }
        return null;
    }




}
