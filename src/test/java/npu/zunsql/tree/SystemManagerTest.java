package npu.zunsql.tree;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by WQT on 2017/11/16.
 */
public class SystemManagerTest {
    @Test
    public void loadDatabase1() throws Exception {
    }

    @Test
    public void addDatabase1() throws Exception {
    }

    @Test
    public void loadDatabase() throws Exception {
        SystemManager test = new SystemManager();
        Database testDB = test.addDatabase("test");
        Transaction testTran = testDB.beginWriteTrans();
        if (testDB.drop(testTran))
        {
            try {
                testTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            testTran.RollBack();
        }
        testDB =test.loadDatabase("test");
    }

    @Test
    public void addDatabase() throws Exception {
    }

}