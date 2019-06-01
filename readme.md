# Minisql

### Update Info 更新信息

| Date       | User | Info                             |
| ---------- | ---- | -------------------------------- |
| 2019-05-18 | ycj  | 新建项目框架，约定命名、注释规范 |
| 2019-05-30 | ycj  | Buffer&BufferManager实现|
| 2019-06-01 | yrj  | Address&FieldType&TableRow&Condition类实现|
| 2019-06-01 | ycj | BufferManager类增加了make_invalid接口，修改了read_block_from_disk函数 |

------------------------------

### Coding Style 代码风格约定

```java
	/*
     * [代码风格约定]
     * 函数命名采用下划线法，示例：void build_red_black_tree();
     * 变量命名采用小驼峰，示例：indexValue
     * 每个类的类名必须大写，且需要提供相应的接口，并对接口进行注释
     * 每个类中的方法需要进行注释
     * 单行注释统一采用//
     * 函数类定义的左大括号不换行，示例： void build_red_black_tree() {
     * 变量赋值、运算符预留空格，示例：int a = b + 5;
     * [新增]必要时可以在函数内部自行处理异常抛出
     * [新增]静态常量（宏）定义大写，示例：public static final int SIZE = 1024;
     * */

	/*
     * [示例]
     * function: 主方法
     * param[in]: 命令行参数数组args(如果是void则省略该行)
     * param[out]: (如果是void则省略该行)
     * author: ycj (采用个人姓名缩写)
     * note: 这是一个示例(如果没有特殊说明则省略该行)
     * */
```

---

### Buffer Manager 模块接口说明

##### 约定：块大小为4KB

```java
//构造函数，为缓冲区中所有块申请内存空间
+ BufferManage();

//将块中有效的且脏的数据回写到磁盘
+ public void destruct_buffer_manager();

//将缓冲区中对应文件名为filename的块设置为无效
+ public void make_invalid(String filename);

//将文件中的第ofs块读入到缓冲区中
//如果第ofs块已经在内存中存在，则直接返回对该块的引用
//如果不存在空闲的块则返回null
//如果ofs溢出，则返回对默认块（所有字节都是0）的引用
//如果第ofs块在内存中不存在但是其他必要条件满足，则将该块从文件写入到缓冲区中并返回对该块的引用
+ public Block read_block_from_disk_quote(String filename, int ofs);

//将文件中第ofs块读入到缓冲区中，并返回对该块的下标，若出现错误则返回-1或默认块
+ public int read_block_from_disk(String filename, int ofs);

-----------------------------------------
//从磁盘的filename文件中的第ofs块读取数据到缓冲区的第bid块中
//若操作成功则返回true，否则返回false
+ private boolean read_block_from_disk(String filename, int ofs, int bid);

//把缓冲区的第bid块回写到磁盘中
//若操作成功则返回true，否则返回false
+ private boolean write_block_to_disk(int bid);

//得到缓冲区中空闲的块的编号，若返回-1则没有所有的块都被占用
+ private int get_free_block_id();
```

##### 此外对于缓冲区中的块，提供部分底层的操作接口：

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

-----------------------
### Address类说明
```java
//成员变量定义
+ private String fileName; //文件名字
+ private int blockOffset; //块在文件内的偏移
+ private int byteOffset;  //字节在块内的偏移
```

### FieldType 类说明
```java
//成员变量定义
+ private String type; // 数值类型,"char","int","float" 三种
+ private int length;  // char类型对应的长度
```

### Condition 类说明
```java
//成员变量定义
+ private String name;  // 属性名字
+ private String value; // 属性的值
+ private String operator; //操作符，"=","<>",">","<",">=","<=" 六种
```

### TableRow 类说明
```java
//成员变量定义
+ private List<String> attributeValue; // 属性值列表

//成员方法
+ public void add_attribute_value(String attributeValue); // 添加一个属性值
+ public String get_attribute_value(int index)； // 得到对应下标的属性值
```


-----------------------
