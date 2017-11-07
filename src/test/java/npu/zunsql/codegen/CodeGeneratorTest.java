package npu.zunsql.codegen;

import npu.zunsql.sqlparser.Parser;
import npu.zunsql.sqlparser.ast.*;
import npu.zunsql.ve.Instruction;
import npu.zunsql.ve.OpCode;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CodeGeneratorTest {
    @Test
    public void testBinaryToExpression() {
        List<BinaryExpression> cases = Arrays.asList(
                new BinaryExpression(
                        new BinaryExpression(QualifiedNameExpression.of("a"), Op.GE, new NumberExpression("1")),
                        Op.AND,
                        new BinaryExpression(new NumberExpression("2"), Op.EQ, QualifiedNameExpression.of("b"))
                )
        );
        List<List<Instruction>> expects = Arrays.asList(
                Arrays.asList(
                        new Instruction(OpCode.Filter, "a", ">=", "1"),
                        new Instruction(OpCode.And, null, null, null),
                        new Instruction(OpCode.Filter, "2", "=", "b")
                )
        );
        for (int i = 0; i < cases.size(); i++) {
            assertEquals(CodeGenerator.BinaryExpressionToFilter(cases.get(i)), expects.get(i));
        }
    }

    @Test
    public void testGenerateByteCodeSelect() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("select * from t where a = 1"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Select, null, null, "t"),
                        new Instruction(OpCode.BeginColSelect, null, null, null),
                        new Instruction(OpCode.AddColSelect, "*", null, null),
                        new Instruction(OpCode.EndColSelect, null, null, null),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Filter, "a", "=", "1"),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("select a, b from t where a = 1 or 2 > b"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Select, null, null, "t"),
                        new Instruction(OpCode.BeginColSelect, null, null, null),
                        new Instruction(OpCode.AddColSelect, "a", null, null),
                        new Instruction(OpCode.AddColSelect, "b", null, null),
                        new Instruction(OpCode.EndColSelect, null, null, null),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Filter, "a", "=", "1"),
                        new Instruction(OpCode.Or, null, null, null),
                        new Instruction(OpCode.Filter, "2", ">", "b"),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeDelete() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("delete from table_1 where x=10 and y=100"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Delete, null, null, "table_1"),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Filter, "x", "=", "10"),
                        new Instruction(OpCode.And, null, null, null),
                        new Instruction(OpCode.Filter, "y", "=", "100"),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeInsert() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("insert into table_1(x,y,z) values(1,2.2,'ccc')"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Insert, null, null, "table_1"),
                        new Instruction(OpCode.BeginItem, null, null, null),
                        new Instruction(OpCode.AddItemCol, "x", "Integer", "1"),
                        new Instruction(OpCode.AddItemCol, "y", "Float", "1"),
                        new Instruction(OpCode.AddItemCol, "z", "String", "cc"),
                        new Instruction(OpCode.EndItem, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeCreate() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("create table test ( x int primary key, y double, z varchar) "))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.CreateTable, null, null, "test"),
                        new Instruction(OpCode.AddCol, "x", "Integer", null),
                        new Instruction(OpCode.AddCol, "y", "Float", null),
                        new Instruction(OpCode.AddCol, "z", "String", null),
                        new Instruction(OpCode.BeginPK, null, null, null),
                        new Instruction(OpCode.AddPK, "x", null, null),
                        new Instruction(OpCode.EndPK, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeDrop() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("drop table test  "))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.DropTable, null, null, "test"),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }
}
