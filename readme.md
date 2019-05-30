# Minisql



### Update Information

| Date       | User | Info                             |
| ---------- | ---- | -------------------------------- |
| 2019-05-18 | ycj  | 新建项目框架，约定命名、注释规范 |
| 2019-05-30 | ycj  | Buffer&BufferManager实现|

### Buffer Manager 模块接口说明

##### 约定：块大小为4KB

```java
//构造函数，为缓冲区中所有块申请内存空间
+ BufferManage();

//将块中有效的且脏的数据回写到磁盘
+ public void destruct_buffer_manager();

//从磁盘的filename文件中的第ofs块读取数据到缓冲区的第bid块中
//若操作成功则返回true，否则返回false
+ public boolean read_block_from_disk(String filename, int ofs, int bid);

//将文件中的第ofs块读入到缓冲区中
//如果第ofs块已经在内存中存在，则直接返回对该块的引用
//如果文件不存在或者ofs不合法或者不存在空闲的块则返回null
//如果第ofs块在内存中不存在但是其他必要条件满足，则将该块从文件写入到缓冲区中并返回对该块的引用
+ public Block read_block_from_disk(String filename, int ofs)

//把缓冲区的第bid块回写到磁盘中
//若操作成功则返回true，否则返回false
+ public boolean write_block_to_disk(int bid);

//得到缓冲区中空闲的块的编号，若返回-1则没有所有的块都被占用
+ public int get_free_block_id();
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



