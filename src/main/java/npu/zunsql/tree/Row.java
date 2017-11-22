package npu.zunsql.tree;

import npu.zunsql.cache.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ed on 2017/10/30.
 */
public class Row
{
    // 每个Row中包含一个所有列
    protected List<Cell> cellList = new ArrayList<Cell>();

    protected Cell nextRowKey;

    protected Cell lastRowKey;

    protected Row(List<String> SList)
    {
        for(int i = 0; i < SList.size(); i++)
        {
            cellList.add(new Cell(SList.get(i)));
        }
        nextRowKey = null;
        lastRowKey = null;
    }

    protected List<String> getStringList()
    {
        List<String> SList = new ArrayList<String>();
        for(int i = 0; i < SList.size(); i++)
        {
            SList.add(cellList.get(i).getValue_s());
        }
        return SList;
    }

    protected Cell getCell(int array)
    {
        return cellList.get(array);
    }
}
