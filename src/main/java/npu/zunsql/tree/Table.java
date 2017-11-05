package npu.zunsql.tree;

import javafx.scene.control.Cell;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Table
{

    public final static int TT_READ = 1;
    public final static int TT_WRITE = 1;

    public final static int CT_INT = 1;
    public final static int CT_DOUBLE = 1;
    public final static int CT_STRING = 1;

    public final static int LO_LOCKED = 1;
    public final static int LO_SHARED = 1;

    private Integer Lock;
    private String TableName;
    private Column Key;
    private List<Column> OtherColumn;
    private Row RootRow;

    public Table(String TName,Column KeyColumn,List<Column> OtherColumnPass)
    {
        TableName = TName;
        Key = KeyColumn;
        OtherColumn = OtherColumnPass;
//        for(int i = 0; i < OtherColumn.getColumnType().size(); i++)
//        {
//            ColumnType1.add((Integer)column.getColumnType().get(i));
//        }
//        for(int i = 0; i < column.getColumnName().size(); i++)
//        {
//            ColumnName1.add((String)column.getColumnName().get(i));
//        }
    }

    public boolean Drop(Transaction thistran)
    {
        return true;
    }

    public boolean Clear(Transaction thistran)
    {
        return true;
    }

    public String GetTableName()
    {
        return TableName;   //NULL
    }

    public Cursor CreateCursor(String TableName)
    {
        Table table = new Table(TableName, Key, OtherColumn);
        Cursor cursor = new Cursor(table);
        return cursor;  //NULL
    }
}
