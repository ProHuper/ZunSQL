package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Table
{
    public final static int LO_LOCKED = 1;
    public final static int LO_SHARED = 2;
    private Integer lock;
    private String tableName;
    private Column keyColumn;
    private List<Column> columns;
    private Node rootNode;

    // 已经新建好了一个page，只需要填写相关table信息
    public Table(String name,Column key,List<Column> coList,int pageID)
    {
        tableName = name;
        keyColumn = key;
        columns = coList;
        lock = LO_SHARED;
        rootNode = new Node();
    }

    // 已有page，只需要加载其中的信息。
    public Table(String name,int pageID)
    {

    }

    // 需要自己新建Page，并填写相关table信息
    public Table(String name,Column key,List<Column> coList)
    {

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
