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
    public void testExpressionToInstructionsInternal() {
        List<BinaryExpression> cases = Arrays.asList(
                new BinaryExpression(
                        new BinaryExpression(QualifiedNameExpression.of("a"), Op.GE, new NumberExpression("1")),
                        Op.AND,
                        new BinaryExpression(new NumberExpression("2"), Op.EQ, QualifiedNameExpression.of("b"))
                ),
                new BinaryExpression(
                        new BinaryExpression(new NumberExpression("3"), Op.MUL, new NumberExpression("4")),
                        Op.PLUS,
                        new BinaryExpression(new NumberExpression("5"), Op.MINUS, new NumberExpression("6"))
                ),
                new BinaryExpression(
                        new UnaryExpression(Op.NEG, new NumberExpression("1")),
                        Op.EQ,
                        new BinaryExpression(QualifiedNameExpression.of("a"), Op.PLUS, new NumberExpression("2"))
                )
        );
        List<List<Instruction>> expects = Arrays.asList(
                Arrays.asList(
                        new Instruction(OpCode.Operand, "a", null, null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "GT", null, null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operand, "b", null, null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
                        new Instruction(OpCode.Operator, "And", null, null)
                ),
                Arrays.asList(
                        new Instruction(OpCode.Operand, null, "3", null),
                        new Instruction(OpCode.Operand, null, "4", null),
                        new Instruction(OpCode.Operator, "Mul", null, null),
                        new Instruction(OpCode.Operand, null, "5", null),
                        new Instruction(OpCode.Operand, null, "6", null),
                        new Instruction(OpCode.Operator, "Sub", null, null),
                        new Instruction(OpCode.Operator, "Add", null, null)
                ),
                Arrays.asList(
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "Neg", null, null),
                        new Instruction(OpCode.Operand, "a", null, null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operator, "Add", null, null),
                        new Instruction(OpCode.Operator, "EQ", null, null)
                )
        );
        for (int i = 0; i < cases.size(); i++) {
            assertEquals(CodeGenerator.ExpressionToInstructionsInternal(cases.get(i)), expects.get(i));
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
                        new Instruction(OpCode.Operand, "a", null, null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
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
                        new Instruction(OpCode.Operand, "a", null, null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operand, "b", null, null),
                        new Instruction(OpCode.Operator, "GT", null, null),
                        new Instruction(OpCode.Operator, "Or", null, null),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeDelete() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("delete from table_1 where x+2=10-1"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Delete, null, null, "table_1"),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Operand, "x", null, null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operator, "Add", null, null),
                        new Instruction(OpCode.Operand, null, "10", null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "Sub", null, null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("delete from table_1 where x=(10-1)*3"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Delete, null, null, "table_1"),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Operand, "x", null, null),
                        new Instruction(OpCode.Operand, null, "10", null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operator, "Sub", null, null),
                        new Instruction(OpCode.Operand, null, "3", null),
                        new Instruction(OpCode.Operator, "Mul", null, null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }

    @Test
    public void testGenerateByteCodeInsert() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("insert into table_1(x,y,z) values(1+2,-2.2,'ccc')"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Insert, null, null, "table_1"),
                        new Instruction(OpCode.BeginItem, null, null, null),
                        new Instruction(OpCode.AddItemCol, "x", null, null),
                        new Instruction(OpCode.BeginExpression, null, null, null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operator, "Add", null, null),
                        new Instruction(OpCode.EndExpression, null, null, null),
                        new Instruction(OpCode.AddItemCol, "y", null, null),
                        new Instruction(OpCode.BeginExpression, null, null, null),
                        new Instruction(OpCode.Operand, null, "2.2", null),
                        new Instruction(OpCode.Operator, "Neg", null, null),
                        new Instruction(OpCode.EndExpression, null, null, null),
                        new Instruction(OpCode.AddItemCol, "z", null, null),
                        new Instruction(OpCode.BeginExpression, null, null, null),
                        new Instruction(OpCode.Operand, null, "ccc", null),
                        new Instruction(OpCode.EndExpression, null, null, null),
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

    @Test
    public void testGenerateByteCodeUpdate() {
        assertEquals(
                CodeGenerator.GenerateByteCode(Arrays.asList(Parser.parse("update t1 set x = 1+2 where y = 'ab'"))),
                Arrays.asList(
                        new Instruction(OpCode.Transaction, null, null, null),
                        new Instruction(OpCode.Update, null, null, "t1"),
                        new Instruction(OpCode.Set, "x", null, null),
                        new Instruction(OpCode.BeginExpression, null, null, null),
                        new Instruction(OpCode.Operand, null, "1", null),
                        new Instruction(OpCode.Operand, null, "2", null),
                        new Instruction(OpCode.Operator, "Add", null, null),
                        new Instruction(OpCode.EndExpression, null, null, null),
                        new Instruction(OpCode.BeginFilter, null, null, null),
                        new Instruction(OpCode.Operand, "y", null, null),
                        new Instruction(OpCode.Operand, null, "ab", null),
                        new Instruction(OpCode.Operator, "EQ", null, null),
                        new Instruction(OpCode.EndFilter, null, null, null),
                        new Instruction(OpCode.Execute, null, null, null),
                        new Instruction(OpCode.Commit, null, null, null)
                ));
    }
}
