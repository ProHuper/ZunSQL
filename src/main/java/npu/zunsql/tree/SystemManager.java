package npu.zunsql.tree;
import npu.zunsql.cache.Page;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by WQT on 2017/11/7.
 */
public class SystemManager
{
    // 用于管理其他数据库信息的数据库
    // 此数据库中包含一张master表
    // master表包含两列，数据库名为主键，以及pageNumber
    private Database masterDB;

    public SystemManager()
    {
        masterDB = new Database("masterDB");
        Column keyColumn = new Column(3,"name");
        Column valueColumn = new Column(1,"pageNumber");
        ArrayList<Column> columnList = new ArrayList<Column>();
        columnList.add(keyColumn);
        columnList.add(valueColumn);
        Transaction masterTran = masterDB.beginWriteTrans();
        Table masterTable = masterDB.createTable("DBmaster",keyColumn,columnList,masterTran);
        if(masterTable != null)
        {
            try {
                masterTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            masterTran.RollBack();
        }
    }

    public Database loadDatabase(String dBName)
    {
        // 读操作
        Transaction loadTran = masterDB.beginReadTrans();
        Table master = masterDB.getTable("master",loadTran);
        Cursor masterCursor = master.createCursor(loadTran);
        Column valueColumn = master.getColumn("pageNumber");
        int dBPageID = masterCursor.GetData(loadTran).getCell(valueColumn).getValue_Int();
        if (dBPageID >= 0)
        {
            try {
                loadTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            loadTran.RollBack();
        }
        return new Database(dBName,dBPageID);
    }

    public Database addDatabase(String dBName)
    {
        // 读操作
        Transaction readTran = masterDB.beginReadTrans();
        Table master = masterDB.getTable("master",readTran);
        Cursor masterCursor = master.createCursor(readTran);
        if (master != null)
        {
            try {
                readTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            readTran.RollBack();
        }

        // 写操作
        Transaction addTran = masterDB.beginWriteTrans();
        Column keyColumn = master.getKeyColumn();
        Column valueColumn = master.getColumn("pageNumber");
        Cell keyCell = new Cell(keyColumn,dBName);
        ByteBuffer tempBuffer = ByteBuffer.allocate(1024);
        // 新建一个Page用于存储新的DB
        Page newDBPage = new Page(tempBuffer);
        Cell valueCell = new Cell(valueColumn,newDBPage.getPageID());
        ArrayList<Cell> cList = new ArrayList<Cell>();
        cList.add(keyCell);
        cList.add(valueCell);
        Row thisRow = new Row(keyCell,cList);

        if (masterCursor.Insert(addTran,thisRow))
        {
            try {
                addTran.Commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            addTran.RollBack();

            // TODO：并且释放原本申请的Page空间。

        }
        return new Database(dBName,newDBPage);
    }
}
