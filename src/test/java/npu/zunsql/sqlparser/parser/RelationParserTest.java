package npu.zunsql.sqlparser.parser;

import org.jparsec.Parser;
import npu.zunsql.sqlparser.ast.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static npu.zunsql.sqlparser.parser.RelationParser.TABLE;
import static npu.zunsql.sqlparser.parser.RelationParser.TABLE_NAME;

public class RelationParserTest {

    @Test
    public void testTable() {
        TerminalParserTest.assertParser(TABLE, "a.b", table("a", "b"));
    }

    @Test
    public void testTableName() {
        TerminalParserTest.assertParser(TABLE_NAME, "a.b", tableRelation("a", "b"));
    }

    @Test
    public void testSelectClause() {
        Parser<Boolean> parser = RelationParser.selectClause();
        TerminalParserTest.assertParser(parser, "select", false);
        TerminalParserTest.assertParser(parser, "select distinct", true);
    }

    @Test
    public void testFromClause() {
        Parser<List<TableRelation>> parser = RelationParser.fromClause();
        TerminalParserTest.assertParser(parser, "from a", Arrays.asList(tableRelation("a")));
        TerminalParserTest.assertParser(parser, "from a, b", Arrays.asList(tableRelation("a"), tableRelation("b")));
    }

    @Test
    public void testSelect() {
        Parser<Relation> parser = RelationParser.select();
        TerminalParserTest.assertParser(parser, "select distinct 1, 2 from t1, t2",
                new Select(true,
                        Arrays.asList(ExpressionParserTest.number(1), ExpressionParserTest.number(2)),
                        Arrays.asList(tableRelation("t1"), tableRelation("t2")),
                        null));
        TerminalParserTest.assertParser(parser, "select * from t where 1 = 1",
                new Select(false,
                        Arrays.asList(new WildcardExpression(QualifiedName.of())),
                        Arrays.asList(tableRelation("t")),
                        new BinaryExpression(
                                ExpressionParserTest.number(1), Op.EQ, ExpressionParserTest.number(1))));
        TerminalParserTest.assertParser(parser, "select a+b from t where a > 1",
                new Select(false,
                        Arrays.asList(new BinaryExpression(
                                ExpressionParserTest.name("a"), Op.PLUS, ExpressionParserTest.name("b"))),
                        Arrays.asList(tableRelation("t")),
                        new BinaryExpression(
                                ExpressionParserTest.name("a"), Op.GT, ExpressionParserTest.number(1))));
    }

    @Test
    public void testDelete() {
        Parser<Relation> parser = RelationParser.delete();
        TerminalParserTest.assertParser(parser, "delete from t1",
                new Delete(tableRelation("t1"), null));
        TerminalParserTest.assertParser(parser, "delete from t where 1 = 1",
                new Delete(tableRelation("t"),
                        new BinaryExpression(
                                ExpressionParserTest.number(1), Op.EQ, ExpressionParserTest.number(1))));
        TerminalParserTest.assertParser(parser, "delete from t where a > 1",
                new Delete(tableRelation("t"),
                        new BinaryExpression(
                                ExpressionParserTest.name("a"), Op.GT, ExpressionParserTest.number(1))));
    }

    @Test
    public void testInsert() {
        Parser<Relation> parser = RelationParser.insert();
        TerminalParserTest.assertParser(parser, "insert into t (a, b) values (1, 2)",
                new Insert(tableRelation("t"),
                        Arrays.asList(QualifiedNameExpression.of("a"), QualifiedNameExpression.of("b")),
                        Arrays.asList(ExpressionParserTest.number(1), ExpressionParserTest.number(2))));
        TerminalParserTest.assertParser(parser, "insert into c (d, c) values (1+2, 'fuck')",
                new Insert(tableRelation("c"),
                        Arrays.asList(QualifiedNameExpression.of("d"), QualifiedNameExpression.of("c")),
                        Arrays.asList(new BinaryExpression(
                                ExpressionParserTest.number(1), Op.PLUS, ExpressionParserTest.number(2)
                        ), new StringExpression("fuck"))));
    }

    @Test
    public void testUpdate() {
        Parser<Relation> parser = RelationParser.update();
        TerminalParserTest.assertParser(parser, "update t set a = 1 where puck = 'shit'",
                new Update(tableRelation("t"),
                        Arrays.asList(new Assignment(QualifiedName.of("a"), ExpressionParserTest.number(1))),
                        new BinaryExpression(
                                ExpressionParserTest.name("puck"), Op.EQ, new StringExpression("shit"))));
        TerminalParserTest.assertParser(parser, "update t set a = 1, b = 'shit' where a > 2 and not b = 'shit'",
                new Update(tableRelation("t"),
                        Arrays.asList(
                                new Assignment(QualifiedName.of("a"), ExpressionParserTest.number(1)),
                                new Assignment(QualifiedName.of("b"), new StringExpression("shit"))),
                        new BinaryExpression(
                                new BinaryExpression(ExpressionParserTest.name("a"), Op.GT, ExpressionParserTest.number(2)),
                                Op.AND,
                                new UnaryExpression(Op.NOT,
                                        new BinaryExpression(ExpressionParserTest.name("b"), Op.EQ, new StringExpression("shit"))))));
    }

    @Test
    public void testCreate() {
        Parser<Relation> parser = RelationParser.create();
        TerminalParserTest.assertParser(parser, "create table t ( id int , name varchar, puck double primary key)",
                new Create(tableRelation("t"),
                        Arrays.asList(
                                new Column(QualifiedNameExpression.of("id"), DataType.INT, false),
                                new Column(QualifiedNameExpression.of("name"), DataType.VARCHAR, false),
                                new Column(QualifiedNameExpression.of("puck"), DataType.DOUBLE, true)
                        )));
        TerminalParserTest.assertParser(parser, "create table t ( id int, name varchar primary key, puck double )",
                new Create(tableRelation("t"),
                        Arrays.asList(
                                new Column(QualifiedNameExpression.of("id"), DataType.INT, false),
                                new Column(QualifiedNameExpression.of("name"), DataType.VARCHAR, true),
                                new Column(QualifiedNameExpression.of("puck"), DataType.DOUBLE, false)
                        )));
        TerminalParserTest.assertParser(parser, "create table t ( id int primary key, name varchar, puck double )",
                new Create(tableRelation("t"),
                        Arrays.asList(
                                new Column(QualifiedNameExpression.of("id"), DataType.INT, true),
                                new Column(QualifiedNameExpression.of("name"), DataType.VARCHAR, false),
                                new Column(QualifiedNameExpression.of("puck"), DataType.DOUBLE, false)
                        )));
    }

    static Relation table(String... names) {
        return new TableRelation(QualifiedName.of(names));
    }

    static TableRelation tableRelation(String... names) {
        return new TableRelation(QualifiedName.of(names));
    }
}
