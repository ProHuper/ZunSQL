package npu.zunsql.tree;

import javafx.scene.control.Tab;

import java.util.List;

/**
 * Created by Ed on 2017/10/29.
 */
public class Column
{
    // column中包含一个columntype。
    private Integer columnType;

    // columntype分为三种：int、double、string。
    // 三种类型分别通过static变量来标定。

    // 当column为int类型时，columnType的值为1
    public final static int CT_INT = 1;
    // 当column为double类型时，columnType的值为2
    public final static int CT_DOUBLE = 2;
    // 当column为String类型时，columnType的值为3
    public final static int CT_STRING = 3;

    // column中包含一个columnName。
    private String columnName;

    public Column(Integer type, String name)
    {
        columnType = type;
        columnName = name;
    }

    // 判断类型是否匹配。
    // 输入参数：column，另一个列类型。
    // 输出参数，boolean类型，true表示类型匹配，false表示类型不匹配。
    public boolean IsEqual(Column column)
    {
        // 若类型相同，名字相同，则表明两个列类型为同一列。
        if(column.columnType == columnType && column.columnName.contentEquals(columnName))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
