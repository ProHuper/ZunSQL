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
    public boolean isMatch(Column column)
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

    public boolean isEqual(Cell a,Cell b)
    {
        if (a.getColumn().isMatch(this) && b.getColumn().isMatch(this))
        {
            if (columnType == CT_INT)
            {
                return a.getValue_Int() == b.getValue_Int();
            }
            else if (columnType == CT_DOUBLE)
            {
                return abs(a.getValue_Double() - b.getValue_Double()) < 0.000001;
            }
            else
            {
                return a.getValue_String().contentEquals(b.getValue_String());
            }
        }
        else
        {
            return false;
        }
    }
    public boolean isBigger(Cell a,Cell b)
    {
        if (a.getColumn().isMatch(this) && b.getColumn().isMatch(this))
        {
            if (columnType == CT_INT)
            {
                return a.getValue_Int() > b.getValue_Int();
            }
            else if (columnType == CT_DOUBLE)
            {
                return a.getValue_Double() > b.getValue_Double();
            }
            else
            {
                return a.getValue_String().compareTo(b.getValue_String()) > 0;
            }
        }
        else
        {
            return false;
        }
    }
}
