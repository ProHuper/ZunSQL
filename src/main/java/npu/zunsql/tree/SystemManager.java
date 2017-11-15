package npu.zunsql.tree;
import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by WQT on 2017/11/7.
 */
public class SystemManager
{
    // 用于管理其他数据库信息的数据库
    // 此数据库中包含一张master表
    // master表包含两列，数据库名为主键，以及pageNumber
    Database masterDB;

    public SystemManager()
    {
        masterDB = new Database("masterDB");
        Column keyColumn = new Column(3,"name");
        Column valueColumn = new Column(1,"pageNumber");
        List<Column> columnList = null;
        columnList.add(keyColumn);
        columnList.add(valueColumn);
        Transaction masterTran = masterDB.beginWriteTrans();
        Table masterTable = masterDB.createTable("master",keyColumn,columnList,masterTran);
        if(masterTable != null)
        {
            masterTran.Commit();
        }
        else
        {
            masterTran.RollBack();
        }
    }

    public Database loadDatabase(String dBName)
    {
        Table master = masterDB.getTable("master");
        Cursor masterCursor = master.createCursor();
        Column keyColumn = master.getKeyColumn();
        List<Column> columns = master.getColumns();
        Column valueColumn = master.getColumn("pageNumber");
        masterCursor.MovetoUnpacked(keyCell);
        Row data = masterCursor.GetData();
        Cell value = data.getCell(valueColumn);
        int dBPage = value.getValue_Int();
        Page thisPage = masterDB.cacheManager.getPageFromFile(dBPage);
        return new Database(thisPage);
    }

    public Database addDatabase(String dBName)
    {
        Table master = masterDB.getTable("master");
        Cursor masterCursor = master.createCursor();
        Column keyColumn = new Column(3,"name");
        Column valueColumn = new Column(1,"pageNumber");
        Cell keyCell = new Cell(keyColumn,dBName);
        masterCursor.MovetoUnpacked(keyCell);
        Row data = masterCursor.GetData();
        Cell value = data.getCell(valueColumn);
        int dBPage = value.getValue_Int();
        Page thisPage = masterDB.cacheManager.getPageFromFile(dBPage);
        return new Database(thisPage);
    }
}
