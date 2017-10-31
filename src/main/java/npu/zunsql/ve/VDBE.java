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
    private QueryResult currentSelected = null;
    private Map<String, List<ConstList>> map = new HashMap<String, List<ConstList>>();
    private Map<String, QueryResult> queryResults = new HashMap<String, QueryResult>();

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
                        pc--;
                        continue;
                    }
                    else{
                        continue;
                    }
                }

                case Achieve: {
                    queryResults.put(p1,currentSelected);
                    currentSelected=null;
                    continue;
                }

                case CreateDB: {
                    //todo：调用下层方法
                    break;
                }

                case DropDB: {
                    //todo：调用下层方法
                    break;
                }

                case OpenDB: {
                    //todo：调用下层方法
                    break;
                }

                case CloseDB: {
                    //todo：调用下层方法
                    break;
                }

                case CreateTable: {
                    //todo：调用下层方法
                    break;
                }

                case DropTable: {
                    //todo：调用下层方法
                    break;
                }

                case OpenTable: {
                    //todo：调用下层方法
                    break;

                }

                case CloseTable: {
                    //todo：调用下层方法
                    break;
                }

                case Select: {

                    //对比currentSelected的表头与所选择属性的名称是否一致
                    List<Column> header = currentSelected.getHeader();
                    List<ConstList> selectedName = map.get(p1);

                    boolean consistency=true;
                    if(header.size()!=selectedName.size())
                    {
                        //报错
                    }
                    for(int i=0;i<header.size();i++)
                    {
                        if(header.get(i).columnName!=selectedName.get(i).item1)
                        {
                            consistency=false;
                            break;
                        }
                    }

                    if(consistency==true)
                    {
                        for(int i=0;i<selectedName.size();i++)
                        {
                            String columnName = selectedName.get(i).item1;
                            //把数据放入currentSelected中
                        }
                    }


                }

                case Insert: {
                    //创造一个record，并调用下一层的方法进行插入
                    break;
                }

                case Delete: {
                    //todo:调用删除当前记录的方法
                    break;
                }

                case Update: {

                }

                case Set: {

                }

                case Add: {
                    caculate(OpCode.Add, p1, p2, p3);
                    break;
                }

                case Sub: {
                    caculate(OpCode.Sub, p1, p2, p3);
                    break;
                }

                case Mul: {
                    caculate(OpCode.Mul, p1, p2, p3);
                    break;
                }

                case Div: {
                    caculate(OpCode.Div, p1, p2, p3);
                    break;
                }

                case And: {
                    caculate(OpCode.And, p1, p2, p3);
                    break;
                }

                case Or: {
                    caculate(OpCode.Or, p1, p2, p3);
                    break;
                }

                case Not: {
                    String value = getStringValue(p2);
                    
                    if(lowestType(p2) > 1){
                        //TODO error
                    }
                    else{
                        if(Double.valueOf(value) == 0){
                            setRegister(p1, 1);
                        }
                        else setRegister(p1, 0);
                        
                    }
                    break;
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
    
    private void caculate(OpCode op, String p1, String p2, String p3){

        String value1 = getStringValue(p2);
        String value2 = getStringValue(p3);
                
        int type1 = lowestType(value1);
        int type2 = lowestType(value2);

        if(type1 > 1 || type2 > 1){
            //error
        }

        else if(Math.max(type1, type2) == 1){

            double tempP2 = Double.valueOf(p2);
            double tempP3 = Double.valueOf(p3);
            double isReg = checkRegister(p1);
            
            if(isReg == 0){
                //TODO error
                
            }
            else{
                switch (op){
                    case Add:{
                        setRegister(p1,tempP2 + tempP3);
                    }
                    
                    case Sub:{
                        setRegister(p1,tempP2 - tempP3);
                    }
                    
                    case Mul:{
                        setRegister(p1,tempP2 * tempP3);
                    }
                    
                    case Div:{
                        setRegister(p1,tempP2 / tempP3);
                    }
                    case And:{
                        if((tempP2 != 0) && (tempP3 !=0)){
                            setRegister(p1, 1);
                        }
                        else setRegister(p1, 0);
                    }
                    case Or:{
                        if((tempP2 == 0) && (tempP3 ==0)){
                            setRegister(p1, 0);
                        }
                        else setRegister(p1, 1);
                    }
                }
                
               
            }

        }
    }
    
    private String getStringValue(String in){
        
        if(in.equals("$1")){
            return r1+"";
        }
        else if(in.equals("$2")){
            return r2+"";
        }
        
        //TODO 返回当前游标指向记录的名为in的属性值；
        return null;
        
    }

    private double checkRegister(String p){
        
        if(p.equals("$1") || p.equals("$2")){
            return 1;
        }
        
        return 0;
        
    }

    private void setRegister(String p, double value){
        if(p.equals("$1")){
            r1 = value;
        }
        else{
            r2 = value;
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

    private int lowestType(String item)
    {
        int type = 0;

        for(int i = 0; i < item.length(); i++)
        {
            char temp = item.charAt(i);
            if(temp <= '9' && temp >= '0')
            {
                //什么都不做
            }
            if(temp == '.')
            {
                type++;
            }
            else
            {
                type=2;
            }
        }
        return type;
    }
}
