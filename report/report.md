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



#### 3.5 *IndexManager* 接口（stl&zjs）



#### 3.6 *BufferManager* 接口

***Buffer Manager* 设计思想：** `BufferManager`是通过与磁盘中文件进行数据交换来实现缓存的一种数据库管理机制。在本项目中，为了提高文件系统的`I/O`效率，我们约定缓冲区的一页（块）为`4KB`，同时缓冲区中最多可以容纳`50`个`Page(Block)`。其核心设计方法包括：

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
