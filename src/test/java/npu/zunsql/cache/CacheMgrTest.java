import junit.framework.TestCase;
import npu.zunsql.cache.CacheMgr;

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






    }

    public void testCommitTransation() throws Exception {
    }

    public void testRollbackTransation() throws Exception {
    }

    public void testReadPage() throws Exception {
    }

    public void testWritePage() throws Exception {
    }

    public void testSetPageToFile() throws Exception {
    }

    public void testGetPageFromFile() throws Exception {
    }

}