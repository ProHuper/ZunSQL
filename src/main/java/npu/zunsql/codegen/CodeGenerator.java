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
            }
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
                        ret.add(new Instruction(OpCode.EndColSelect, null, null, null));
                        Expression where = ((Select)statement).where;
                        ret.addAll(WhereToInstruction(where));
                        break TYPE_SWITCH;
                    }
                    if (statement instanceof Insert) {
                        Insert insert = (Insert)statement;
                        String table = insert.table.tableName.names.get(0);
                        ret.add(new Instruction(OpCode.Insert, null, null, table));
                        ret.add(new Instruction(OpCode.BeginItem, null, null, null));
                        for (int i = 0; i < insert.names.size(); i++) {
                            String name = ((QualifiedNameExpression)insert.names.get(0)).qname.names.get(0);
                            String type = null;
                            String value = insert.expressions.get(0).toString();
                            if (insert.expressions.get(0) instanceof StringExpression) {
                                type = "String";
                            } else if (insert.expressions.get(0) instanceof NumberExpression) {
                                if (value.contains(".")) {
                                    type = "Float";
                                } else {
                                    type = "Integer";
                                }
                            }
                            ret.add(new Instruction(OpCode.AddItemCol, name, type, value));
                        }
                        ret.add(new Instruction(OpCode.EndItem, null, null, null));
                        break TYPE_SWITCH;
                    }
                    if (statement instanceof Create) {
                        Create create = (Create)statement;
                        String table = create.table.tableName.names.get(0);
                        ret.add(new Instruction(OpCode.CreateTable, null, null, table));
                        for (Column col: create.columns) {
                            String name = ((QualifiedNameExpression)col.name).qname.names.get(0);
                            String type = DataTypeToString(col.type);
                            ret.add(new Instruction(OpCode.AddCol, name, type, null));
                        }
                        ret.add(new Instruction(OpCode.BeginPK, null, null, null));
                        for (Column col: create.columns) {
                            if (col.isPrimaryKey) {
                                String name = ((QualifiedNameExpression)col.name).qname.names.get(0);
                                ret.add(new Instruction(OpCode.AddPK, name, null, null));
                            }
                        }
                        ret.add(new Instruction(OpCode.EndPK, null, null, null));
                        break TYPE_SWITCH;
                    }
                    if (statement instanceof Delete) {
                        Delete delete = (Delete)statement;
                        String table = delete.from.tableName.names.get(0);
                        ret.add(new Instruction(OpCode.Delete, null, null, table));
                        Expression where = delete.where;
                        ret.addAll(WhereToInstruction(where));
                        break TYPE_SWITCH;
                    }
                    if (statement instanceof Drop) {
                        Drop drop = (Drop)statement;
                        String table = drop.table.tableName.names.get(0);
                        ret.add(new Instruction(OpCode.DropTable, null, null, table));
                        break TYPE_SWITCH;
                    }
                    if (statement instanceof Update) {
                        Update update = (Update)statement;
                        String table = update.table.tableName.names.get(0);
                        ret.add(new Instruction(OpCode.Update, null, null, table));

                        Expression where = update.where;
                        ret.addAll(WhereToInstruction(where));
                        for (Assignment a: update.updates) {
                            String name = a.name.names.get(0);
                            // TODO(Cholerae): set translation
                        }
                    }
                }
                ret.add(new Instruction(OpCode.Execute, null, null, null));
            if (!inTransaction) {
                ret.add(new Instruction(OpCode.Commit, null, null, null));
            }
        }
        return ret;
    }

    private static List<Instruction> WhereToInstruction(Expression where) {
        List<Instruction> ret = new ArrayList<>();
        if (where != null) {
            ret.add(new Instruction(OpCode.BeginFilter, null, null, null));
            if (where instanceof BinaryExpression) {
                BinaryExpression bin = (BinaryExpression)where;
                ret.addAll(BinaryExpressionToFilter(bin));
            }
            ret.add(new Instruction(OpCode.EndFilter, null, null, null));
            // Do not support other expression now due to lack of OpCode type.
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

    private static String DataTypeToString(DataType dt) {
        String ret = null;
        switch (dt) {
            case INT:
                ret = "Integer";
                break;
            case DOUBLE:
                ret = "Float";
                break;
            case VARCHAR:
                ret = "String";
        }
        return ret;
    }
}
