package npu.zunsql.ve;

import java.util.List;
import java.util.Map;

public class VirtualMachine
{
	Map<String,QueryResult> tables;

	List<ByteCode> filters;
	List<String> selectedColumns;
	List<AttrInstance> record;
	List<Column> columns;
	List<ByteCode> instuctions;
	QueryResult result;

    String targetTable;
    String pkName;
    String updateAttr;
    String updateValue;
    String tableName;
    Activity activity;
    QueryResult joinResult;

	boolean filtersReadOnly;
	boolean recordReadOnly;
	boolean columnsReadOnly;
	boolean selectedColumnsReadOnly;

    Database db;

	public VirtualMachine()
	{
		filtersReadOnly=true;
		recordReadOnly=true;
		columnsReadOnly=true;
		selectedColumnsReadOnly=true;

        result=null;
		activity=null;
		targetTable=null;

        //todo：这里需要约定数据库文件的名称，暂时定为db
        db=new DataBase("db");
	}

	public QueryResult runAll(List<ByteCode> instuctions)
	{
		for(ByteCode instruction: instuctions)
		{
			return run(instruction);
		}
		return result;
	}

    public QueryResult run(ByteCode instruction)
    {
        OpCode opCode = instruction.opCode;
        String p1 = instruction.p1;
        String p2 = instruction.p2;
        String p3 = instruction.p3;

        switch(opCode)
        {
            case Integer:
                //暂未启用的指令
                break;

            case Float:
                //暂未启用的指令
                break;

            case String:
                //暂未启用的指令
                break;

            case Transaction:
                //todo：调用下层方法
                break;

            case Commit:
                //todo：调用下层方法
                break;

            case Rollback:
                //todo：调用下层方法
                break;

            case CreateDB:
                //todo：调用下层方法
                break;

            case DropDB:
                //todo：调用下层方法
                break;

            case CreateTable:
                columnsReadOnly=true;
                break;

            case DropTable:
                //todo：调用下层方法
                break;

            case Insert:
                activity=Activity.Insert;
                if(targetTable==null)
                {
                    targetTable=p3;
                }
                else
                {
                    log("Can not operate two tables at the same time");
                    //todo:throw
                }

            case Delete:
                activity=Activity.Delete;
                targetTable=p3;
                break;

            case Select:
                activity=Activity.Select;
                targetTable=p3;
                break;

            case Update:
                activity=Activity.Update;
                targetTable=p3;
                break;

            case AddCol:
                if(columnsReadOnly==true)
                {
                    log("error");
                    //throw
                }
                else
                {
                    columns.add(new Column(p1,p2));
                }
                break;

            case BeginPK:
                //在只支持一个属性作为主键的条件下，此操作本无意义
                //但指定主键意味着属性信息输入完毕，因此将columnsReadOnly置为true
                columnsReadOnly=true;
                break;

            case AddPK:
                //在只支持一个属性作为主键的条件下，直接对pkName赋值即可
                pkName=p1;
                break;

            case EndPK:
                //在只支持一个属性作为主键的条件下，此操作无意义
                break;

            case BeginItem:
                recordReadOnly=false;
                break;

            case AddItemCol:
                if(recordReadOnly==false)
                {
                    record.add(new AttrInstance(p1,p2,p3));
                }
                else
                {
                    log("Semantic error");
                    //todo:throw
                }

            case EndItem:
                recordReadOnly=true;
                break;

            case BeginFilter:
                filtersReadOnly=false;
                break;

            case Filter:
                if(filtersReadOnly==false)
                {
                    filters.add(new ByteCode(opCode,p1,p2,p3));
                }
                else
                {
                    log("Semantic error");
                    //todo:throw
                }
                break;

            case EndFilter:
                filtersReadOnly=true;
                break;

            case BeginColSelect:
                selectedColumnsReadOnly=false;
                break;

            case AddColSelect:
                if(selectedColumnsReadOnly==true)
                {
                    log("error");
                    //throw
                }
                else
                {
                    selectedColumns.add(p1);
                }
                break;

            case EndColSelect:
                selectedColumnsReadOnly=true;
                break;

            case BeginJoin:
                //接收到join命令，清空临时表
                joinResult=null;
                break;

            case AddTable:
                tableName=p1;
                //调用下层方法，加载p1表，将自然连接的结果存入joinResult
                join(tableName);
                break;

            case EndJoin:
                //todo：调用下层方法建树


            case Set:
                updateAttr=p1;
                updateValue=p3;
                break;

            case And:

                break;

            case Or:
                //todo
                break;

            case Not:
                //todo
                break;

            case Execute:
                //todo
                break;

            default:
                log("No such bytecode: "+opCode+" "+p1+" "+p2+" "+p3);
                break;

        }
        return result;
    }

    private void log(String info)
    {
        System.out.println(info);
    }

    private void join(string tableName)
    {
        Table table=db.GetTable(tableName);
        Cursor p=new Cursor(table);


        //todo:需要下层提供可以访问表头的方法用以构造QueryResult的表头
        //todo:构造用于存放本次结果的临时表
        //QueryResult iterObj=

        //填充数据
        int count=joinResult.res.size()/joinResult.header.size();
        for(int i=0;i<count;i++)
        {
            while(p!=null)
            {
                //todo:判断p所指向的条目是否与joinResult中第i项满足构成自然连接的条件
            }
        }
        
    }
}