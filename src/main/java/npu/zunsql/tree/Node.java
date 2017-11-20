package npu.zunsql.tree;

import npu.zunsql.cache.Page;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by WQT on 2017/11/6.
 */

// 本Node类用于组织B树内部结构
// 每个Node包含一个Row列表和一个SonNode列表，其中SonNode列表除初始化时外，始终比Row列表多一个。
public class Node {
    // 用于表示本树为几阶B树。
    public final static int M = 3;

    // 每个节点包含不少于M/2，不超过M的Row。
    private List<Integer> rowPageList;

    // 每个节点包含不少于M/2+1，不超过M+1的SonNode。
    private List<Integer> sonPageList;

    // 表示父亲节点
    private int father;

    // 表示本节点在父亲节点儿子中的第几位。
    private int order;

    // 表示PageID
    private int pageID;

    protected Node(Page thisPage)
    {
        rowPageList = null;
        sonPageList = null;
        father = -1;
        order = -1;
        pageID = thisPage.getPageID();
    }

    protected Node(int thisPageID)
    {
        pageID = thisPageID;
    }

    // 根据Node的属性构造Node。
    private Node(List<Integer> thisRowList, List<Integer> thisSonList, int thisOrder)
    {
        rowPageList = thisRowList;
        sonPageList = thisSonList;
        father = -1;

        // 为每一位儿子维护父亲和排位信息。
        for (int i = 0; i < sonPageList.size(); i++)
        {
            new Node(sonPageList.get(i)).setFather(pageID);
            new Node(sonPageList.get(i)).setOrder(i);
        }

        // 维护自身排位信息。
        order = thisOrder;
    }

    private boolean setFather(int ID)
    {
        father = ID;

        // TODO：维护page信息

        return true;
    }


    private boolean setOrder(int or)
    {
        order = or;

        // TODO：维护page信息

        return true;
    }

    // 分裂除根节点外的其他节点。
    private Node devideNode()
    {
        List<Integer> rightRow;
        List<Integer> rightNode;
        rightRow = rowPageList.subList(M/2 + 1, M);
        rowPageList = rowPageList.subList(0, M/2);
        rightNode = sonPageList.subList(M/2 + 1,M + 1);
        sonPageList = sonPageList.subList(0, M/2 + 1);
        return new Node(rightRow, rightNode, order + 1);
    }

    // 分裂根节点
    private boolean rootDevideNode()
    {
        List<Integer> leftRow;
        List<Integer> rightRow;
        List<Integer> leftNode;
        List<Integer> rightNode;
        leftRow = rowPageList.subList(0, M/2);
        leftNode = sonPageList.subList(0, M/2 + 1);
        rightRow = rowPageList.subList(M/2 + 1, M);
        rightNode = sonPageList.subList(M/2 + 1, M + 1);
        rowPageList = rowPageList.subList(M/2, M/2 + 1);
        List<Integer> newSonList = null;
        newSonList.add(new Node(leftRow, leftNode, 0).pageID);
        newSonList.add(new Node(rightRow, rightNode,1).pageID);
        sonPageList = newSonList;
        return true;
    }

    // 调整本节点使其顺序为sonOrder的儿子row数量恢复至M/2
    private boolean adjustNode(int sonOrder)
    {
        Node thisSonNode = new Node(sonPageList.get(sonOrder));

        // 排除最大值边界越界情况，向左下合并
        if (sonOrder < sonPageList.size() - 1)
        {
            Node rightSonNode = new Node(sonPageList.get(sonOrder + 1));
            if (rightSonNode.rowPageList.size() > M/2)
            {
                thisSonNode.insertRow(rowPageList.get(sonOrder));
                rowPageList.set(sonOrder, rightSonNode.getFirstRow().pageID);
                rightSonNode.deleteRow(new Row(rowPageList.get(order)).getKeyCell());
                return true;
            }

        }

        // 排除零值边界越界情况，向右下合并
        if (sonOrder > 0)
        {
            Node leftSonNode = new Node(sonPageList.get(sonOrder - 1));
            if (leftSonNode.rowPageList.size() > M/2)
            {
                thisSonNode.insertRow(rowPageList.get(sonOrder - 1));
                rowPageList.set(sonOrder - 1, leftSonNode.getLastRow().pageID);
                leftSonNode.deleteRow(new Row(rowPageList.get(order)).getKeyCell());
                return true;
            }
        }

        // 没有相邻的可支援兄弟节点，只好删除此节点。
        return deleteNode(sonOrder);
    }

    // 在本节点中添加子节点，分别添加row和对应的SonNode。
    private boolean addNode(Row row, Node node)
    {
        // 用于记录是否添加了这个节点。
        boolean addOrNot = false;
        for (int i = 0; i < rowPageList.size(); i++)
        {
            Row thisRow = new Row(rowPageList.get(i));
            Node thisNode = new Node(sonPageList.get(i));
            if (thisRow.getKeyCell().bigerThan(row.getKeyCell()))
            {
                row.setLeftRow(thisRow.getLeftRow());
                row.setRightRow(thisRow);
                thisRow.getLeftRow().setRightRow(row);
                thisRow.setLeftRow(row);
                rowPageList.add(i, row.pageID);
                sonPageList.add(i, node.pageID);
                thisNode.order = i;
                thisNode.father = pageID;
                addOrNot = true;
                break;
            }
        }
        // 如果之前都没有添加这个节点，那么此时添加至末尾。
        if (!addOrNot)
        {
            if (rowPageList.size() == 0)
            {
                row.setLeftRow(null);
            }
            else
            {
                row.setLeftRow(new Row(rowPageList.get(rowPageList.size() - 1)));
                new Row(rowPageList.get(rowPageList.size() - 1)).setRightRow(row);
            }
            row.setRightRow(null);
            rowPageList.add(row.pageID);
            sonPageList.add(sonPageList.size() - 2, node.pageID);
        }

        // 当未超出长度时，插入完毕。
        if (rowPageList.size() <= M)
        {
            return true;
        }
        // 超出长度时，进行单元分裂。
        else
        {
            if (father < 0)
            {
                return rootDevideNode();
            }
            else
            {
                return new Node(father).addNode(new Row(rowPageList.get(M/2)),devideNode());
            }
        }
    }

    private boolean deleteNode(int sonOrder)
    {
        Row thisRow;
        if (sonOrder < sonPageList.size() - 1)
        {
            thisRow = new Row(rowPageList.get(sonOrder));
            Node rightNode = new Node(sonPageList.get(sonOrder + 1));
            rightNode.insertRow(thisRow);
            if (sonOrder < rowPageList.size() - 1)
            {
                thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

            }
            if (sonOrder > 0)
            {
                thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
            }
            rowPageList.remove(sonOrder);
            sonPageList.remove(sonOrder);
            for (int i = sonOrder; i < sonPageList.size(); i++)
            {
                new Node(sonPageList.get(i)).order = i;
            }
            if (rowPageList.size() < M/2)
            {
                if (father < 0)
                {
                    if (rowPageList.size() < 1)
                    {
                        rowPageList = new Node(sonPageList.get(0)).rowPageList;
                        sonPageList = new Node(sonPageList.get(0)).sonPageList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return new Node(father).adjustNode(order);
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            thisRow = new Row(rowPageList.get(sonOrder - 1));
            Node leftNode = new Node(sonPageList.get(sonOrder - 1));
            leftNode.insertRow(thisRow);
            if (sonOrder < rowPageList.size() - 1)
            {
                thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

            }
            if (sonOrder > 0)
            {
                thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
            }
            rowPageList.remove(sonOrder - 1);
            sonPageList.remove(sonOrder);
            for (int i = sonOrder; i < sonPageList.size(); i++)
            {
                new Node(sonPageList.get(i)).order = i;
            }
            if (rowPageList.size() < M/2)
            {
                if (father < 0)
                {
                    if (rowPageList.size() < 1)
                    {
                        rowPageList = new Node(sonPageList.get(0)).rowPageList;
                        sonPageList = new Node(sonPageList.get(0)).sonPageList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return new Node(father).adjustNode(order);
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
        for (int i = 0; i < rowPageList.size(); i++)
        {
            if (new Row(rowPageList.get(i)).getKeyCell().equalTo(row.getKeyCell()))
            {
                return false;
            }
            else if (new Row(rowPageList.get(i)).getKeyCell().bigerThan(row.getKeyCell()))
            {
                insertNumber = i;
                insertOrNot = true;
                break;
            }
        }
        if (!insertOrNot)
        {
            insertNumber = rowPageList.size();
        }
        if (sonPageList == null)
        {
            rowPageList.add(insertNumber,row.pageID);
            if (rowPageList.size() <= M)
            {
                return true;
            }
            else
            {
                if (father < 0)
                {
                    return rootDevideNode();
                }
                else
                {
                    return new Node(father).addNode(new Row(rowPageList.get(M/2)),devideNode());
                }
            }
        }
        else
        {
            return new Node(sonPageList.get(insertNumber)).insertRow(row);
        }
    }

    public boolean deleteRow(Cell key)
    {
        boolean deleteOrNot = false;
        int deleteNumber = 0;
        for (int i = 0; i < rowPageList.size(); i++)
        {
            Row thisRow = new Row(rowPageList.get(i));
            if (thisRow.getKeyCell().equalTo(key))
            {
                if (sonPageList == null)
                {
                    if (i < rowPageList.size() - 1)
                    {
                        thisRow.getRightRow().setLeftRow(thisRow.getLeftRow());

                    }
                    if (i > 0)
                    {
                        thisRow.getLeftRow().setRightRow(thisRow.getRightRow());
                    }
                    rowPageList.remove(i);
                    if (rowPageList.size() < M/2)
                    {
                        if (father < 0)
                        {
                            if (rowPageList.size() < 1)
                            {
                                rowPageList = new Node(sonPageList.get(0)).rowPageList;
                                sonPageList = new Node(sonPageList.get(0)).sonPageList;
                                return true;
                            }
                            else
                            {
                                return true;
                            }
                        }
                        else
                        {
                            return new Node(father).adjustNode(order);
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    rowPageList.set(i, thisRow.getRightRow().pageID);
                    return new Node(sonPageList.get(i + 1)).deleteRow(thisRow.getRightRow().getKeyCell());
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
            deleteNumber = rowPageList.size();
        }
        if (sonPageList == null)
        {
            return false;
        }
        else
        {
            return new Node(sonPageList.get(deleteNumber)).deleteRow(key);
        }
    }

    public Row getRow()
    {
        if (rowPageList.size() > 0)
        {
            return new Row(rowPageList.get(0));
        }
        else
        {
            return null;
        }
    }

    public Row getFirstRow()
    {
        if (sonPageList == null)
        {
            return new Row(rowPageList.get(0));
        }
        else
        {
            return new Node(sonPageList.get(0)).getFirstRow();
        }
    }

    public Row getLastRow()
    {
        if (sonPageList == null)
        {
            return new Row(rowPageList.get(rowPageList.size() - 1));
        }
        else
        {
            return new Node(sonPageList.get(sonPageList.size() - 1)).getLastRow();
        }
    }

    public Row getSpecifyRow(Cell key)
    {
        int insertNumber = -1;
        for (int i = 0; i < rowPageList.size(); i++)
        {
            Row thisRow = new Row(rowPageList.get(i));
            if (thisRow.getKeyCell().equalTo(key))
            {
                return thisRow;
            }
            else if (thisRow.getKeyCell().bigerThan(key))
            {
                if (sonPageList == null)
                {
                    insertNumber = i;
                    break;
                }
                else
                {
                    return new Node(sonPageList.get(i)).getSpecifyRow(key);
                }
            }
        }
        if (sonPageList == null)
        {
            if (insertNumber > 0)
            {
                return new Row(rowPageList.get(insertNumber));
            }
            else
            {
                return null;
            }
        }
        else
        {
            return new Node(sonPageList.get(sonPageList.size() - 1)).getSpecifyRow(key);
        }

    }
}
