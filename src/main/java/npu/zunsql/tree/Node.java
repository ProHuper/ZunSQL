package npu.zunsql.tree;

import npu.zunsql.cache.CacheMgr;
import npu.zunsql.cache.Page;

import java.util.List;
/**
 * Created by WQT on 2017/11/6.
 */

// 本Node类用于组织B树内部结构
// 每个Node包含一个Row列表和一个SonNode列表，其中SonNode列表除初始化时外，始终比Row列表多一个。
public class Node {
    // 用于表示本树为几阶B树。
    public final static int M = 3;

    CacheMgr cacheManager;

    // 每个节点包含不少于M/2+1，不超过M+1的SonNode。
    private List<Integer> sonNodeList;

    // 表示父亲节点
    private int fatherNode;

    // 表示本节点在父亲节点儿子中的第几位。
    private int order;

    // 每个节点包含不少于M/2，不超过M的Row。
    private List<Row> rowList;

    // 每个Node表示一个Page
    private Page thisPage;

    protected Node(int thisPageID, CacheMgr cacheManager, Transaction thisTran)
    {
        this.cacheManager = cacheManager;
        thisPage = this.cacheManager.readPage(thisTran.tranNum, thisPageID);
        // TODO:根据thisPage加载本Node信息
    }

    // 根据Node的属性构造Node。
    private Node(List<Row> thisRowList, List<Integer> thisSonList, int thisOrder, CacheMgr cacheManager, Transaction thisTran)
    {
        // TODO:开辟一个Page来存放本Node

        rowList = thisRowList;
        sonNodeList = thisSonList;
        fatherNode = -1;

        // 为每一位儿子维护父亲和排位信息。
        for (int i = 0; i < sonNodeList.size(); i++)
        {
            Node sonNode = new Node(sonNodeList.get(i),cacheManager,thisTran);
            sonNode.setFather(thisPage.getPageID(),thisTran);
            sonNode.setOrder(i,thisTran);
        }

        // 维护自身排位信息。
        order = thisOrder;
    }

    private boolean setFather(int ID,Transaction thisTran)
    {
        fatherNode = ID;

        // TODO：维护page信息

        return true;
    }


    private boolean setOrder(int or, Transaction thisTran)
    {
        order = or;

        // TODO：维护page信息

        return true;
    }

    // 分裂除根节点外的其他节点。
    private Node devideNode(Transaction thisTran)
    {
        List<Row> rightRow;
        List<Integer> rightNode;
        rightRow = rowList.subList(M/2 + 1, M);
        rowList = rowList.subList(0, M/2);
        rightNode = sonNodeList.subList(M/2 + 1,M + 1);
        sonNodeList = sonNodeList.subList(0, M/2 + 1);
        //TODO:保存本Node信息
        return new Node(rightRow, rightNode, order + 1, cacheManager, thisTran);
    }

    // 分裂根节点
    private boolean rootDevideNode(Transaction thisTran)
    {
        List<Row> leftRow;
        List<Row> rightRow;
        List<Integer> leftNode;
        List<Integer> rightNode;
        leftRow = rowList.subList(0, M/2);
        leftNode = sonNodeList.subList(0, M/2 + 1);
        rightRow = rowList.subList(M/2 + 1, M);
        rightNode = sonNodeList.subList(M/2 + 1, M + 1);
        rowList = rowList.subList(M/2, M/2 + 1);
        List<Integer> newSonList = null;
        newSonList.add(new Node(leftRow, leftNode, 0, cacheManager, thisTran).thisPage.getPageID());
        newSonList.add(new Node(rightRow, rightNode,1, cacheManager, thisTran).thisPage.getPageID());
        sonNodeList = newSonList;
        //TODO:保存本Node信息
        return true;
    }

    // 调整本节点使其顺序为sonOrder的儿子row数量恢复至M/2
    private boolean adjustNode(int sonOrder, Transaction thisTran)
    {
        Node thisSonNode = new Node(sonNodeList.get(sonOrder),cacheManager,thisTran);

        // 排除最大值边界越界情况，向左下合并
        if (sonOrder < sonNodeList.size() - 1)
        {
            Node rightSonNode = new Node(sonNodeList.get(sonOrder + 1), cacheManager, thisTran);
            if (rightSonNode.rowList.size() > M/2)
            {
                thisSonNode.insertRow(rowList.get(sonOrder),thisTran);
                rightSonNode.deleteRow(rowList.get(order).getCell(0),thisTran);
                rowList.set(sonOrder, rightSonNode.getFirstRow());
                //TODO:保存本Node信息
                return true;
            }

        }

        // 排除零值边界越界情况，向右下合并
        if (sonOrder > 0)
        {
            Node leftSonNode = new Node(sonNodeList.get(sonOrder - 1),cacheManager,thisTran);
            if (leftSonNode.rowList.size() > M/2)
            {
                thisSonNode.insertRow(rowList.get(sonOrder - 1));
                leftSonNode.deleteRow(rowList.get(order).getCell(0));
                rowList.set(sonOrder - 1, leftSonNode.getLastRow());
                //TODO:保存本Node信息
                return true;
            }
        }

        // 没有相邻的可支援兄弟节点，只好删除此节点。
        return deleteNode(sonOrder,thisTran);
    }

    // 在本节点中添加子节点，分别添加row和对应的SonNode。
    private boolean addNode(Row row, Node node, Transaction thisTran)
    {
        // 用于记录是否添加了这个节点。
        boolean addOrNot = false;
        for (int i = 0; i < rowList.size(); i++)
        {
            Row thisRow = rowList.get(i);
            Node thisNode = new Node(sonNodeList.get(i),cacheManager,thisTran);
            if (!addOrNot && thisRow.getCell(0).bigerThan(row.getCell(0)))
            {
                rowList.add(i, row);
                sonNodeList.add(i, node.thisPage.getPageID());
                thisNode.order = i;
                thisNode.fatherNode = thisPage.getPageID();
                addOrNot = true;
            }
            thisNode.setOrder(i,thisTran);
        }
        // 如果之前都没有添加这个节点，那么此时添加至末尾。
        if (!addOrNot)
        {
            if (rowList.size() == 0)
            {
                row.setLeftRow(null);
            }
            else
            {
                row.setLeftRow(new Row(rowList.get(rowList.size() - 1)));
                new Row(rowList.get(rowList.size() - 1)).setRightRow(row);
            }
            row.setRightRow(null);
            rowList.add(row.pageID);
            sonNodeList.add(sonNodeList.size() - 2, node.thisPage);
        }

        // 当未超出长度时，插入完毕。
        if (rowList.size() <= M)
        {
            return true;
        }
        // 超出长度时，进行单元分裂。
        else
        {
            if (fatherNode < 0)
            {
                return rootDevideNode();
            }
            else
            {
                return new Node(fatherNode).addNode(new Row(rowList.get(M/2)),devideNode());
            }
        }
    }

    private boolean deleteNode(int sonOrder)
    {
        Row thisRow;
        if (sonOrder < sonNodeList.size() - 1)
        {
            thisRow = new Row(rowList.get(sonOrder));
            Node rightNode = new Node(sonNodeList.get(sonOrder + 1));
            rightNode.insertRow(thisRow);
            if (sonOrder < rowList.size() - 1)
            {
                thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

            }
            if (sonOrder > 0)
            {
                thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
            }
            rowList.remove(sonOrder);
            sonNodeList.remove(sonOrder);
            for (int i = sonOrder; i < sonNodeList.size(); i++)
            {
                new Node(sonNodeList.get(i)).order = i;
            }
            if (rowList.size() < M/2)
            {
                if (fatherNode < 0)
                {
                    if (rowList.size() < 1)
                    {
                        rowList = new Node(sonNodeList.get(0)).rowList;
                        sonNodeList = new Node(sonNodeList.get(0)).sonNodeList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return new Node(fatherNode).adjustNode(order);
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            thisRow = new Row(rowList.get(sonOrder - 1));
            Node leftNode = new Node(sonNodeList.get(sonOrder - 1));
            leftNode.insertRow(thisRow);
            if (sonOrder < rowList.size() - 1)
            {
                thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

            }
            if (sonOrder > 0)
            {
                thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
            }
            rowList.remove(sonOrder - 1);
            sonNodeList.remove(sonOrder);
            for (int i = sonOrder; i < sonNodeList.size(); i++)
            {
                new Node(sonNodeList.get(i)).order = i;
            }
            if (rowList.size() < M/2)
            {
                if (fatherNode < 0)
                {
                    if (rowList.size() < 1)
                    {
                        rowList = new Node(sonNodeList.get(0)).rowList;
                        sonNodeList = new Node(sonNodeList.get(0)).sonNodeList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return new Node(fatherNode).adjustNode(order);
                }
            }
            else
            {
                return true;
            }
        }
    }

    public boolean insertRow(int rowID)
    {
        return insertRow(new Row(rowID));
    }

    public boolean insertRow(Row row)
    {
        boolean insertOrNot = false;
        int insertNumber = 0;
        for (int i = 0; i < rowList.size(); i++)
        {
            if (new Row(rowList.get(i)).getKeyCell().equalTo(row.getKeyCell()))
            {
                return false;
            }
            else if (new Row(rowList.get(i)).getKeyCell().bigerThan(row.getKeyCell()))
            {
                insertNumber = i;
                insertOrNot = true;
                break;
            }
        }
        if (!insertOrNot)
        {
            insertNumber = rowList.size();
        }
        if (sonNodeList == null)
        {
            rowList.add(insertNumber,row.pageID);
            if (rowList.size() <= M)
            {
                return true;
            }
            else
            {
                if (fatherNode < 0)
                {
                    return rootDevideNode();
                }
                else
                {
                    return new Node(fatherNode).addNode(new Row(rowList.get(M/2)),devideNode());
                }
            }
        }
        else
        {
            return new Node(sonNodeList.get(insertNumber)).insertRow(row);
        }
    }

    public boolean deleteRow(Cell key)
    {
        boolean deleteOrNot = false;
        int deleteNumber = 0;
        for (int i = 0; i < rowList.size(); i++)
        {
            Row thisRow = new Row(rowList.get(i));
            if (thisRow.getKeyCell().equalTo(key))
            {
                if (sonNodeList == null)
                {
                    if (i < rowList.size() - 1)
                    {
                        thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

                    }
                    if (i > 0)
                    {
                        thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
                    }
                    rowList.remove(i);
                    if (rowList.size() < M/2)
                    {
                        if (fatherNode < 0)
                        {
                            if (rowList.size() < 1)
                            {
                                rowList = new Node(sonNodeList.get(0)).rowList;
                                sonNodeList = new Node(sonNodeList.get(0)).sonNodeList;
                                return true;
                            }
                            else
                            {
                                return true;
                            }
                        }
                        else
                        {
                            return new Node(fatherNode).adjustNode(order);
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    rowList.set(i, thisRow.getRightRow().pageID);
                    return new Node(sonNodeList.get(i + 1)).deleteRow(thisRow.getRightRow().getKeyCell());
                }
            }
            else if (thisRow.getKeyCell().bigerThan(key))
            {
                deleteNumber = i;
                deleteOrNot = true;
                break;
            }
        }
        if (!deleteOrNot)
        {
            deleteNumber = rowList.size();
        }
        if (sonNodeList == null)
        {
            return false;
        }
        else
        {
            return new Node(sonNodeList.get(deleteNumber)).deleteRow(key);
        }
    }

    public Row getRow()
    {
        if (rowList.size() > 0)
        {
            return new Row(rowList.get(0));
        }
        else
        {
            return null;
        }
    }

    public Row getFirstRow()
    {
        if (sonNodeList == null)
        {
            return new Row(rowList.get(0));
        }
        else
        {
            return new Node(sonNodeList.get(0)).getFirstRow();
        }
    }

    public Row getLastRow()
    {
        if (sonNodeList == null)
        {
            return new Row(rowList.get(rowList.size() - 1));
        }
        else
        {
            return new Node(sonNodeList.get(sonNodeList.size() - 1)).getLastRow();
        }
    }

    public Row getSpecifyRow(Cell key)
    {
        int insertNumber = -1;
        for (int i = 0; i < rowList.size(); i++)
        {
            Row thisRow = new Row(rowList.get(i));
            if (thisRow.getKeyCell().equalTo(key))
            {
                return thisRow;
            }
            else if (thisRow.getKeyCell().bigerThan(key))
            {
                if (sonNodeList == null)
                {
                    insertNumber = i;
                    break;
                }
                else
                {
                    return new Node(sonNodeList.get(i)).getSpecifyRow(key);
                }
            }
        }
        if (sonNodeList == null)
        {
            if (insertNumber > 0)
            {
                return new Row(rowList.get(insertNumber));
            }
            else
            {
                return null;
            }
        }
        else
        {
            return new Node(sonNodeList.get(sonNodeList.size() - 1)).getSpecifyRow(key);
        }

    }
}
