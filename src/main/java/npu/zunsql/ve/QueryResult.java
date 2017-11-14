package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.List;

public class QueryResult
{
    private List<List<String>> res = new ArrayList<>();
    private List<Column> header;

    public QueryResult(List<Column> pCol) {
        header=new ArrayList<Column>(pCol);
        return;
    }

    public boolean addRecord(List<String> pRecord)
    {
        return res.add(pRecord);
    }

    public List<Column> getHeader() {
        return header;
    }

    public List<List<String>> getRes() {
        return res;
    }
}
