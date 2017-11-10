package npu.zunsql.cache;
import junit.framework.TestCase;

import java.nio.ByteBuffer;

public class PageTest extends TestCase {
    private static Page page;
    public void setUp() throws Exception {
        super.setUp();
        ByteBuffer buffer = ByteBuffer.allocate(Page.PAGE_SIZE);
        buffer.putInt(111);
        page = new Page(2, buffer);
    }

    public void tearDown() throws Exception {

    }

    public void testGetPageID() throws Exception {
        assertEquals(1,page.getPageID());
    }

    public void testGetPageBuffer() throws Exception {
        ByteBuffer tmp = page.getPageBuffer();
        tmp.flip();
        assertEquals(1111, tmp.getInt());
    }

}