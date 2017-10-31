package npu.zunsql.ve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Huper on 2017/10/30.
 */
public class VDBE {

    private double r1;
    private double r2;
    private int pc;

    private List<ByteCode> instrustions = new ArrayList<ByteCode>();
    private List<ConstList> inList = new ArrayList<ConstList>();
    private List<ConstList> notInList = new ArrayList<ConstList>();
    private Map<String, List<ConstList>> map = new HashMap<String, List<ConstList>>();
    private Map<String, List<QueryResult>> queryResults = new HashMap<String, List<QueryResult>>();

    public VDBE(List<ByteCode> instrustions){
        this.instrustions = instrustions;
        this.pc = 0;
        this.r1 = 0;
        this.r2 = 0;
    }

    public int vdbeStart(){

        for(pc = 0; pc < instrustions.size(); pc++){

            ByteCode tempCode = instrustions.get(pc);
            String p1 = tempCode.p1;
            String p2 = tempCode.p2;
            String p3 = tempCode.p3;

            switch(tempCode.opCode) {
                case Transaction: {
                    //TODO 下层方法，开始事务。
                    break;
                }

                case Commit: {
                    //TODO 下层方法，提交事务。
                    break;
                }

                case Rollback: {
                    //TODO 下层方法，回滚。
                    break;
                }

                case Next: {
                    //TODO 下层方法 ，移动游标。
                    break;
                }

                case Goto: {
                    pc = Integer.valueOf(p1);
                    pc--;
                    continue;
                }

                case Jump: {
                    double bool = checkRegister(p2);
                    if(bool == 0){
                        pc = Integer.valueOf(p1);
                        continue;
                    }
                    else{
                        continue;
                    }
                }

                case Achieve: {

                }

                case CreateDB: {
                    break;
                }

                case DropDB: {
                    break;
                }

                case OpenDB: {

                }

                case CloseDB: {

                }

                case CreateTable: {
                    break;
                }

                case DropTable: {
                    break;
                }

                case OpenTable: {

                }

                case CloseTable: {

                }

                case Select: {

                }

                case Insert: {

                }

                case Delete: {

                }

                case Update: {

                }

                case Set: {

                }

                case Add: {

                }

                case Sub: {

                }

                case Mul: {

                }

                case Div: {

                }

                case And: {

                }

                case Or: {

                }

                case Not: {

                }

                case In: {

                }

                case Is: {

                }

                case Exists: {

                }

                case EQ: {

                }

                case NE: {

                }

                case GE: {

                }

                case GT: {

                }

                case LE: {

                }

                case LT: {

                }

                case BeginAssemble: {

                }

                case AddItem: {

                }

                case EndAssemble: {

                }
            }

        }

        return 0;
    }

    private double checkRegister(String p){

        if(p.equals("r1")){
            return r1;
        }
        else if(p.equals("r2")){
            return r2;
        }
        return 0;
    }

    private void setRegister(String p, double info){
        if(p.equals("r1")){
            r1 = info;
        }
        else{
            r2 = info;
        }
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

    private int convertToNum(String info){

        if(info.contains(".")){
            return 1;
        }
        else return 0;

    }
}
