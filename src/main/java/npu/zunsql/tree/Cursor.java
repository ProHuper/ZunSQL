package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public abstract class Cursor
{
    protected Cursor()
    {
        ;
    }

    // 获取列类型
    // 输入参数：columnName，列名。
    public abstract BasicType getColumnType(String columnName);

    // 获取某一列的单元字符串。
    // 输入参数：columnName，列名。
    public abstract String getCell_s(String columnName);

    // 获取某一列的单元整形。
    // 输入参数：columnName，列名。
    public abstract Integer getCell_i(String columnName);

    // 获取某一列的单元双精度。
    // 输入参数：columnName，列名。
    public abstract Double getCell_d(String columnName);

    // 获取主键单元字符串。
    public abstract String getKeyCell_s();

    // 获取主键单元整形。
    public abstract Integer getKeyCell_i();

    // 获取主键单元双精度。
    public abstract Double getKeyCell_d();

    // 游标移至首条
    public abstract boolean moveToFirst(Transaction thisTran);

    // 游标移至末尾
    public abstract boolean moveToLast(Transaction thisTran);

    // 游标后移一条
    public abstract boolean moveToNext(Transaction thisTran);

    // 游标前移一条
    public abstract boolean moveToPrevious(Transaction thisTran);

    // 游标移至指定位
    // 输入参数：key主键的字符串值
    public abstract boolean moveToUnpacked(Transaction thisTran,String key);

    // 游标移至指定位
    // 输入参数：key主键的整型值
    public abstract boolean moveToUnpacked(Transaction thisTran,Integer key);

    // 游标移至指定位
    // 输入参数：key主键的双精度值
    public abstract boolean moveToUnpacked(Transaction thisTran,Double key);

    // 删除本条
    public abstract boolean delete(Transaction thistran);

    // 插入一条
    public abstract boolean insert(Transaction thisTran,List<String> stringList);

    // 获取本条内容，字符串值
    public abstract List<String> getData();

    // 调整本条内容
    public abstract boolean setData(Transaction thisTran,List<String> stringList);
}


class TableCursor extends Cursor
{
    protected Table aimTable;
    protected int thisRowID;
    protected Node thisNode;

    protected TableCursor(Table thisTable, Transaction thisTran)
    {
        super();
        aimTable = thisTable;
        thisRowID = 0;
    }

    public BasicType getColumnType(String columnName)
    {
        return aimTable.getColumn(columnName).getType();
    }

    // 获取某一列的单元字符串。
    // 输入参数：columnName，列名。
    public String getCell_s(String columnName)
    {
        return thisNode.getRow(thisRowID).getCell(aimTable.getColumn(columnName).getNumber()).getValue_s();
    }

    // 获取某一列的单元整形。
    // 输入参数：columnName，列名。
    public Integer getCell_i(String columnName)
    {
        return thisNode.getRow(thisRowID).getCell(aimTable.getColumn(columnName).getNumber()).getValue_i();
    }

    // 获取某一列的单元双精度。
    // 输入参数：columnName，列名。
    public Double getCell_d(String columnName)
    {
        return thisNode.getRow(thisRowID).getCell(aimTable.getColumn(columnName).getNumber()).getValue_d();
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

    // 游标移至首条
    public boolean moveToFirst(Transaction thisTran)
    {
        thisNode = aimTable.getRootNode(thisTran);
        thisRowID = 0;
        return true;
    }

    // 游标移至末尾
    public boolean moveToLast(Transaction thisTran)
    {
        //TODO:游标移至末尾
        return true;
    }

    // 游标后移一条
    public boolean moveToNext(Transaction thisTran)
    {
        //TODO:游标后移一位
        return true;
    }

    // 游标前移一条
    public boolean moveToPrevious(Transaction thisTran)
    {
        //TODO:游标前移一位
        return true;
    }

    // 游标移至指定位
    // 输入参数：key主键的字符串值
    public boolean moveToUnpacked(Transaction thisTran,String key)
    {
        Cell keyCell = new Cell(key);
        // TODO:游标移至指定位
        return true;
    }

    // 游标移至指定位
    // 输入参数：key主键的整型值
    public boolean moveToUnpacked(Transaction thisTran,Integer key)
    {
        Cell keyCell = new Cell(key.toString());
        // TODO:游标移至指定位
        return true;
    }

    // 游标移至指定位
    // 输入参数：key主键的双精度值
    public boolean moveToUnpacked(Transaction thisTran,Double key)
    {
        Cell keyCell = new Cell(key.toString());
        // TODO:游标移至指定位
        return true;
    }

    // 删除本条
    public boolean delete(Transaction thisTran)
    {
        aimTable.getRootNode(thisTran).deleteRow(new Cell(getKeyCell_s()),thisTran);
        //TODO:游标移至下一位
        return true;
    }

    // 插入一条
    public boolean insert(Transaction thisTran,List<String> stringList)
    {
        Row row = new Row(stringList);
        thisRowID = aimTable.getRootNode(thisTran).insertRow(row,thisTran);
    }

    // 获取本条内容，字符串值
    public List<String> getData()
    {
        return thisRowID.getStringList();
    }

    // 调整本条内容
    public boolean setData(Transaction thistran,List<String> stringList)
    {
        delete(thistran);
        insert(thistran,stringList);
        return true;
    }
}

class ViewCursor extends Cursor
{
    protected View aimView;
    protected int RowID;

    protected ViewCursor(View aView)
    {
        super();
        aimView = aView;
        RowID = 0;
    }

    // 获取列类型
    // 输入参数：columnName，列名。
    public BasicType getColumnType(String columnName)
    {
        return aimView.getColumn(columnName).getType();
    }

    // 获取某一列的单元字符串。
    // 输入参数：columnName，列名。
    public String getCell_s(String columnName)
    {
        return aimView.rowList.get(RowID).getCell(aimView.getColumn(columnName).getNumber()).getValue_s();
    }

    // 获取某一列的单元整形。
    // 输入参数：columnName，列名。
    public Integer getCell_i(String columnName)
    {
        return aimView.rowList.get(RowID).getCell(aimView.getColumn(columnName).getNumber()).getValue_i();
    }

    // 获取某一列的单元双精度。
    // 输入参数：columnName，列名。
    public Double getCell_d(String columnName)
    {
        return aimView.rowList.get(RowID).getCell(aimView.getColumn(columnName).getNumber()).getValue_d();
    }

    // 获取第一列单元字符串。
    public String getKeyCell_s()
    {
        return aimView.rowList.get(RowID).getCell(0).getValue_s();
    }

    // 获取第一列单元整形。
    public Integer getKeyCell_i()
    {
        return aimView.rowList.get(RowID).getCell(0).getValue_i();
    }

    // 获取第一列单元双精度。
    public Double getKeyCell_d()
    {
        return aimView.rowList.get(RowID).getCell(0).getValue_d();
    }

    // 游标移至首条
    public boolean moveToFirst(Transaction thisTran)
    {
        RowID = 0;
        return true;
    }

    // 游标移至末尾
    public boolean moveToLast(Transaction thisTran)
    {
        RowID = aimView.rowList.size() - 1;
        return true;
    }

    // 游标后移一条
    public boolean moveToNext(Transaction thisTran)
    {
        if(RowID < aimView.rowList.size() - 1)
        {
            RowID++;
            return true;
        }
        else
        {
            return false;
        }
    }

    // 游标前移一条
    public boolean moveToPrevious(Transaction thisTran)
    {
        if(RowID > 0)
        {
            RowID--;
            return true;
        }
        else
        {
            return false;
        }
    }

    // 游标移至指定位
    // 输入参数：key主键的字符串值
    public boolean moveToUnpacked(Transaction thisTran,String key)
    {
        return false;
    }

    // 游标移至指定位
    // 输入参数：key主键的整型值
    public boolean moveToUnpacked(Transaction thisTran,Integer key)
    {
        return false;
    }

    // 游标移至指定位
    // 输入参数：key主键的双精度值
    public boolean moveToUnpacked(Transaction thisTran,Double key)
    {
        return false;
    }

    // 删除本条
    public boolean delete(Transaction thistran)
    {
        return false;
    }

    // 插入一条
    public boolean insert(Transaction thisTran,List<String> stringList)
    {
        return false;
    }

    // 获取本条内容，字符串值
    public List<String> getData()
    {
        return aimView.rowList.get(RowID).getStringList();
    }

    // 调整本条内容
    public boolean setData(Transaction thisTran,List<String> stringList)
    {
        return false;
    }
}