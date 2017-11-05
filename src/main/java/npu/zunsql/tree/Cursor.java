package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
public class Cursor
{

    private Table aimtable;
    private Row thisRow;

    //
    private Column ThisColumn;
    private Integer ThisValue;
    //

    public Cursor(Table thisable)
    {

    }

    //boolean ClearCursor()

    public boolean MovetoFirst()
    {
        return true;
    }

    public boolean MovetoLast()
    {
        return true;
    }

    public boolean MovetoNext()
    {
        return true;
    }

    public boolean MovetoPrevious()
    {
        return true;
    }

    public boolean MovetoUnpacked(Cell keycell)
    {
        return true;
    }

    public boolean Delete(Transaction thistran)
    {
        return true;
    }

    public boolean Insert(Transaction thistran,Row thisRow)
    {
        return true;
    }

    public Cell GetKey()
    {

        Cell cell = new Cell(ThisColumn, ThisValue);
        return cell;
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
