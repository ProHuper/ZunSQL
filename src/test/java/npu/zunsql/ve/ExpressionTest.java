package npu.zunsql.ve;

import npu.zunsql.tree.BasicType;
import org.junit.Test;


/**
 * Created by Huper on 2017/12/6.
 */
public class ExpressionTest {
    Expression expression = new Expression();
    UnionOperand a;
    UnionOperand b;
    @Test
    public void testExpression(){
        expression = new Expression();
        a = new UnionOperand(BasicType.Float, "12.5");
        b = new UnionOperand(BasicType.Float, "13.5");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Add);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());

        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Float, "16");
        b = new UnionOperand(BasicType.Float, "5");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Div);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "16");
        b = new UnionOperand(BasicType.Integer, "5");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Div);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Float, "20.5");
        b = new UnionOperand(BasicType.Float, "2");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Mul);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.String, "123456");
        b = new UnionOperand(BasicType.String, "123456");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.EQ);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.String, "123456");
        b = new UnionOperand(BasicType.String, "124567");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.LE);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.String, "124567");
        b = new UnionOperand(BasicType.String, "123456");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.GT);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.String, new String("123456"));
        b = new UnionOperand(BasicType.String, new String("123456"));
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.EQ);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "26");
        b = new UnionOperand(BasicType.Integer, "24");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.GT);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "125");
        b = new UnionOperand(BasicType.Integer, "125");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.NE);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "1");
        b = new UnionOperand(BasicType.Integer, "1");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.And);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        b = new UnionOperand(BasicType.Integer, "1");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.And);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        b = new UnionOperand(BasicType.Integer, "1");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Or);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        b = new UnionOperand(BasicType.Integer, "0");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Or);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        b = new UnionOperand(BasicType.Integer, "0");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Or);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        b = new UnionOperand(BasicType.Integer, "1");
        expression.addOperand(a);
        expression.addOperand(b);
        expression.applyOperator(OpCode.Or);
        System.out.println(a.getValue() + "   " + b.getValue() + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "0");
        expression.addOperand(a);
        expression.applyOperator(OpCode.Not);
        System.out.println(a.getValue()  + "   " + expression.getAns().getValue());


        expression = new Expression();
        expression.clearStack();
        a = new UnionOperand(BasicType.Integer, "1236");
        expression.addOperand(a);
        expression.applyOperator(OpCode.Neg);
        System.out.println(a.getValue() + "   " + expression.getAns().getValue());

    }

}
