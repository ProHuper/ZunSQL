package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/30.
 */
public class Row
{
    private List<Cell> Celllist;
    private List<Row> SonRow;
    Row FatherRow;
    Row LeftBrotherRow;
    Row RightBrotherRow;

    public Row(List<Cell> ThisCelllist)
    {
        Celllist = ThisCelllist;
    }

    public boolean ChangeCell(Cell ThisCell)
    {
        return true; //false
    }

    public Cell getCell(Column ThisColumn)
    {
        return Celllist.get(Celllist.size());
    }
}
