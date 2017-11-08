package npu.zunsql.sqlparser.parser;

import npu.zunsql.sqlparser.ast.*;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.junit.Test;

import static npu.zunsql.sqlparser.parser.ExpressionParser.NUMBER;
import static npu.zunsql.sqlparser.parser.ExpressionParser.QUALIFIED_NAME;

public class ExpressionParserTest {

    @Test
    public void testNumber() {
        TerminalParserTest.assertParser(NUMBER, "1.2", new NumberExpression("1.2"));
    }

    @Test
    public void testQualifiedName() {
        Parser<Expression> parser = QUALIFIED_NAME;
        TerminalParserTest.assertParser(parser, "a", name("a"));
        TerminalParserTest.assertParser(parser, "a . bc", name("a", "bc"));
    }

    @Test
    public void testQualifiedWildcard() {
        TerminalParserTest.assertParser(ExpressionParser.QUALIFIED_WILDCARD, "a.b.*",
                new WildcardExpression(QualifiedName.of("a", "b")));
    }

    @Test
    public void testWildcard() {
        TerminalParserTest.assertParser(ExpressionParser.WILDCARD, "a.b.*",
                new WildcardExpression(QualifiedName.of("a", "b")));
        TerminalParserTest.assertParser(ExpressionParser.WILDCARD, "*",
                new WildcardExpression(QualifiedName.of()));
    }

    @Test
    public void testString() {
        TerminalParserTest.assertParser(ExpressionParser.STRING, "'foo'", new StringExpression("foo"));
    }

    @Test
    public void testFunctionCall() {
        Parser<Expression> parser = ExpressionParser.functionCall(NUMBER);
        TerminalParserTest.assertParser(parser, "f()", FunctionExpression.of(QualifiedName.of("f")));
        TerminalParserTest.assertParser(parser, "a.b(1)",
                FunctionExpression.of(QualifiedName.of("a", "b"), number(1)));
        TerminalParserTest.assertParser(parser, "a.b(1, 2)",
                FunctionExpression.of(QualifiedName.of("a", "b"), number(1), number(2)));
    }

    @Test
    public void testDataType() {
        Parser<DataType> parser = ExpressionParser.DATA_TYPE;
        TerminalParserTest.assertParser(parser, " int ", DataType.INT);
        TerminalParserTest.assertParser(parser, " double", DataType.DOUBLE);
        TerminalParserTest.assertParser(parser, " varchar", DataType.VARCHAR);
    }

    @Test
    public void testPrimaryKey() {
        Parser<Boolean> parser = ExpressionParser.PRIMARY_KEY;
        TerminalParserTest.assertParser(parser, "primary key", true);
        TerminalParserTest.assertParser(parser.optional(false), "", false);
    }

    @Test
    public void testAssignment() {
        Parser<Assignment> parser = ExpressionParser.assignment();
        TerminalParserTest.assertParser(parser, "a = 1",
                new Assignment(
                        QualifiedName.of("a"), number(1)));
        TerminalParserTest.assertParser(parser, "foo = 'abc'",
                new Assignment(
                        QualifiedName.of("foo"), new StringExpression("abc")));
        TerminalParserTest.assertParser(parser, "a2 = 1 + 2 - 3",
                new Assignment(
                        QualifiedName.of("a2"),
                        new BinaryExpression(
                                new BinaryExpression(number(1), Op.PLUS, number(2)),
                                Op.MINUS,
                                number(3)
                        )));
    }

    @Test
    public void testValue() {
        Parser<Expression> parser = ExpressionParser.VALUE;
        TerminalParserTest.assertParser(parser, "1", number(1));
        TerminalParserTest.assertParser(parser, "((1))", number(1));
        TerminalParserTest.assertParser(parser, "1 + 2", new BinaryExpression(number(1), Op.PLUS, number(2)));
        TerminalParserTest.assertParser(parser, "2 * (1 + (2))",
                new BinaryExpression(number(2), Op.MUL,
                        new BinaryExpression(number(1), Op.PLUS, number(2))));
        TerminalParserTest.assertParser(parser, "2 - 1 / (2)",
                new BinaryExpression(number(2), Op.MINUS,
                        new BinaryExpression(number(1), Op.DIV, number(2))));
        TerminalParserTest.assertParser(parser, "2 * 1 % -2",
                new BinaryExpression(
                        new BinaryExpression(number(2), Op.MUL, number(1)),
                        Op.MOD, new UnaryExpression(Op.NEG, number(2))));
        TerminalParserTest.assertParser(ExpressionParser.STRING, "'foo'", new StringExpression("foo"));
    }

    @Test
    public void testArithmetic() {
        Parser<Expression> parser = ExpressionParser.arithmetic(NUMBER);
        TerminalParserTest.assertParser(parser, "1", number(1));
        TerminalParserTest.assertParser(parser, "((1))", number(1));
        TerminalParserTest.assertParser(parser, "1 + 2", new BinaryExpression(number(1), Op.PLUS, number(2)));
        TerminalParserTest.assertParser(parser, "2 * (1 + (2))",
                new BinaryExpression(number(2), Op.MUL,
                        new BinaryExpression(number(1), Op.PLUS, number(2))));
        TerminalParserTest.assertParser(parser, "2 - 1 / (2)",
                new BinaryExpression(number(2), Op.MINUS,
                        new BinaryExpression(number(1), Op.DIV, number(2))));
        TerminalParserTest.assertParser(parser, "2 * 1 % -2",
                new BinaryExpression(
                        new BinaryExpression(number(2), Op.MUL, number(1)),
                        Op.MOD, new UnaryExpression(Op.NEG, number(2))));
        TerminalParserTest.assertParser(parser, "f(1)", FunctionExpression.of(QualifiedName.of("f"), number(1)));
        TerminalParserTest.assertParser(parser, "foo.bar(1, 2) + baz(foo.bar(1 / 2))",
                new BinaryExpression(
                        FunctionExpression.of(QualifiedName.of("foo", "bar"), number(1), number(2)),
                        Op.PLUS,
                        FunctionExpression.of(QualifiedName.of("baz"),
                                FunctionExpression.of(QualifiedName.of("foo", "bar"), new BinaryExpression(
                                        number(1), Op.DIV, number(2))))));
    }

    @Test
    public void testCompare() {
        Parser<Expression> parser = ExpressionParser.compare(NUMBER);
        TerminalParserTest.assertParser(parser, "1 = 1", new BinaryExpression(number(1), Op.EQ, number(1)));
        TerminalParserTest.assertParser(parser, "1 < 2", new BinaryExpression(number(1), Op.LT, number(2)));
        TerminalParserTest.assertParser(parser, "1 <= 2", new BinaryExpression(number(1), Op.LE, number(2)));
        TerminalParserTest.assertParser(parser, "1 <> 2", new BinaryExpression(number(1), Op.NE, number(2)));
        TerminalParserTest.assertParser(parser, "2 > 1", new BinaryExpression(number(2), Op.GT, number(1)));
        TerminalParserTest.assertParser(parser, "2 >= 1", new BinaryExpression(number(2), Op.GE, number(1)));
    }

    @Test
    public void testLogical() {
        Parser<Expression> parser = ExpressionParser.logical(NUMBER);
        TerminalParserTest.assertParser(parser, "1", number(1));
        TerminalParserTest.assertParser(parser, "(1)", number(1));
        TerminalParserTest.assertParser(parser, "((1))", number(1));
        TerminalParserTest.assertParser(parser, "not 1", new UnaryExpression(Op.NOT, number(1)));
        TerminalParserTest.assertParser(parser, "1 and 2", new BinaryExpression(number(1), Op.AND, number(2)));
        TerminalParserTest.assertParser(parser, "1 or 2", new BinaryExpression(number(1), Op.OR, number(2)));
        TerminalParserTest.assertParser(parser, "1 or 2 and 3", new BinaryExpression(number(1), Op.OR,
                new BinaryExpression(number(2), Op.AND, number(3))));
        TerminalParserTest.assertParser(parser, "1 or NOT 2", new BinaryExpression(number(1), Op.OR,
                new UnaryExpression(Op.NOT, number(2))));
        TerminalParserTest.assertParser(parser, "not 1 and 2", new BinaryExpression(
                new UnaryExpression(Op.NOT, number(1)), Op.AND, number(2)));
    }

    @Test
    public void testCondition() {
        Parser<Expression> parser =
                ExpressionParser.condition(Parsers.or(NUMBER, QUALIFIED_NAME));
        TerminalParserTest.assertParser(parser, "1 = 2", new BinaryExpression(number(1), Op.EQ, number(2)));

        TerminalParserTest.assertParser(parser, "(1 < 2 or not a > 1)",
                new BinaryExpression(
                        new BinaryExpression(number(1), Op.LT, number(2)),
                        Op.OR,
                        new UnaryExpression(Op.NOT,
                                new BinaryExpression(
                                        name("a"), Op.GT,
                                        number(1)
                                ))
                )
        );
    }

    static Expression number(int i) {
        return new NumberExpression(Integer.toString(i));
    }

    static Expression name(String... names) {
        return QualifiedNameExpression.of(names);
    }
}
