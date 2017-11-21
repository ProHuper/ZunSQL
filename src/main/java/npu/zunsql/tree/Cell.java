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

    public Cell(Integer  givenValue)
    {
        sValue = givenValue.toString();
    }

    public Cell(Double  givenValue)
    {
        sValue = givenValue.toString();
    }

    public boolean bigerThan(Cell cell)
    {
        return sValue.compareTo(cell.getValue_s()) > 0;
    }

    public boolean equalTo(Cell cell)
    {
        return sValue.contentEquals(cell.getValue_s());
    }

    // 返回本单元的String值
    // 输入参数：无
    // 输出参数：String类型。
    public String getValue_s()
    {
        return sValue;
    }
    public Integer getValue_i()
    {
        return Integer.valueOf(sValue);
    }
    public Double getValue_d()
    {
        return Double.valueOf(sValue);
    }
}
