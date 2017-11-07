package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/30.
 */
public class Cell
{
    // cell中包含一个column类型的column用于表示本cell所属的列。
    private Column thisColumn;

    // cell中包含三种类型的值，int、double、String。
    // 每个cell有且仅有一个变量的值有效。
    // 根据Column的type判断cell中的哪个值可用。
    private Integer value;
    private Double dValue;
    private String sValue;

    // 根据不同的cell类型提供不同的cell构造方法，主要包括int、double、string。
    public Cell(Column givenColumn, Integer givenValue)
    {
        thisColumn = givenColumn;
        value = givenValue;
        dValue = 0.0;
        sValue = "";
    }

    public Cell(Column givenColumn, Double givenValue)
    {
        thisColumn = givenColumn;
        dValue = givenValue;
        value = 0;
        sValue = "";
    }

    public Cell(Column givenColumn, String  givenValue)
    {
        thisColumn = givenColumn;
        sValue = givenValue;
        dValue = 0.0;
        value = 0;
    }

    public boolean bigerThan(Cell cell)
    {
        return thisColumn.isBigger(this,cell);
    }

    public boolean equalTo(Cell cell)
    {
        return thisColumn.isEqual(this,cell);
    }
    // 返回列类型。
    // 输入参数：无。
    // 输出参数：column类型。
    public Column getColumn()
    {
        return thisColumn;
    }

    // 返回本单元的int值。
    // 输入参数：无。
    // 输出参数：Integer类型。
    public Integer getValue_Int()
    {
        return value;
    }

    // 返回本单元的double值。
    // 输入参数：无。
    // 输出参数：Double类型。
    public Double getValue_Double()
    {
        return dValue;
    }

    // 返回本单元的String值
    // 输入参数：无
    // 输出参数：String类型。
    public String getValue_String()
    {
        return sValue;
    }
}
