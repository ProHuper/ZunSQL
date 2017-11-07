package npu.zunsql.ve;

class AttrInstance
{
	String type;
	String value;
	String attrName;

	AttrInstance(String pAttrName,String pType,String pValue)
	{
		value=pValue;
		type=pType;
		attrName=pAttrName;
	}
}