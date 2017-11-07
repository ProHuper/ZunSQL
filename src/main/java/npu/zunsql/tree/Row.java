package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/30.
 */
public class Row
{
    // 每个Row中包含一个其他列
    private List<Cell> otherCellList;
    // 每个Row中包含一个主键
    private Cell keyCell;
    // 除了叶子节点的其他节点都拥有他的子节点
    private List<Row> sonRow;
    // 除了根节点的其他节点都拥有他的父亲节点
    private Row fatherRow;
    // 除了最小节点外，其他节点都拥有他的左节点
    private Row leftBrotherRow;
    // 除了最大节点外，其他节点都拥有他的右节点
    private Row rightBrotherRow;

    public Row(Cell key,List<Cell> others)
    {
        keyCell = key;
        otherCellList = others;
    }

    // 改变row中的某一列，直接传入该单元，函数会根据本单元信息匹配对应列进行修改。
    // 输入参数：thisCell，需要替换的单元。
    // 输出参数：boolean类型，true表示修改成功，false表示修改失败。
    public boolean ChangeCell(Cell thisCell)
    {
        // 返回值
        boolean changeOrNot = false;

        // 若为主键
        if(thisCell.getColumn().IsEqual(keyCell.getColumn()))
        {
            keyCell = thisCell;
            changeOrNot = true;
        }
        else
        {
            // 对其他列进行遍历
            for(int i = 0; i < otherCellList.size(); i++)
            {
                if(thisCell.getColumn().IsEqual(otherCellList.get(i).getColumn()))
                {
                    otherCellList.set(i,thisCell);
                    changeOrNot = true;
                }
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
        if(column.IsEqual(keyCell.getColumn()))
        {
            return keyCell;
        }
        else
        {
            // 对其他列进行遍历
            for(int i = 0; i < otherCellList.size(); i++)
            {
                if(column.IsEqual(otherCellList.get(i).getColumn()))
                {
                    return otherCellList.get(i);
                }
            }
        }
        return null;
    }
}
