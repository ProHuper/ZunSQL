package npu.zunsql;
import npu.zunsql.DBInstance;
import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;
import npu.zunsql.cache.Transaction;
import npu.zunsql.ve.QueryResult;
import sun.misc.Cache;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args)
    {
        /* test for readPage and writePage in cacheMgr, when a transation commit
        CacheMgr cacheManager = new CacheMgr("student");
        int tranID  = cacheManager.beginTransation("w");

        ByteBuffer tempBuffer = ByteBuffer.allocate(Page.PAGE_SIZE);
        tempBuffer.putInt(0,987);
        Page tempPage = new Page(tempBuffer);
        cacheManager.writePage(tranID, tempPage);
        try
        {
            cacheManager.commitTransation(tranID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Page rPage = cacheManager.readPage(2, tempPage.getPageID());
        int ret = rPage.getPageBuffer().getInt(0);
        System.out.println(ret);

        */

        DBInstance dbinstance = DBInstance.Open("test.db");
        QueryResult result = dbinstance.Execute("create table student(int stuno primary key, varchar name, double score)");


        result = dbinstance.Execute("Insert into student values(2017001, \"zhang\", 98.0)");
        System.out.println("Insert Row:" + result.getAffectedCount());
        dbinstance.Close();
    }
}
