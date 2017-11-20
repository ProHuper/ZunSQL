package npu.zunsql.tree;

import javafx.scene.control.Tab;

import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Ed on 2017/10/29.
 */
public class Column
{
    // column中包含一个columntype。
    private BasicType columnType;

    // column中包含一个columnName。
    private String columnName;

    public Column(BasicType type, String name)
    {
        columnType = type;
        columnName = name;
    }

    public BasicType getType()
    {
        return columnType;
    }

    public String getName()
    {
        return columnName;
    }
}
