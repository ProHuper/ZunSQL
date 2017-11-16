package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/28.
 */
public class Cursor
{

    private Table aimTable;
    private Row thisRowPageID;

    public Cursor(Table thisTable,Transaction thistran)
    {
        aimTable = thisTable;
        thisRowPageID = aimTable.getRootNode(thistran).getRow();

    }

    //boolean ClearCursor()

    public boolean MovetoFirst(Transaction thistran)
    {
        thisRowPageID = aimTable.getRootNode(thistran).getFirstRow();
        return true;
    }

    public boolean MovetoLast(Transaction thistran)
    {
        thisRowPageID = aimTable.getRootNode(thistran).getLastRow();
        return true;
    }

    public boolean MovetoNext(Transaction thistran)
    {
        thisRowPageID = thisRowPageID.getRightRow();
        return true;
    }

    public boolean MovetoPrevious(Transaction thistran)
    {
        thisRowPageID = thisRowPageID.getLeftRow();
        return true;
    }

    public boolean MovetoUnpacked(Transaction thistran,Cell keycell)
    {
        thisRowPageID = aimTable.getRootNode(thistran).getSpecifyRow(keycell);
        return true;
    }

    public boolean Delete(Transaction thistran)
    {
        thisRowPageID = thisRowPageID.getRightRow();
        aimTable.getRootNode(thistran).deleteRow(thisRowPageID.getKeyCell());
        return true;
    }

    public boolean Insert(Transaction thistran,Row row)
    {
        thisRowPageID = row;
        return aimTable.getRootNode(thistran).insertRow(thisRowPageID);
    }

    public Cell GetKey(Transaction thistran)
    {
        return thisRowPageID.getKeyCell();
    }

    public Integer GetKeySize(Transaction thistran)
    {
       if(thisRowPageID.getKeyCell().getType().equals("Integer"))
       {
           return 4;
       }
       else if(thisRowPageID.getKeyCell().getType().equals("Float"))
       {
           return 8;
       }
       else if(thisRowPageID.getKeyCell().getType().equals("String"))
       {
           return thisRowPageID.getKeyCell().getValue_String().length();
       }
       else
       {
           return -1;
       }
    }

    public Row GetData(Transaction thistran)
    {
        return thisRowPageID;
    }

    public Integer GetDataSize(Transaction thistran)
    {
        return 1;
    }

    public boolean setData(Transaction thistran,Row row)
    {
        thisRowPageID = row;
        Cursor deleteRow = new Cursor(aimTable,thistran);
        deleteRow.MovetoUnpacked(thistran,row.getKeyCell());
        deleteRow.Delete(thistran);
        Insert(thistran,row);
        return true;
    }
}
