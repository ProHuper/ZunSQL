package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.List;

public class QueryResult
{
    private List<String> res;
    private List<Column> header;

    public QueryResult(List<Column> pCol) {
        header=new ArrayList<Column>(pCol);
        return;
    }

    public boolean addRecord(List<String> pRecord)
    {
        return res.addAll(pRecord);
    }

    public List<Column> getHeader() {
        return header;
    }
}
