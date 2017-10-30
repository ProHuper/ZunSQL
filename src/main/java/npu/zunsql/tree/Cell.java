package npu.zunsql.tree;

/**
 * Created by Ed on 2017/10/30.
 */
public class Cell
{
    private Column ThisColumn;
    private Integer value;
    private Double dvalue;
    private String Svalue;

    public Cell(Column ThisColumn, Integer ThisValue)
    {

    }

    public Cell(Column ThisColumn, Double ThisValue)
    {

    }

    public Cell(Column ThisColumn, String  ThisValue)
    {

    }

    public Column getColumn()
    {
        Column column = new Column(1," ");
        return column;
    }

    public Integer getvalue_int()
    {
        return 1;
    }

    public Double getvalue_double()
    {
        return 1.0;
    }

    public String getvalue_String()
    {
        return " ";
    }
}
