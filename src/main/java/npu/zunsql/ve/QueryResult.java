package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.List;

public class QueryResult
{
    private List<List<String>> res = new ArrayList<>();
    private List<Column> header;
    private boolean isSucceed;

    public QueryResult(List<Column> pCol) {
        header=new ArrayList<Column>(pCol);
        return;
    }
    public QueryResult(boolean pIsSucceed){
        isSucceed=pIsSucceed;
    }

    public boolean getIsSucceed(){
        return isSucceed;
    }

    public boolean addRecord(List<String> pRecord)
    {
        return res.add(pRecord);
    }

    public List<Column> getHeader() {
        return header;
    }

    public List<String> getHeaderString() {
        List<String> result = new ArrayList<>();
        header.forEach(n -> result.add(n.ColumnName));
        return result;
    }

    public List<List<String>> getRes() {
        return res;
    }
}