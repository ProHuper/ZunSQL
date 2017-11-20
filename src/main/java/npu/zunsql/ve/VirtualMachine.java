package npu.zunsql.ve;

import npu.zunsql.tree.*;

import java.io.IOException;
import java.util.*;

public class VirtualMachine
{
	//作为过滤器来对记录进行筛选
	private List<EvalDiscription> filters;
	//存储被选出的列
    private List<String> selectedColumns;
    //存储要插入的记录
    private List<AttrInstance> record;
    //存储要创建表的各项表头，该数据结构仅用于创建表
    private List<Column> columns;
    //存储execute指令执行后的查询结构，仅select指令对应的操作会使得该集合非空
    private QueryResult result;
    //要操作的对象表名
    private String targetTable;
    //创建表时主键的名称存储在该变量中
    private String pkName;
    //要更新的属性名称，顺序必须与下一个变量的顺序一致
    private List<String> updateAttrs;
    //要更新的属性值，顺序必须与上一个变量的顺序一致
    private List<List<EvalDiscription> > updateValues;
    //临时变量
    private List<EvalDiscription> singleUpdateValue;
    //记录本次execute将执行的命令
    private Activity activity;
    //作为join操作的结果集
    private QueryResult joinResult;
    //事务句柄
    private Transaction tran;
    //等待连接的表名
    private List<String> waitingJoin;

    private boolean suvReadOnly;
	private boolean recordReadOnly;
	private boolean columnsReadOnly;
	private boolean selectedColumnsReadOnly;
    private Database db;

	public VirtualMachine(Database pdb)
	{
		recordReadOnly=true;
		columnsReadOnly=true;
		selectedColumnsReadOnly=true;
		suvReadOnly=true;

		tran=null;
        result=null;
		activity=null;
		targetTable=null;
		joinResult=null;

        waitingJoin=new ArrayList<>();
		filters=new ArrayList<>();
		selectedColumns=new ArrayList<>();
		record=new ArrayList<>();
		columns=new ArrayList<>();
		updateAttrs =new ArrayList<>();
		updateValues =new ArrayList<>();
		singleUpdateValue=new ArrayList<>();

		pkName=null;
        db=pdb;
	}

    public QueryResult run(ByteCode instruction) throws IOException
    {
        OpCode opCode = instruction.opCode;
        String p1 = instruction.p1;
        String p2 = instruction.p2;
        String p3 = instruction.p3;

        //所有操作都改为延时操作，即在execute后生效，其他命令只会向VM中填充信息
        //特例是commit指令和rollback指令会立即执行
        switch (opCode)
        {
            //下面是关于事务的处理代码
            case Transaction:
                //如果这里不能提供Transaction的类型，那么只能在execute的时候由虚拟机来自动推断
                //这里不做任何处理，因为上一层并没有交给本层事务类型
                break;

            case Commit:
                try {
                    tran.Commit();
                }
                catch (IOException e){
                    Util.log("提交失败");
                    throw e;
                }
                break;

            case Rollback:
                tran.RollBack();
                break;

            //下面是创建表的处理代码
            case CreateTable:
                activity=Activity.CreateTable;
                columnsReadOnly = false;
                targetTable=p3;
                break;

            case AddCol:
                columns.add(new Column(p1, p2));
                break;

            case BeginPK:
                //在只支持一个属性作为主键的条件下，此操作本无意义
                //但指定主键意味着属性信息输入完毕，因此将columnsReadOnly置为true
                columnsReadOnly = true;
                break;

            case AddPK:
                //在只支持一个属性作为主键的条件下，直接对pkName赋值即可
                pkName = p1;
                break;

            case EndPK:
                //在只支持一个属性作为主键的条件下，此操作无意义
                //暂时将此命令作为createTable结束的标志
                break;

            //下面是删除表的操作
            case DropTable:
                activity=Activity.DropTable;
                targetTable=p3;
                break;

            //下面是插入操作，这是个延时操作
            case Insert:
                activity = Activity.Insert;
                targetTable=p3;
                break;

            //下面是删除操作，这是个延时操作
            case Delete:
                activity = Activity.Delete;
                targetTable = p3;
                break;

            //下面是选择操作，这是个延时操作
            case Select:
                activity = Activity.Select;
                targetTable = p3;
                break;

            //下面是更新操作，这是个延时操作
            case Update:
                activity = Activity.Update;
                targetTable = p3;
                break;

            //下面是关于插入一条记录的内容的操作
            case BeginItem:
                recordReadOnly = false;
                break;

            case AddItemCol:
                record.add(new AttrInstance(p1, p2, p3));

            case EndItem:
                recordReadOnly = true;
                break;

            //关于选择器的选项，这里借助表达式实现，仅在最后将记录的表达式传给filters
            case BeginFilter:
                suvReadOnly = false;
                singleUpdateValue=new ArrayList<>();
                break;

            case EndFilter:
                filters=singleUpdateValue;
                suvReadOnly = true;
                break;

            //下面是关于select选择的属性的设置
            case BeginColSelect:
                selectedColumnsReadOnly = false;
                break;

            case AddColSelect:
                selectedColumns.add(p1);
                break;

            case EndColSelect:
                selectedColumnsReadOnly = true;
                break;

            //下面是处理选择的表的连接操作的代码
            case BeginJoin:
                //接收到join命令，清空临时表
                joinResult = null;
                break;

            case AddTable:
                targetTable = p1;
                //调用下层方法，加载p1表，将自然连接的结果存入joinResult
                join(targetTable);
                break;

            case EndJoin:
                break;

            //下面的代码设置update要更新的值，形式为colName=Expression
            case Set:
                updateAttrs.add(p1);
                break;

            case BeginExpression:
                suvReadOnly=false;
                singleUpdateValue=new ArrayList<>();
                break;

            case EndExpression:
                updateValues.add(singleUpdateValue);
                suvReadOnly=true;
                break;

            //记录Expression描述的代码
            case Operand:
                singleUpdateValue.add(new EvalDiscription(opCode,p1,p2));
                break;

            case Operator:
                singleUpdateValue.add(new EvalDiscription(OpCode.valueOf(p1),null,null));
                break;

            case Execute:
                execute();
                break;

            default:
                Util.log("没有这样的字节码: " + opCode + " " + p1 + " " + p2 + " " + p3);
                break;

        }
        return result;
    }

    private boolean execute() {
        if(targetTable==null) {
            Util.log("没有指定要操作的表!");
        }
        else {
            switch (activity){
                case Select:
                    select();
                    break;
                case Delete:
                    delete();
                    break;
                case Update:
                    update();
                    break;
                case Insert:
                    insert();
                    break;
                case CreateTable:
                    createTable();
                    break;
                case DropTable:
                    dropTable();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private QueryResult dropTable(){
        tran=db.beginWriteTrans();
        if(db.getTable(targetTable,tran).drop(tran)==false){
            Util.log("删除表失败");
            return new QueryResult(false);
        }
        else{
            return new QueryResult(true);
        }
    }
    private QueryResult createTable(){
	    //需要开启一个写事务
	    tran=db.beginWriteTrans();

	    //检索主键
        int pkType=-1;
        for(Column x:columns){
            if(x.getColumnName()==pkName){
                switch (x.getColumnType()){
                    case "String":
                        pkType= npu.zunsql.tree.Column.CT_STRING;
                        break;
                    case "Integer":
                        pkType= npu.zunsql.tree.Column.CT_INT;
                        break;
                    case "Float":
                        pkType= npu.zunsql.tree.Column.CT_DOUBLE;
                        break;
                }
            }
        }
        if(pkType==-1){
            Util.log("虚拟机未找到创建表的主键");
        }

        //将ve的Column重构为tree的Column
        List<npu.zunsql.tree.Column> tColumns=new ArrayList<>();
        for(Column item:columns){
            int type= npu.zunsql.tree.Column.CT_STRING;
            if(item.getColumnType()=="Integer"){
                type= npu.zunsql.tree.Column.CT_INT;
            }
            else if(item.getColumnType()=="Float"){
                type= npu.zunsql.tree.Column.CT_DOUBLE;
            }
            tColumns.add(new npu.zunsql.tree.Column(type,item.getColumnName()));
        }

        if(null!=db.createTable(targetTable,new npu.zunsql.tree.Column(pkType,pkName),tColumns,tran)){
           return new QueryResult(true);
        }
        else{
            return new QueryResult(false);
        }

    }
    /**
     *检查当前记录是否满足where子句的条件
     * @param p 当前表上的指针
     * @return 满足条件返回true，否则返回false
     */
    private boolean check(Cursor p) {
	    //如果没有where子句，那么返回true，即对所有记录都执行操作
	    if(filters.size()==0){
	        return true;
        }
        UnionOperand ans=eval(filters,p);
	    if(ans.getType()==BasicType.String){
	        Util.log("where子句的表达式返回值不能为String");
	        //返回false,此返回值没有意义
            return false;
        }
        else if(Math.abs(Double.valueOf(ans.getValue()))<1e-10){
	        return false;
        }
        else{
            return true;
        }
    }
    private void select(){
        tran=db.beginReadTrans();
	    //构造结果集的表头
	    List<Column> selected=new ArrayList<Column>();
	    Table t=db.getTable(targetTable,tran);
	    for(String colName:selectedColumns){
	        Column col=new Column(t.getColumn(colName).getName(),t.getColumn(colName).getType());
            selected.add(col);
        }
        result=new QueryResult(selected);

        Cursor p=t.createCursor(tran);
        while(p!=null){
            if(check(p)==true){
                List<String> ansRecord=new ArrayList<String>();
                for(String colName:selectedColumns){
                    ansRecord.add(p.GetData(tran).getCell(colName).getValue_String());
                }
                result.addRecord(ansRecord);
            }
            p.MovetoNext(tran);
        }
    }
    private void delete(){
        tran=db.beginWriteTrans();
        if(filters.size()==0){
            db.getTable(targetTable,tran).clear(tran);
        }
        else{
            Cursor p=db.getTable(targetTable,tran).createCursor(tran);
            while(p!=null){
                if(check(p)){
                    p.Delete(tran);
                }
                else{
                    p.MovetoNext(tran);
                }
            }
        }
    }

    /**
     * 对全表进行更新
     */
    private void update(){
        Cursor p=db.getTable(targetTable,tran).createCursor(tran);
        while(p!=null){
            if(check(p)){
                Row record=p.GetData(tran);
                for(int i = 0; i< updateAttrs.size(); i++){

                    //查询要更新的属性的信息并创建cell对象来执行更新
                    String name=record.getCell(updateAttrs.get(i)).getColumn().getName();
                    String type=record.getCell(updateAttrs.get(i)).getColumn().getType();
                    if(type=="String"){
                        record.ChangeCell(new Cell(new npu.zunsql.tree.Column(3,name),eval(updateValues.get(i),p).
                                getValue()));
                    }
                    else if(type=="Integer"){
                        record.ChangeCell(new Cell(new npu.zunsql.tree.Column(1,name),Integer.valueOf(
                                eval(updateValues.get(i),p).getValue())));
                    }
                    else{
                        record.ChangeCell(new Cell(new npu.zunsql.tree.Column(2,name),Double.valueOf(
                                eval(updateValues.get(i),p).getValue())));
                    }
                }

            }
            p.MovetoNext(tran);
        }
    }

    /**
     * 将一条记录插入到表中
     * 因为上层没有产生default，下层也未提供接口，因此这里每次只能插入一条完整的记录
     */
    private void insert(){
        List<String> colNames=new ArrayList<>();
        List<String> colValues=new ArrayList<>();

        for(AttrInstance item:record){
            colNames.add(item.getAttrName());
            colValues.add(item.getValue());
        }

        db.getTable(targetTable,tran).insert(colNames,colValues);
    }

    /**
     * 确定一个字符串值的最小可承载类型
     * @param strVal 要判断的值
     * @return 最小的可承载类型
     */
    private static BasicType lowestType(String strVal){
	    int dot=0;
	    boolean alpha=false;
	    for(int i=0;i<strVal.length();i++){
	        char c=strVal.charAt(i);
            if(c=='.'){
                dot++;
            }
            else if(c>'9'||c<'0'){
                alpha=true;
                break;
            }
        }
        if(alpha==true||dot>=2){
	        return BasicType.String;
        }
        else if(dot==1){
            return BasicType.Float;
        }
        else{
            return BasicType.Integer;
        }
    }

    /**
     *根据表达式的描述求值
     * @param evalDiscriptions 要计算的表达式描述
     * @param p 计算时需要依赖的数据的指针
     */
    private  UnionOperand eval(List<EvalDiscription> evalDiscriptions,Cursor p){
        Expression exp=new Expression();
        for(int i=0;i<evalDiscriptions.size();i++) {
            if(evalDiscriptions.get(i).cmd==OpCode.Operand){
                if(evalDiscriptions.get(i).col_name!=null){
                    Cell c=p.GetData(tran).getCell(evalDiscriptions.get(i).col_name);
                    switch(c.getColumn().getType()){
                        case "String":
                            exp.addOperand(new UnionOperand(BasicType.String,c.getValue_String()));
                            break;
                        case "Float":
                            exp.addOperand(new UnionOperand(BasicType.Float,c.getValue_Double().toString()));
                            break;
                        case "Integer":
                            exp.addOperand(new UnionOperand(BasicType.Integer,c.getValue_Int().toString()));
                            break;
                        default:
                            Util.log("不存在的类型");
                    }
                }
                else{
                    String val=evalDiscriptions.get(i).constant;
                    BasicType cType= lowestType(val);
                    exp.addOperand(new UnionOperand(cType,val));
                }
            }
            else{
                exp.applyOperator(evalDiscriptions.get(i).cmd);
            }
        }
        return exp.getAns();
    }

    private void join(String tableName)
    {
        Table table = db.getTable(tableName,tran);
        List<List<String>> resList = joinResult.getRes();
        List<Column> resHead = joinResult.getHeader();
        List<Column> fromTreeHead = new ArrayList<>();
        table.getColumns().forEach(n -> fromTreeHead.add(new Column(n.getColumnName())));

        Cursor cursor = new Cursor(table,tran);
        JoinMatch matchedJoin = checkUnion(resHead, fromTreeHead);
        QueryResult copy = new QueryResult(matchedJoin.getJoinHead());

        for(int i = 0; i < resList.size(); i++){
            List<String> tempRes = resList.get(i);
            while(cursor != null)
            {
                Row row = cursor.GetData(tran);
                List<Cell> fromTreeCell = row.getCellList();
                List<String> fromTreeString = new ArrayList<>();
                for(Cell n: fromTreeCell){
                    fromTreeString.add(n.getColumn().getColumnName());
                }
                List<String> copyTreeString = new ArrayList<>();
                fromTreeString.forEach(n -> copyTreeString.add(n));

                Iterator iterator = matchedJoin.getJoinUnder().keySet().iterator();
                while(iterator.hasNext()){
                    int nextKey = (Integer) iterator.next();
                    int nextValue = matchedJoin.getJoinUnder().get(nextKey);
                    String s1 = tempRes.get(nextKey);
                    String s2 = fromTreeString.get(nextValue);
                    if( !s1.equals(s2) ){
                        break;
                    }
                    else{
                        copyTreeString.remove(nextValue);
                    }
                }

                if(iterator.hasNext()){
                    List<String> line = new ArrayList<>();
                    tempRes.forEach(n -> line.add(n));
                    copyTreeString.forEach(n -> line.add(n));
                    copy.getRes().add(line);
                }

                cursor.MovetoNext(tran);
            }
        }
        joinResult = copy;
    }

    public JoinMatch checkUnion(List<Column> head1, List<Column> head2){
        List<Column> unionHead = new ArrayList<>();
        Map<Integer,Integer> unionUnder = new HashMap<>();

        head1.forEach(n -> unionHead.add(n));

        for(Column n : head2){
            if(!head1.contains(n)){
                unionHead.add(n);
            }
        }

        for(int i = 0; i < head1.size(); i++){
            int locate = head2.indexOf(head1.get(i));
            if(locate != -1){
                unionUnder.put(i,locate);
            }
        }

        return new JoinMatch(unionHead, unionUnder);
    }


    //这个方法只用于测试自然连接操作。
    public QueryResult forTestJoin(JoinMatch joinMatch, QueryResult input1, QueryResult input2){
        int matchCount = 0;
        QueryResult copy = new QueryResult(joinMatch.getJoinHead());
        List<List<String>> resList = input1.getRes();
        for(int i = 0; i < resList.size(); i++){
            List<String> tempRes = resList.get(i);
            for(List<String> fromTreeString: input2.getRes())
            {
                List<String> copyTreeString = new ArrayList<>();
                fromTreeString.forEach(n -> copyTreeString.add(n));
                Iterator iterator = joinMatch.getJoinUnder().keySet().iterator();
                matchCount = 0;

                while(iterator.hasNext()){
                    int nextKey = (Integer) iterator.next();
                    int nextValue = joinMatch.getJoinUnder().get(nextKey);
                    String s1 = tempRes.get(nextKey);
                    String s2 = fromTreeString.get(nextValue);
                    if( !s1.equals(s2) ){
                        break;
                    }
                    else{
                        matchCount++;
                        copyTreeString.remove(nextValue);
                    }
                }

                if(matchCount == joinMatch.getJoinUnder().size()){
                    List<String> line = new ArrayList<>();
                    tempRes.forEach(n -> line.add(n));
                    copyTreeString.forEach(n -> line.add(n));
                    copy.getRes().add(line);
                }
            }
        }
        return copy;
    }

    private Transaction beginTran(){
        if(tran==null){
            tran=beginTran();
        }
        return null;
    }
}