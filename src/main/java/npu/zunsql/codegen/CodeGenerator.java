package npu.zunsql.codegen;

import java.util.ArrayList;
import java.util.List;

import npu.zunsql.sqlparser.ast.*;
import npu.zunsql.ve.Instruction;
import npu.zunsql.ve.OpCode;

public class CodeGenerator {
    public static final List<Instruction> GenerateByteCode(List<Relation> statements) {
        List<Instruction> ret = new ArrayList<>();
        Boolean inTransaction = false;
        for (Relation statement: statements) {
            if (statement instanceof Begin) {
                inTransaction = true;
                ret.add(new Instruction(OpCode.Transaction, null, null, null));
                continue;
            }
            if (statement instanceof Commit) {
                inTransaction = false;
                ret.add(new Instruction(OpCode.Commit, null, null, null));
                continue;
            }
            if (statement instanceof Rollback) {
                inTransaction = false;
                ret.add(new Instruction(OpCode.Rollback, null, null, null));
                continue;
            }
            if (!inTransaction) {
                ret.add(new Instruction(OpCode.Transaction, null, null, null));
                TYPE_SWITCH:
                {
                    if (statement instanceof Select) {
                        String table = ((Select)statement).from.get(0).tableName.names.get(0);
                        ret.add(new Instruction(OpCode.Select, null, null, table));
                        ret.add(new Instruction(OpCode.BeginColSelect, null, null, null));
                        List<Expression> exprs = ((Select)statement).exprs;
                        for (Expression expr: exprs) {
                            if (expr instanceof WildcardExpression) {
                                ret.add(new Instruction(OpCode.AddColSelect, "*", null, null));
                            } else if (expr instanceof QualifiedNameExpression) {
                                String name = ((QualifiedNameExpression) expr).qname.names.get(0);
                                ret.add(new Instruction(OpCode.AddColSelect, name, null, null));
                            }
                            // Do not support other expression now due to lack of OpCode type.
                        }
                        Expression where = ((Select)statement).where;
                        if (where != null) {
                            ret.add(new Instruction(OpCode.BeginFilter, null, null, null));
                            if (where instanceof BinaryExpression) {
                                BinaryExpression bin = (BinaryExpression)where;
                                ret.addAll(BinaryExpressionToFilter(bin));
                            }
                            ret.add(new Instruction(OpCode.EndFilter, null, null, null));
                            // Do not support other expression now due to lack of OpCode type.
                        }
                        ret.add(new Instruction(OpCode.EndColSelect, null, null, null));
                        break TYPE_SWITCH;
                    }
                }
                ret.add(new Instruction(OpCode.Execute, null, null, null));
                ret.add(new Instruction(OpCode.Commit, null, null, null));
            }
        }
        return ret;
    }

    public static final List<Instruction> BinaryExpressionToFilter(BinaryExpression bin) {
        List<Instruction> ret = new ArrayList<>();
        switch (bin.operator) {
            case OR:
                ret.addAll(BinaryExpressionToFilter((BinaryExpression)(bin.left)));
                ret.add(new Instruction(OpCode.Or, null, null, null));
                ret.addAll(BinaryExpressionToFilter((BinaryExpression)(bin.right)));
                break;
            case AND:
                ret.addAll(BinaryExpressionToFilter((BinaryExpression)(bin.left)));
                ret.add(new Instruction(OpCode.And, null, null, null));
                ret.addAll(BinaryExpressionToFilter((BinaryExpression)(bin.right)));
                break;
            default:
                ret.add(new Instruction(OpCode.Filter, bin.left.toString(), OpToString(bin.operator), bin.right.toString()));
        }
        return ret;
    }

    private static String OpToString(Op op) {
        String ret = null;
        switch (op) {
            case GT:
                ret = ">";
                break;
            case LT:
                ret = "<";
                break;
            case GE:
                ret = ">=";
                break;
            case LE:
                ret = "<=";
                break;
            case EQ:
                ret = "=";
        }
        return ret;
    }
}
