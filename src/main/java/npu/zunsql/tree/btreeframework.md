## 读写事务
### #define TT_READ 1
### #define TT_WRITE 2
## 数据类型
### #define CT_INT 1
### #define CT_DOUBLE 2
### #define CT_STRING 3
## 二级锁
### #define LO_LOCKED 1
### #define LO_SHARED 2

# class Database
## private:
### String DataBaseName;
	数据库名，DataBaseName仅在初始化时被赋值，其后不做任何修改。

### List`<Table>` TableList;
	表的列表

## public：
### Database(String DBName);
	本类的构造函数，

### Transaction BeginTrans(int TransType);
	开始一个事务,TransType表示事务类型
	读事务：TT_READ，写事务：TT_WRITE
	成功返回Transaction对象，失败返回0

### Table CreateTable(String TableName，Column KeyColumn);
	添加一张表
	成功返回Table，失败返回空。

### Table GetTable(String TableName);
	根据表名得到一张表
	成功返回Table，失败返回空。

### bool Lock();
	给整个数据库加锁
	遍历整个TableList，对所有的表进行加锁。
	成功返回true，失败返回false

### bool UnLock();
	给整个数据库解锁
	遍历整个TableList，对所有的表进行解锁。
	成功返回true，失败返回false




# class Transaction
## private:
### int tranNum;
	事务编号，从下层获取，用于标定不同事务。
	在本类中，tranNum仅在初始化时被赋值，其后不做任何修改。

## public：
### Transaction(int tranNum)
	本类的构造函数,主要实现对属性的初始化赋值。

### bool Commit();
	提交事务
	成功返回true，失败返回false

### bool Rollback();
	回滚事务
	成功返回true，失败返回false





# class Table
## private:
### String TableName;
	表名
### Column Key;
	主键
### List`<Column>` OtherColumn;
	其他列
### int Lock;
	本变量为锁标记，主要用于标记排他写。
	写锁：LO_LOCKED
	读锁：LO_SHARED
### Cell RootRow;

## public:
### table(String TName,Column KeyColumn,List`<Column>` OtherColumn);（不考虑主键取多列的情况）
	本函数为table类的构造函数
	TName为TableName,KeyColumn为Key,OtherColumn为其他列

### bool Drop();
	删除一张表
	成功返回true，失败返回false。

### bool Clear();
	清空一张表
	成功返回true，失败返回false。

### String GetTableName();
	得到表名
	成功返回TableName， 失败返回空。

### Cursor CreateCursor(String TableName);
	添加一个光标
	成功返回cursor，失败返回空。



# class Row
## private:
### List`<Cell>` Celllist;
### List<Row> SonRow;
### Row FatherRow;
### Row LeftBrotherRow;
### Row RightBrotherRow;

## public:
### Row(List`<Cell>` Celllist);
	Row类的构造函数
### bool ChangeCell(Cell ThisCell);
	改变某单元的值
  成功返回true，失败返回false。
### Cell getCell(Column ThisColumn);
	根据列信息找到单元格


# class Cell
## Private:
### Column ThisColumn;
	用于表征Cell所属的列。
### int value;
	若为整形，则使用value进行存取
### double dvalue;
	若为整形，则使用dvalue进行存储
### String Svalue;
	若为字符串，则使用Svalue进行存取
## Public:
### Cell(Column ThisColumn,int ThisValue);
### Cell(Column ThisColumn,double ThisValue);
### Cell(Column ThisColumn,String Thisvalue);
### Column getColumn();
### int getvalue_int();
### double getvalue_double();
### String getvalue_String();



# class Column
## private:
### int ColumnType;
### String ColumnName;

## public:
### Column(int Type,String ColumnName)



# class Cursor
## private:
### Table aimtable;
	表示目标表，在构造函数中赋值。
### Row thisRow;

## public：
### Cursor(Table thistable)
	本函数为cursor类的构造函数

### bool ClearCursor()	（感觉没啥必要）
	本函数将Cursor置为空
	成功返回true，失败返回false。

### bool MovetoFirst()
	本函数将Cursor指向Btree的第一个元素。
	成功返回true，失败返回false。

### bool MovetoLast()
	本函数将Cursor指向Btree的最后一个元素。
	成功返回true，失败返回false。

### bool MovetoNext()
	本函数将Cursor指向Cursor指向的下一个元素。
	成功返回true，失败返回false。

### bool MovetoPrevious()
	本函数将Cursor指向Cursor指向的上一个元素。
	成功返回true，失败返回false。

### bool MovetoUnpacked(Cell keyCell)
	本函数用于将Cursor定位到指定key的位置，如果匹配不到，则将Cursor停在与key值相近的某位置
	成功返回true，失败返回false。

### bool Delete();
	删除节点
	成功返回true，失败返回false。

### bool Insert(Row thisRow);
	插入节点
	成功返回true，失败返回false。

### int GetKeySize();
	获取KeySize
	成功返回true，失败返回false。

### Cell GetKey();
	获取Key值
	成功返回true，失败返回false。

### int GetDataSize();
	获取数据大小
	成功返回true，失败返回false。

### Row GetData();
	获取数据
	成功返回data，失败返回空。

### bool setData(List<BaseType>);
	修改数据
	成功返回true，失败返回false。

