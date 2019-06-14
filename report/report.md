# **MiniSQL设计说明书**

##### 浙江大学2018~2019学年春夏学期《数据库系统》夏学期大程报告

## 1 总体框架（zjs/stl看着补充一些）

#### 1.1 实现功能分析

##### 1.1.1 总目标：

- 设计并实现一个精简型单用户*SQL*引擎 ***(DBMS)*** ***MiniSQL***，允许用户通过字符界面输入*SQL*语句实现表的建立/删除；索引的建立/删除以及表记录的插入/删除/查找。

##### 1.1.2 需求概述

- **数据类型：** 只要求支持三种基本数据类型：`int`，`char(n)`，`float`，其中`char(n)`满足 $1 ≤n ≤ 255$。
- **表定义：** 一个表最多可以定义$32$个属性，各属性可以指定是否为`unique`；支持单属性的主键定义。
- **索引的建立和删除：** 对于表的主属性自动建立`B+`树索引，对于声明为`unique`的属性可以通过SQL语句由用户指定建立/删除`B+`树索引（因此，所有的`B+`树索引都是单属性单值的）。
- **查找记录：** 可以通过指定用`and`连接的多个条件进行查询，支持等值查询和区间查询。
- **插入和删除记录：** 支持每次一条记录的插入操作；支持每次一条或多条记录的删除操作。

#### 1.2 系统体系结构

![1-2-1](1-2-1.png)

#### 1.3 设计语言与运行环境

**工具：JAVA JDK 10.0.2**

**集成开发环境：Win10 IntelliJ IDEA**



## 2 各模块实现功能

#### 2.1 *Interpreter*

`Interpreter`模块直接与用户交互，主要实现以下功能：

- 程序流程控制，即启动并初始化→【接收命令、处理命令、显示命令结果】循环→退出流程。

- 接收并解释用户输入的命令，生成命令的内部数据结构表示，同时检查命令的语法正确性和语义正确性，对正确的命令调用API层提供的函数执行并显示执行结果，对不正确的命令显示错误信息。


#### 2.2 *API*

`API`模块是整个系统的核心，其主要功能为提供执行SQL语句的接口，供`Interpreter`层调用。该接口以`Interpreter`层解释生成的命令内部表示为输入，根据`Catalog Manager`提供的信息确定执行规则，并调用`Record Manager`、`Index Manager`和`Catalog Manager`提供的相应接口进行执行，最后返回执行结果给`Interpreter`模块。

#### 2.3 *Catalog Manager*

`Catalog Manager`负责管理数据库的所有模式信息，包括：

- 数据库中所有表的定义信息，包括表的名称、表中字段（列）数、主键、定义在该表上的索引。

- 表中每个字段的定义信息，包括字段类型、是否唯一等。

- 数据库中所有索引的定义，包括所属表、索引建立在那个字段上等。


`Catalog Manager`还必需提供访问及操作上述信息的接口，供`Interpreter`和`API`模块使用。

#### 2.4 *Record Manager*

`Record Manager`负责管理记录表中数据的数据文件。主要功能为实现数据文件的创建与删除（由表的定义与删除引起）、记录的插入、删除与查找操作，并对外提供相应的接口。其中记录的查找操作要求能够支持不带条件的查找和带一个条件的查找（包括等值查找、不等值查找和区间查找）。

数据文件由一个或多个数据块组成，块大小应与缓冲区块大小相同。一个块中包含一条至多条记录，为简单起见，只要求支持定长记录的存储，且不要求支持记录的跨块存储。

#### 2.5 *Index Manager*

`Index Manager`负责`B+`树索引的实现，实现`B+`树的创建和删除（由索引的定义与删除引起）、等值查找、插入键值、删除键值等操作，并对外提供相应的接口。

`B+`树中节点大小应与缓冲区的块大小相同，`B+`树的叉数由节点大小与索引键大小计算得到。

#### 2.6 *Buffer Manager*

`Buffer Manager`负责缓冲区的管理，主要功能有：

- 根据需要，读取指定的数据到系统缓冲区或将缓冲区中的数据写出到文件

- 实现缓冲区的替换算法，当缓冲区满时选择合适的页进行替换

- 记录缓冲区中各页的状态，如是否被修改过等

- 提供缓冲区页的*pin*功能，及锁定缓冲区的页，不允许替换出去

为提高磁盘I/O操作的效率，缓冲区与文件系统交互的单位是块，块的大小应为文件系统与磁盘交互单位的整数倍，一般可定为4KB或8KB。

#### 2.7 *DB Files*

`DB Files`指构成数据库的所有数据文件，主要由记录数据文件、索引数据文件和`Catalog`数据文件组成。



## 3 内部数据形式及各模块提供的接口（各写各的）

#### 3.1 内部数据存放形式（zjs）



#### 3.2 *Interpreter* 实现（yrj先写 ycj补充）



#### 3.3 *CatalogManager* 接口（stl）



#### 3.4 *RecordManager* 接口（yrj）
** *RecodManager* 设计思想： ** `RecordManager`负责管理记录表中数据的数据文件，实现最终对文件内记录的增查删改操作，其总体设计思想如下：

- **单表存储**：一个文件存一张表，一张表上的所有记录全部存在一个文件内

- **文件结构**：采用堆文件结构，记录在有空闲的地方均可插入，无须再对记录顺序进行额外处理。

- **空闲链表**：为保证利用充分的存储空间，文件会维护一个空闲链表，将空闲的记录区域进行记录。在文件的`第1个Block`的`前4Bytes`保存第一个空闲记录所在的地址 (一个地址对应一条记录，从0开始编址)，对于指向的空闲记录，它会保存下一个空闲记录所在的地址，形成一个空闲链表。插入时可以根据空闲链表对应的地址进行记录的插入，充分利用空闲空间。注：若空闲地址为`-1`，表明此为空闲链表的尾部，若继续插入，则需要插入到所有记录的尾部。

- **记录存储**：记录在块之间连续存储，若块剩余空间不足，则在新的块中存储，记录不会跨块存储。每条记录开头额外存储`1Byte`的标志字节，用于标志该条记录是否有效，后面存储记录对应的属性值。若该条记录是存在的，标志字节置为`-1`，后面存储对应的属性值；若该条记录是空的，标志字节置为`0`，后面的属性空间存储一个`4Bytes`大小的地址，用于表示下一个空闲记录所在的地址，达到存储空间重用。注: 若记录属性值本身不足`4Bytes`，则将其扩充到`4Bytes`，保证表中所有记录所占空间是固定的。

- **文件读写**：使用`BufferManager`对文件块进行读写，使用缓存技术减小文件`I/O`操作。

** *RecordManager* 主要函数实现： **

- **建表**：使用`File`类的`createNewFile`函数创建一个与表名相同的文件，读入文件的第一个快，将`前4Bytes`的存储空间写入`-1`，表明此为空闲链表的尾部。

- **删表**：调用`BufferManager`函数将该文件所有在缓冲区的块置为无效，调用`File`类的`delete`函数将文件删除。

- **查找**：对于对应表名的文件，从文件头开始顺序扫描记录，跳过首字节为`0`的空记录，读取存在记录对应的属性，判断是否符合条件，若符合则将其加入结果中，查找完毕返回结果。

- **插入**：对于对应表名的文件，首先读取其第一个块，读取`前4Bytes`的空闲链表地址，若为`-1`，则调用`CatalogManager`获取记录个数，找到全部记录尾部对应的地址，若为非负数，则直接定位第一个空闲记录地址。找到地址后，在首字节写入`-1`，在后续空间写入记录对应的属性值，同时更新文件第一个块`前4Bytes`的空闲记录地址。

- **删除**：对于对应表名的文件，首先读取其第一个块并给其上锁。顺序扫描记录，跳过首字节为`0`的空记录，读取存在记录对应的属性，若符合删除条件则将其标志字节置为`0`，然后将接下来`4Bytes`的空间写入文件头的空闲地址值，用于指示下一个空闲记录地址，最后更新文件头`前4Bytes`的空闲地址，使其指向当前的删除记录所在的地址，完成空闲链表的更新。

- **地址查找/删除**：对于存在索引的表，通过`IndexManager`可以找到记录所在的地址，因此`RecordManager`提供直接通过地址来对记录查找/删除的方法。与普通查找/删除方法类似，不同之处在于定位方式不再是顺序扫描，而是根据地址对应的值逐个定位，其余操作与普通查找/删除方法类似。为尽量减小不同块的访问次数,首先会对全部地址从低到高排序，使在同一块内的记录可以统一处理。

- **记录保存**：通过调用`BufferManager`的`destruct_buffer_manager`函数清空缓冲区，将缓冲区内的块写回硬盘中，实现记录的保存。

`RecordManager`对外提供如下接口：

```java
//创建给定表名的表（新建文件）
//若创建成功返回true，创建失败返回false
+ public static boolean create_table(String tableName)；

//删除给定表名的表 (删除文件）
//若删除成功返回true，删除失败返回false
+ public static boolean drop_table(String tableName)；

//select功能函数，给定表名和条件，返回符合该条件的全部记录
//返回记录的属性顺序和创建表时一致
+ public static Vector<TableRow> select(String tableName, Vector<Condition> conditions)；

//insert功能函数，给定表名和待插入记录，返回该记录所插入的地址
//插入记录中属性顺序必须和创建表时属性的顺序一致
//若插入失败，返回null
+ public static Address insert(String tableName, TableRow data)；

//delete功能函数，给定表名和条件，删除表中符合条件的记录，返回删除记录的个数
+ public static int delete(String tableName, Vector<Condition> conditions)；

//select功能函数，给定一系列地址及条件，返回对应地址、且符合条件的全部记录
//返回记录的属性顺序和创建表时一致
//所有地址必须在同一文件内
//若地址对应的记录不存在，则不会加入结果中
+ public static Vector<TableRow> select(Vector<Address> address，Vector<Condition> conditions)；

//delete功能函数，给定一系列地址，删除对应地址、且符合条件的记录，返回删除记录的个数
//所有地址必须在同一文件内
//若地址对应的记录不存在，则不会计入删除数
+ public static int delete(Vector<Address> address，Vector<Condition> conditions)；

//project功能函数，给定表名，查询结果和投影属性名称，返回投影后的记录结果
//投影属性名称顺序没有要求，查询结果的属性顺序必须和创建表时的属性一致
//投影结果的属性顺序与投影名称顺序一致
+ public static Vector<TableRow> project(String tableName, Vector<TableRow> result, Vector<String> projectName)；

//保存函数，将当前操作的记录保存到磁盘中
//程序结束前调用，其将内部缓冲区的块写入磁盘中
+ public static void store_record()；
```

#### 3.5 *IndexManager* 接口（stl&zjs）



#### 3.6 *BufferManager* 接口

** *Buffer Manager* 设计思想：** `BufferManager`是通过与磁盘中文件进行数据交换来实现缓存的一种数据库管理机制。在本项目中，为了提高文件系统的`I/O`效率，我们约定缓冲区的一页（块）为`4KB`，同时缓冲区中最多可以容纳`50`个`Page(Block)`。其核心设计方法包括：

- 从磁盘中读写文件：通过`File`类进行文件操作以及`RandomAccessFile`进行文件中字节定位。

- 通过`LRU`算法对缓冲区进行替换：每次对缓冲区的页进行访问时时，都会将其所带有的计数器加一，当缓冲区中所有页都有数据时，需要选择`LRUCount`值最少的（也就是最少使用）的页进行替换，以提高运行效率。

- 缓冲区各页的状态主要通过私有变量进行维护，且对外提供公共接口。主要的状态变量有：

  - `isDirty`：页中的数据是否是脏的（被修改过，但是没有写回硬盘）。
  - `isValid`：页中的数据是否是合法存在的。当`isValid`为`False`的时候，块中的数据不需要被回写会磁盘，因为它在载入时在原有的文件中并不存在对应的块。
  - `isLocked`：页是否被锁住（被占用）。
  - `LRUCount`：用于LRU算法的计数器。
  - `filename`：页所对应的文件名
  - `blockOffset`：页在文件中对应的偏移量（`4KB`每`offset`）

- 从缓冲区中某一页的某一起始偏移量读（写）入整数、浮点数或是`length`长度的字符串（内存为`Big-Endian`的大头模式）：

  - 整数：构造算法

    ```java
    int b0 = blockData[offset] & 0xFF;
    int b1 = blockData[offset + 1] & 0xFF;
    int b2 = blockData[offset + 2] & 0xFF;
    int b3 = blockData[offset + 3] & 0xFF;
    return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    ```

  - 浮点数：调用`Float.floatToIntBits`或`Float.intBitsToFloat`方法。

  - 字符串：由于一个`char`占用两个字节，因此需要调用`str.getBytes()`方法来转换成`Byte`类型。

`BufferManager`对外提供如下接口：

```java
//构造函数，为缓冲区中所有块申请内存空间
+ public static void initial_buffer();

//将块中有效的且脏的数据回写到磁盘
+ public static void destruct_buffer_manager();

//将缓冲区中对应文件名为filename的块设置为无效
+ public static void make_invalid(String filename);

//将文件中的第ofs块读入到缓冲区中
//如果第ofs块已经在内存中存在，则直接返回对该块的引用
//如果不存在空闲的块则返回null
//如果ofs溢出，则返回对默认块（所有字节都是0）的引用
//如果第ofs块在内存中不存在但是其他必要条件满足，则将该块从文件写入到缓冲区中并返回对该块的引用
+ public static Block read_block_from_disk_quote(String filename, int ofs);

//将文件中第ofs块读入到缓冲区中，并返回对该块的下标，若出现错误则返回-1或默认块
+ public static int read_block_from_disk(String filename, int ofs);

//--------------------以下是不对外提供但是对整体功能实现比较重要的接口---------------------
//从磁盘的filename文件中的第ofs块读取数据到缓冲区的第bid块中
//若操作成功则返回true，否则返回false
+ private static boolean read_block_from_disk(String filename, int ofs, int bid);

//把缓冲区的第bid块回写到磁盘中
//若操作成功则返回true，否则返回false
+ private static boolean write_block_to_disk(int bid);

//得到缓冲区中空闲的块的编号，若返回-1则没有所有的块都被占用
+ private static int get_free_block_id();
```

此外，这里也给出`BufferManager`的单元构成`Block`类的对外接口：

```java
//返回块是否是脏的或设置块是否是脏的
+ public boolean dirty();
+ public void dirty(boolean flag);

//返回块是否是锁住的，或给块上锁/取消锁
+ public boolean lock();
+ public void lock(boolean flag);

//返回块是否是有效的，或设置块是否是有效的
+ public boolean valid();
+ public void valid(boolean flag);

//获得或设置块对应的文件名
+ public String get_filename();
+ public void set_filename(String fname);

//获得或设置块在文件中的偏移量
+ public int get_block_offset();
+ public void set_block_offset(int ofs);

//获得或设置块中存储的数据
+ public byte[] get_block_data();
+ public void set_block_data(byte[] data);

//清空块中所有数据
+ public void set_block_data();

//读入或写入一个整数到offset字节数处
+ public int read_integer(int offset);
+ public boolean write_integer(int offset, int val);

//读入或写入一个单精度浮点数到offset字节数处
+ public float read_float(int offset);
+ public boolean write_float(int offset, float val);

//读入或写入一个长度为length的字符串到offset字节数处
+ public String read_string(int offset, int length);
+ public boolean write_string(int offset, String str);
```

#### 3.7 *DB Files* 管理（谁建的文件谁写hhh）



## 4 系统测试（zjs）





## 5 其他说明

#### 5.1 个人分工（可能会有遗漏，自己看着再加一点）

| 姓名   | 分工          |
| ------ | ------------ |
| 应承峻 | 整体框架搭建<br>代码开发约定<br>BufferManager<br>API中大部分模块<br>Interpreter中Select和Delete的解析 |
| 朱璟森 | BPTree<br>IndexManager部分模块<br>API中Select模块<br>整体功能测试 |
| 沈韬立 | CatalogManager<br>IndexManager<br>API中Select模块<br>整体功能测试 |
| 余瑞璟 | RecordManager<br>Interpreter中大部分模块<br>整体功能测试 |

#### 5.2 项目Git地址
待验收结束后将开源项目地址： *https://github.com/ChenjunYing/Minisql*


#### 5.3 项目更新日志（最后再加）
