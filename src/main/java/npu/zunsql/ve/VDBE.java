package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Huper on 2017/10/30.
 */
public class VDBE {

    private int r1;
    private int r2;
    private int r3;

    private List<ByteCode> instrustions = new ArrayList<ByteCode>();
    private List<ConstList> inList = new ArrayList<ConstList>();
    private List<ConstList> notInList = new ArrayList<ConstList>();
    private Map<String, List<ConstList>> map = new HashMap<String, List<ConstList>>();
    private Map<String, List<QueryResult>> queryResults = new HashMap<String, List<QueryResult>>();

    public VDBE(List<ByteCode> instrustions){
        this.instrustions = instrustions;
    }

    public int vdbeStart(){

        for(ByteCode tempCode: instrustions){

            switch(tempCode.opCode){
                case Transaction:{
                    break;
                }

                case Commit:{
                    break;
                }

                case Rollback:{
                    break;
                }
            }

        }

        return 0;
    }

    private int buildBTree(QueryResult queryResult)
    {
        //TODO 调用下层B+树的方法
        return 0;
    }

    private QueryResult joinTable(List<String> tables)
    {
        QueryResult joined=null;
        if(tables.size()>0)
        {
            //TODO 加载名为tables[0]的树,把结果写入到joined中
            //joined=new QueryResult(new ArrayList<Property>());

        }
        for(int i=1;i<tables.size();i++)
        {
            //TODO 每一轮循环都将joined和tables[i]连接，用结果覆盖joined
        }
        return joined;
    }
}
