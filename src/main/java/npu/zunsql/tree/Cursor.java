package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
public class Cursor
{

    private Table aimTable;
    private Row thisRow;

    public Cursor(Table thisTable)
    {
        aimTable = thisTable;
        thisRow = aimTable.getRootNode().getRow();
    }

    //boolean ClearCursor()

    public boolean MovetoFirst()
    {
        thisRow = aimTable.getRootNode().getFirstRow();
        return true;
    }

    public boolean MovetoLast()
    {
        thisRow = aimTable.getRootNode().getLastRow();
        return true;
    }

    public boolean MovetoNext()
    {
        thisRow = thisRow.getRightRow();
        return true;
    }

    public boolean MovetoPrevious()
    {
        thisRow = thisRow.getLeftRow();
        return true;
    }

    public boolean MovetoUnpacked(Cell keycell)
    {
        thisRow = aimTable.getRootNode().getSpecifyRow(keycell);
        return true;
    }

    public boolean Delete(Transaction thistran)
    {
        thisRow = thisRow.getRightRow();
        aimTable.getRootNode().deleteRow(thisRow.getKeyCell());
        return true;
    }

    public boolean Insert(Transaction thistran,Row row)
    {
        thisRow = row;
        return aimTable.getRootNode().insertRow(thisRow);
    }

    public Cell GetKey()
    {
        return thisRow.getKeyCell();
    }

    public Integer GetKeySize()
    {
        return 1;
    }

    public Row GetData()
    {
        return thisRow;
    }

    public Integer GetDataSize()
    {
        return 1;
    }

    public boolean setData(Transaction thistran,Row row)
    {
        thisRow = row;
        return true;
    }
}
