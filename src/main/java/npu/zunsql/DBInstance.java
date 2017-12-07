package npu.zunsql;

import npu.zunsql.codegen.CodeGenerator;
import npu.zunsql.sqlparser.Parser;
import npu.zunsql.sqlparser.ast.Relation;
import npu.zunsql.ve.Instruction;
import npu.zunsql.ve.QueryResult;
import npu.zunsql.ve.VirtualMachine;
import npu.zunsql.tree.Database;

import java.util.List;
import java.util.ArrayList;

public class DBInstance
{
	private Database db;
	
	public boolean Open(String name)
	{	
		db = new Database(name);
		if(db!=null)return true;
		  else return false;
	}

	public QueryResult Execute(String statement)
	{
		//定义一个List<Relation>，将parse()返回的Relation对象填入
		List<Relation> statements = new ArrayList<Relation>();
		statements.add(Parser.parse(statement));

		//得到查询的结果
		VirtualMachine vm = new VirtualMachine(db);
		
		List<Instruction> array = CodeGenerator.GenerateByteCode(statements);
		for(Instruction s: array){
			vm.run(s);
		}
		
		//执行select语句返回结果集，其他语句默认情况下执行成功,结果集为null。
        return vm.result;
	}
	
	public boolean Close()
	{
		if(db!=null&&db.close())return true;
		else return false;
	}
}
