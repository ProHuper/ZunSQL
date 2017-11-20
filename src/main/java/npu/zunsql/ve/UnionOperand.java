package npu.zunsql.ve;

public class UnionOperand {
    private String value;
    private BasicType type;
    public UnionOperand(BasicType pType,String pValue){
        value=pValue;
        type=pType;
    }
    public BasicType getType() {
        return type;
    }
    public String getValue(){
        return value;
    }
}
