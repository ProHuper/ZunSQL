package npu.zunsql.ve;

import npu.zunsql.tree.BasicType;
import org.junit.Test;


/**
 * Created by Huper on 2017/12/6.
 */
public class ExpressionTest {
    @Test
    void testExpression(){
        Expression expression = new Expression();
        expression.addOperand(new UnionOperand(BasicType.Float,"12.5"));
        expression.addOperand(new UnionOperand(BasicType.Float,"13.5"));
        expression.applyOperator(OpCode.Add);
        System.out.println(expression.getAns());

        expression.clearStack();
        expression.addOperand(new UnionOperand(BasicType.Float,"11"));
        expression.addOperand(new UnionOperand(BasicType.Float,"16"));
        expression.applyOperator(OpCode.Sub);
        System.out.println(expression.getAns());

        expression.clearStack();
        expression.addOperand(new UnionOperand(BasicType.Integer,"100"));
        expression.addOperand(new UnionOperand(BasicType.Integer,"25"));
        expression.applyOperator(OpCode.Div);
        System.out.println(expression.getAns());

        expression.clearStack();
        expression.addOperand(new UnionOperand(BasicType.Float,"25"));
        expression.addOperand(new UnionOperand(BasicType.Float,"3.6"));
        expression.applyOperator(OpCode.Mul);
        System.out.println(expression.getAns());

        expression.clearStack();
        expression.addOperand(new UnionOperand(BasicType.String,"123"));
        expression.addOperand(new UnionOperand(BasicType.String,"123"));
        expression.applyOperator(OpCode.EQ);
        System.out.println(expression.getAns());

        expression.clearStack();
        expression.addOperand(new UnionOperand(BasicType.Float,"12.5"));
        expression.addOperand(new UnionOperand(BasicType.Float,"13.5"));
        expression.applyOperator(OpCode.Add);
        System.out.println(expression.getAns());
    }

}
