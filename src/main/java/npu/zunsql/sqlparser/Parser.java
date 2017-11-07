package npu.zunsql.sqlparser;

import npu.zunsql.sqlparser.parser.TerminalParser;
import npu.zunsql.sqlparser.ast.Relation;
import npu.zunsql.sqlparser.parser.RelationParser;

public class Parser {
    public static final Relation parse(String stmt) {
        return TerminalParser.parse(RelationParser.Sql(), stmt);
    }
}
