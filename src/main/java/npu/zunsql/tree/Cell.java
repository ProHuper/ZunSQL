package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/30.
 */
public class Cell
{
    private String sValue;

    public Cell(String  givenValue)
    {
        sValue = givenValue;
    }

    public boolean bigerThan(Cell cell)
    {
        return sValue.compareTo(cell.getValue()) > 0;
    }

    public boolean equalTo(Cell cell)
    {
        return sValue.contentEquals(cell.getValue());
    }

    // 返回本单元的String值
    // 输入参数：无
    // 输出参数：String类型。
    public String getValue()
    {
        return sValue;
    }
}
