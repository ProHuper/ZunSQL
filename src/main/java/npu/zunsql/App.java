package npu.zunsql;
import npu.zunsql.DBInstance;
import npu.zunsql.ve.QueryResult;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args)
    {
        DBInstance dbinstance = DBInstance.Open("test.db");
        QueryResult result = dbinstance.Execute("create table student(int stuno primary key, varchar name, double score)");


        result = dbinstance.Execute("Insert into student values(2017001, \"zhang\", 98.0)");
        System.out.println("Insert Row:" + result.getAffectedCount());
        dbinstance.Close();
    }
}
