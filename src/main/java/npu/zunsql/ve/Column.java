package npu.zunsql.ve;


class Column
{
	String ColumnName;
	String ColumnType;
	
	public Column(String pName,String pType)
	{
		ColumnName=pName;
		ColumnType=pType;
	}

	public Column(String pName)
	{
		ColumnName=pName;
	}

	@Override
	public boolean equals(Object obj) {
		Column other = (Column) obj;
		if(this.ColumnName.equals(other.ColumnName))
			return true;
		else return false;
	}
}