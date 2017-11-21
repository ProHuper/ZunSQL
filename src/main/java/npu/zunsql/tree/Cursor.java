package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public class Cursor
{

    private Table aimTable;
    private Row thisRow;

    protected Cursor(Table thisTable,Transaction thistran)
    {
        aimTable = thisTable;
        thisRow = aimTable.getRootNode(thistran).getRow();
    }

    public BasicType getColumnType(String columnName)
    {
        return aimTable.getColumn(columnName).getType();
    }

    // 获取某一列的单元字符串。
    // 输入参数：columnName，列名。
    public String getCell_s(String columnName)
    {
        return thisRow.getCell(aimTable.getColumn(columnName).getNumber()).getValue_s();
    }

    // 获取某一列的单元整形。
    // 输入参数：columnName，列名。
    public Integer getCell_i(String columnName)
    {
        return thisRow.getCell(aimTable.getColumn(columnName).getNumber()).getValue_i();
    }

    // 获取某一列的单元双精度。
    // 输入参数：columnName，列名。
    public Double getCell_d(String columnName)
    {
        return thisRow.getCell(aimTable.getColumn(columnName).getNumber()).getValue_d();
    }

    // 获取主键单元字符串。
    public String getKeyCell_s()
    {
        return getCell_s(aimTable.getKeyColumn().getName());
    }

    // 获取主键单元整形。
    public Integer getKeyCell_i()
    {
        return getCell_i(aimTable.getKeyColumn().getName());
    }

    // 获取主键单元双精度。
    public Double getKeyCell_d()
    {
        return getCell_d(aimTable.getKeyColumn().getName());
    }

    public boolean MovetoFirst(Transaction thistran)
    {
        thisRow = aimTable.getRootNode(thistran).getFirstRow();
        return true;
    }

    public boolean MovetoLast(Transaction thistran)
    {
        thisRow = aimTable.getRootNode(thistran).getLastRow();
        return true;
    }

    public boolean MovetoNext(Transaction thistran)
    {
        return MovetoUnpacked(thistran,thisRow.nextRowKey.getValue_s());
    }

    public boolean MovetoPrevious(Transaction thistran)
    {
        return MovetoUnpacked(thistran,thisRow.lastRowKey.getValue_s());
    }

    public boolean MovetoUnpacked(Transaction thistran,String key)
    {
        Cell keyCell = new Cell(key);
        thisRow = aimTable.getRootNode(thistran).getSpecifyRow(keyCell);
        return true;
    }

    public boolean MovetoUnpacked(Transaction thistran,Integer key)
    {
        Cell keyCell = new Cell(key.toString());
        thisRow = aimTable.getRootNode(thistran).getSpecifyRow(keyCell);
        return true;
    }

    public boolean MovetoUnpacked(Transaction thistran,Double key)
    {
        Cell keyCell = new Cell(key.toString());
        thisRow = aimTable.getRootNode(thistran).getSpecifyRow(keyCell);
        return true;
    }

    public boolean Delete(Transaction thistran)
    {
        aimTable.getRootNode(thistran).deleteRow(new Cell(getKeyCell_s()));
        MovetoUnpacked(thistran,thisRow.nextRowKey.getValue_s());
        return true;
    }

    public boolean Insert(Transaction thistran,List<String> stringList)
    {
        Row row = new Row(stringList);
        thisRow = row;
        return aimTable.getRootNode(thistran).insertRow(thisRow);
    }

    public Integer GetKeySize()
    {
        return getKeyCell_s().length();
    }

    public List<String> GetData()
    {
        return thisRow.getStringList();
    }

    public Integer GetDataSize()
    {
        return 1;
    }

    public boolean setData(Transaction thistran,List<String> stringList)
    {
        Cell keyCell = new Cell(stringList.get(aimTable.getKeyColumn().getNumber()));
        aimTable.getRootNode(thistran).deleteRow(keyCell);
        Insert(thistran,stringList);
        return true;
    }
}
