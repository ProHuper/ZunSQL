package npu.zunsql.ve;

/**
 * Created by Huper on 2017/10/30.
 */
public class ByteCode {

    public OpCode opCode;
    public String p1;
    public String p2;
    public String p3;

    public ByteCode(OpCode opCode, String p1, String p2, String p3){
        this.opCode = opCode;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}
