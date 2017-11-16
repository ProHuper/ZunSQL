package npu.zunsql.tree;

import npu.zunsql.cache.Page;

import java.util.List;

/**
 * Created by Ed on 2017/10/30.
 */
public class Row
{
    // 每个Row中包含一个所有列
    private List<Cell> cellList;
    // 每个Row中包含一个主键
    private Cell keyCell;

    // 除了最小节点外，其他节点都拥有他的左节点
    private Row leftBrotherRow;
    // 除了最大节点外，其他节点都拥有他的右节点
    private Row rightBrotherRow;

    //public boolean nullOrNot;

    public int pageID;

    public Row(Cell key,List<Cell> cList)
    {
        keyCell = key;
        cellList = cList;
    }

    public Row()
    {

        keyCell = null;
        cellList = null;
    }

    public Row(int pagID)
    {
        pageID = pagID;
    }

    public boolean setLeftRow(Row row)
    {
        leftBrotherRow = row;
        return true;
    }

    public Row getLeftRow()
    {
        return leftBrotherRow;
    }

    public boolean setRightRow(Row row)
    {
        rightBrotherRow = row;
        return true;
    }

    public Row getRightRow()
    {
        return rightBrotherRow;
    }

    // 改变row中的某一列，直接传入该单元，函数会根据本单元信息匹配对应列进行修改。
    // 输入参数：thisCell，需要替换的单元。
    // 输出参数：boolean类型，true表示修改成功，false表示修改失败。
    public boolean ChangeCell(Cell thisCell)
    {
        // 返回值
        boolean changeOrNot = false;

        // 若为主键
        if(thisCell.getColumn().isMatch(keyCell.getColumn()))
        {
            keyCell = thisCell;
        }

            // 对所有列进行遍历
        for(int i = 0; i < cellList.size(); i++)
        {
            if(thisCell.getColumn().isMatch(cellList.get(i).getColumn()))
            {
                cellList.set(i,thisCell);
                changeOrNot = true;
            }
        }

        return changeOrNot;
    }

    // 获取某一列的单元。
    // 输入参数：column，列。
    // 输出参数：Cell类型，若成功找到，则返回一个单元，否则返回null。
    public Cell getCell(Column column)
    {
        // 若为主键
        if(column.isMatch(keyCell.getColumn()))
        {
            return keyCell;
        }
        else
        {
            // 对其他列进行遍历
            for(int i = 0; i < cellList.size(); i++)
            {
                if(column.isMatch(cellList.get(i).getColumn()))
                {
                    return cellList.get(i);
                }
            }
        }
        return null;
    }

    public List<Cell> getCellList(){
        return cellList;
    }

    public Cell getKeyCell()
    {
        return keyCell;
    }

    public boolean isMatch(Column key,List<Column> others)
    {
        if (key.isMatch(keyCell.getColumn()) && others.size() == cellList.size())
        {
            for (int i = 0; i < others.size(); i++)
            {
                boolean eqOrNot = false;
                for (int j = 0; j < cellList.size(); j++)
                {
                    if (cellList.get(j).getColumn().isMatch(others.get(i)))
                    {
                        eqOrNot = true;
                    }
                }
                if (!eqOrNot)
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
