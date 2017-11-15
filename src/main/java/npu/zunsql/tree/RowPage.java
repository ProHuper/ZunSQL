package npu.zunsql.tree;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;

/**
 * Created by WQT on 2017/11/15.
 */
public class RowPage extends Page
{
    public RowPage(Page thisPage)
    {
        super(thisPage.getPageID(),thisPage.getPageBuffer());
    }
}
