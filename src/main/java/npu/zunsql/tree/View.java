package npu.zunsql.tree;

import java.util.ArrayList;
import java.util.List;

public class View implements TableReader
{
    protected List<Column> columns;
    protected List<Row> rowList;
    protected View()
    {
        super();
        columns = new ArrayList<Column>();
        rowList = new ArrayList<Row>();
    }

    public List<String> getColumns()
    {
        return null;
    }

    public Cursor createCursor(Transaction thistran)
    {
        return new ViewCursor();
    }

    protected Column getColumn(String columnName)
    {
        for(int i = 0; i < columns.size(); i++)
        {
            if(columns.get(i).getName().equals(columnName))
            {
                return columns.get(i);
            }
        }
        return null;
    }
}