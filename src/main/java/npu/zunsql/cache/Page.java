package npu.zunsql.cache;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Page implements Serializable
{
    public static final int HEAD_SIZE = 0;
    public static final int PAGE_SIZE = 1024;

    protected static int pageConut = 0;

    protected int pageID;
    protected ByteBuffer pageBuffer = null;

    //
    public Page(ByteBuffer buffer)
    {
        this.pageID = pageConut++;
        this.pageBuffer = buffer;
    }

    public Page(int pageID, ByteBuffer buffer)
    {
        this.pageID = pageID;
        this.pageBuffer = buffer;
    }

    //拷贝构造方法
    public Page(Page page)
    {
        this.pageID = page.pageID;
        ByteBuffer tempBuffer = ByteBuffer.allocate(page.pageBuffer.capacity());
        tempBuffer.put(page.pageBuffer);
        this.pageBuffer = tempBuffer;
    }

    public int getPageID()
    {
        return this.pageID;
    }

    public ByteBuffer getPageBuffer()
    {
        return this.pageBuffer;
    }
}
