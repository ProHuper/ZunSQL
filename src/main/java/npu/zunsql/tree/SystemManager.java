package npu.zunsql.tree;
import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by WQT on 2017/11/7.
 */
public class SystemManager
{
    // Mgr页放在第一页
    // 内容包括：database的对应页
    private static Page pageOne;

    // page层的Mgr，用于对Page层进行操作。
    private static CacheMgr cacheMgr;

    // 根据名称确定database页码
    private static Map<String, Integer> databaseList;

    public SystemManager()
    {
        pageOne = cacheMgr.getPageFromFile(0);
        ByteBuffer thisBufer = pageOne.getPageBuffer();

    }


    public int getDBPage(String name)
    {
        return  databaseList.get(name);
    }

    public static Database loadDatabase(String dBName)
    {
        int dBPage = databaseList.get(dBName);
        Page thisPage = cacheMgr.getPageFromFile(dBPage);
        return new Database(thisPage);
    }
    public static boolean addDatabase(String dBName)
    {
        return true;
    }
}
