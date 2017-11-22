package npu.zunsql.tree;

import java.util.List;

/**
 * Created by Ed on 2017/10/28.
 */
public interface TableReader
{
    public abstract List<String> getColumns();

    public abstract Cursor createCursor(Transaction thistran);
}


