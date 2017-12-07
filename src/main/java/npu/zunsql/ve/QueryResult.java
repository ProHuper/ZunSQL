package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.List;

public class QueryResult
{
    private List<List<String>> res = new ArrayList<>();
    private List<Column> header;
    private boolean isSucceed;
    private boolean isSelect;
    private int affected;
    private String resInfo;

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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getAffected() {
        return affected;
    }

    public void addAffected(int affected) {
        this.affected++;
    }

    public String getResInfo() {
        return resInfo;
    }

    public void setResInfo(String resInfo) {
        this.resInfo = resInfo;
    }

    public String resConvert(){
        String result = "";
        return result;
    }
}