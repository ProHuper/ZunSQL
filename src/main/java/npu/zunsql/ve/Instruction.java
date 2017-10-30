package npu.zunsql.ve;

import npu.zunsql.common.FormatObject;

public class Instruction extends FormatObject {
    public OpCode opcode;
    public String p1, p2, p3;

    public Instruction(OpCode opcode, String p1, String p2, String p3) {
        this.opcode = opcode;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}
