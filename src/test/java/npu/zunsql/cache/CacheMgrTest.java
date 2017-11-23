import junit.framework.TestCase;
import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.io.File;
import java.nio.ByteBuffer;

public class CacheMgrTest extends TestCase {

     private CacheMgr cacheMgr;
     private  int transID;
     private  String dbname ="zmx";


    public void setUp() throws Exception {
        super.setUp();
        cacheMgr = new CacheMgr(dbname);
    }
    public void tearDown() throws Exception {

    }

    public void testBeginTransation() throws Exception {

         //读事务
         transID = new cacheMgr.beginTransation("r");
         assertEquals(false,cacheMgr.lock.readLock().tryLock());
         cacheMgr.lock.readLock().unlock();
         //写事务
		 transID = new cacheMgr.beginTransation("w");
		 assertEquals(false,cacheMgr.lock.writeLock().tryLock());
    }

    public void testCommitTransation() throws Exception {







    }

    public void testRollbackTransation() throws Exception {


         //读事务
        transID = new cacheMgr.beginTransation("r");
        assertEquals(false,cacheMgr.transMgr.containsKey(transID));
		//写事务
		transID = new cacheMgr.beginTransation("w");
        assertEquals(false,cacheMgr.transMgr.containsKey(transID));


    }

    public void testReadPage() throws Exception {

        Page page=null;
        //读事务
        transID = new cacheMgr.beginTransation("r");
        page =cacheMgr.readPage(transID,1);
        assertEquals(false,page==null);
        page = cacheMgr.readPage(transID,1);
        assertEquals(false,page==null);

    }




    public void testWritePage() throws Exception {


        transID = new cacheMgr.beginTransation("w");
        Page tempbuffer=cacheMgr.readPage(1);
        cacheMgr.writePage(tempbuffer);
        assertEquals(true,cacheMgr.transOnPage.get(transID).contains(tempbuffer));
    }

    public void testSetPageToFile() throws Exception {

        transID = new cacheMgr.beginTransation("w");
        Page tempbuffer=cacheMgr.readPage(1);
        File db_file = new File(dbname);
       assertEquals(true,cacheMgr.setPageToFile(tempbuffer,db_file));


    }
    public void testGetPageFromFile() throws Exception {

        transID = new cacheMgr.beginTransation("r");
        Page page = cacheMgr.getPageFromFile(1);
        assertEquals(false,page==null);

    }

}