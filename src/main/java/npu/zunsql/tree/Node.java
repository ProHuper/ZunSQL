package npu.zunsql.tree;

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
    private List<Row> rowList;

    // 每个节点包含不少于M/2+1，不超过M+1的SonNode。
    private List<Node> sonList;

    // 表示父亲节点
    private Node father;

    // 表示本节点在父亲节点儿子中的第几位。
    private int order;

    // Node简单的构造方法
    protected Node()
    {
        rowList = new ArrayList<Row>();
        sonList = new ArrayList<Node>();
        father = null;
        order = -1;
    }

    // 根据Node的属性构造Node。
    private Node(List<Row> thisRowList, List<Node> thisSonList, int thisOrder)
    {
        rowList = thisRowList;
        sonList = thisSonList;
        father = null;

        // 为每一位儿子维护父亲和排位信息。
        for (int i = 0; i < sonList.size(); i++)
        {
            sonList.get(i).father = this;
            sonList.get(i).order = i;
        }

        // 维护自身排位信息。
        order = thisOrder;
    }

    // 分裂除根节点外的其他节点。
    private Node devideNode()
    {
        List<Row> rightRow;
        List<Node> rightNode;
        rightRow = rowList.subList(M/2 + 1, M);
        rowList = rowList.subList(0, M/2);
        rightNode = sonList.subList(M/2 + 1,M + 1);
        sonList = sonList.subList(0, M/2 + 1);
        return new Node(rightRow, rightNode, order + 1);
    }

    // 分裂根节点
    private boolean rootDevideNode()
    {
        List<Row> leftRow;
        List<Row> rightRow;
        List<Node> leftNode;
        List<Node> rightNode;
        leftRow = rowList.subList(0, M/2);
        leftNode = sonList.subList(0, M/2 + 1);
        rightRow = rowList.subList(M/2 + 1, M);
        rightNode = sonList.subList(M/2 + 1, M + 1);
        rowList = rowList.subList(M/2, M/2 + 1);
        List<Node> newSonList = new ArrayList<Node>();
        newSonList.add(new Node(leftRow, leftNode, 0));
        newSonList.add(new Node(rightRow, rightNode,1));
        sonList = newSonList;
        return true;
    }

    // 调整本节点使其顺序为sonOrder的儿子row数量恢复至M/2
    private boolean adjustNode(int sonOrder)
    {
        // 排除最大值边界越界情况，向左下合并
        if (sonOrder < sonList.size() - 1 && sonList.get(sonOrder + 1).rowList.size() > M/2)
        {
            sonList.get(sonOrder).insertRow(rowList.get(sonOrder));
            rowList.set(sonOrder,sonList.get(sonOrder + 1).getFirstRow());
            sonList.get(sonOrder + 1).deleteRow(rowList.get(sonOrder).getKeyCell());
            return true;
        }
        // 排除零值边界越界情况，向右下合并
        else if (sonOrder > 0 && sonList.get(sonOrder - 1).rowList.size() > M/2)
        {
            sonList.get(sonOrder).insertRow(rowList.get(sonOrder - 1));
            rowList.set(sonOrder - 1, sonList.get(sonOrder - 1).getLastRow());
            sonList.get(sonOrder - 1).deleteRow(rowList.get(sonOrder - 1).getKeyCell());
            return true;
        }
        // 没有相邻的可支援兄弟节点，只好删除此节点。
        else
        {
            return deleteNode(sonOrder);
        }
    }

    // 在本节点中添加子节点，分别添加row和对应的SonNode。
    private boolean addNode(Row row, Node node)
    {
        // 用于记录是否添加了这个节点。
        boolean addOrNot = false;
        for (int i = 0; i < rowList.size(); i++)
        {
            if (rowList.get(i).getKeyCell().bigerThan(row.getKeyCell()))
            {
                row.setLeftRow(rowList.get(i).getLeftRow());
                row.setRightRow(rowList.get(i));
                rowList.get(i).getLeftRow().setRightRow(row);
                rowList.get(i).setLeftRow(row);
                rowList.add(i, row);
                sonList.add(i, node);
                sonList.get(i).order = i;
                sonList.get(i).father = this;
                addOrNot = true;
                break;
            }
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
                row.setLeftRow(rowList.get(rowList.size() - 1));
                rowList.get(rowList.size() - 1).setRightRow(row);
            }
            row.setRightRow(null);
            rowList.add(row);
            sonList.add(sonList.size() - 2, node);
        }

        // 当未超出长度时，插入完毕。
        if (rowList.size() <= M)
        {
            return true;
        }
        // 超出长度时，进行单元分裂。
        else
        {
            if (father == null)
            {
                return rootDevideNode();
            }
            else
            {
                return father.addNode(rowList.get(M/2),devideNode());
            }
        }
    }

    private boolean deleteNode(int sonOrder)
    {
        if (sonOrder < sonList.size() - 1)
        {
            sonList.get(sonOrder + 1).insertRow(rowList.get(sonOrder));
            if (sonOrder < rowList.size() - 1)
            {
                rowList.get(sonOrder).getRightRow().setLeftRow(rowList.get(sonOrder).getLeftRow());

            }
            if (sonOrder > 0)
            {
                rowList.get(sonOrder).getLeftRow().setRightRow(rowList.get(sonOrder).getRightRow());
            }
            rowList.remove(sonOrder);
            sonList.remove(sonOrder);
            for (int i = sonOrder; i < sonList.size(); i++)
            {
                sonList.get(i).order = i;
            }
            if (rowList.size() < M/2)
            {
                if (father == null)
                {
                    if (rowList.size() < 1)
                    {
                        rowList = sonList.get(0).rowList;
                        sonList = sonList.get(0).sonList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return father.adjustNode(order);
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            sonList.get(sonOrder - 1).insertRow(rowList.get(sonOrder - 1));
            if (sonOrder < rowList.size() - 1)
            {
                rowList.get(sonOrder).getRightRow().setLeftRow(rowList.get(sonOrder).getLeftRow());

            }
            if (sonOrder > 0)
            {
                rowList.get(sonOrder).getLeftRow().setRightRow(rowList.get(sonOrder).getRightRow());
            }
            rowList.remove(sonOrder - 1);
            sonList.remove(sonOrder);
            for (int i = sonOrder; i < sonList.size(); i++)
            {
                sonList.get(i).order = i;
            }
            if (rowList.size() < M/2)
            {
                if (father == null)
                {
                    if (rowList.size() < 1)
                    {
                        rowList = sonList.get(0).rowList;
                        sonList = sonList.get(0).sonList;
                        return true;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return father.adjustNode(order);
                }
            }
            else
            {
                return true;
            }
        }
    }

    public boolean insertRow(Row row)
    {
        boolean insertOrNot = false;
        int insertNumber = 0;
        for (int i = 0; i < rowList.size(); i++)
        {
            if (rowList.get(i).getKeyCell().equalTo(row.getKeyCell()))
            {
                return false;
            }
            else if (rowList.get(i).getKeyCell().bigerThan(row.getKeyCell()))
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
        if (sonList == null)
        {
            rowList.add(insertNumber,row);
            if (rowList.size() <= M)
            {
                return true;
            }
            else
            {
                if (father == null)
                {
                    return rootDevideNode();
                }
                else
                {
                    return father.addNode(rowList.get(M/2),devideNode());
                }
            }
        }
        else
        {
            return sonList.get(insertNumber).insertRow(row);
        }
    }

    public boolean deleteRow(Cell key)
    {
        boolean deleteOrNot = false;
        int deleteNumber = 0;
        for (int i = 0; i < rowList.size(); i++)
        {
            if (rowList.get(i).getKeyCell().equalTo(key))
            {
                if (sonList == null)
                {
                    if (i < rowList.size() - 1)
                    {
                        rowList.get(i).getRightRow().setLeftRow(rowList.get(i).getLeftRow());

                    }
                    if (i > 0)
                    {
                        rowList.get(i).getLeftRow().setRightRow(rowList.get(i).getRightRow());
                    }
                    rowList.remove(i);
                    if (rowList.size() < M/2)
                    {
                        if (father == null)
                        {
                            if (rowList.size() < 1)
                            {
                                rowList = sonList.get(0).rowList;
                                sonList = sonList.get(0).sonList;
                                return true;
                            }
                            else
                            {
                                return true;
                            }
                        }
                        else
                        {
                            return father.adjustNode(order);
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    rowList.set(i, rowList.get(i).getRightRow());
                    return sonList.get(i + 1).deleteRow(rowList.get(i).getRightRow().getKeyCell());
                }
            }
            else if (rowList.get(i).getKeyCell().bigerThan(key))
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
        if (sonList == null)
        {
            return false;
        }
        else
        {
            return sonList.get(deleteNumber).deleteRow(key);
        }
    }

    public Row getRow()
    {
        if (rowList.size() > 0)
        {
            return rowList.get(0);
        }
        else
        {
            return null;
        }
    }

    public Row getFirstRow()
    {
        if (sonList == null)
        {
            return rowList.get(0);
        }
        else
        {
            return sonList.get(0).getFirstRow();
        }
    }

    public Row getLastRow()
    {
        if (sonList == null)
        {
            return rowList.get(rowList.size() - 1);
        }
        else
        {
            return sonList.get(sonList.size() - 1).getLastRow();
        }
    }

    public Row getSpecifyRow(Cell key)
    {
        int insertNumber = -1;
        for (int i = 0; i < rowList.size(); i++)
        {
            if (rowList.get(i).getKeyCell().equalTo(key))
            {
                return rowList.get(i);
            }
            else if (rowList.get(i).getKeyCell().bigerThan(key))
            {
                if (sonList == null)
                {
                    insertNumber = i;
                    break;
                }
                else
                {
                    return sonList.get(i).getSpecifyRow(key);
                }
            }
        }
        if (sonList == null)
        {
            if (insertNumber > 0)
            {
                return rowList.get(insertNumber);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return sonList.get(sonList.size() - 1).getSpecifyRow(key);
        }

    }
}
