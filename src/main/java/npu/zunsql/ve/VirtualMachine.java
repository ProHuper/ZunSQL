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

	String targetTable;
	String pkName;
	String updateAttr;
	String updateValue;
	Activity activity;

	boolean filtersReadOnly;
	boolean recordReadOnly;
	boolean columnsReadOnly;
	boolean selectedColumnsReadOnly;

	public VirtualMachine()
	{
		filtersReadOnly=true;
		recordReadOnly=true;
		columnsReadOnly=true;
		selectedColumnsReadOnly=true;

		activity=null;
		targetTable=null;
	}

	public int runAll(List<ByteCode> instuctions)
	{
		for(ByteCode instruction: instuctions)
		{
			return run(instruction);
		}
		return 0;
	}

	public int run(ByteCode instruction)
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
			activity = Activity.Insert;
			if(targetTable==null)
			{
				targetTable=p3;
			}
			else
			{
				log("Can not operate two tables at the same time");
				//todo:throw
			}
			break;

			case Delete:
			activity = Activity.Delete;
			targetTable=p3;
			break;

			case Select:
			activity = Activity.Select;
			targetTable=p3;
			break;

			case Update:
			activity = Activity.Update;
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

		return 0;
	}

	private void log(String info)
	{
		System.out.println(info);
	}

}