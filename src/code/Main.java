package code;

public class Main {

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

    public static void main(String[] args) {
        try {
            //Demo d = new Demo();
            //System.out.println("Minisql!");
            //System.out.println(d.max(3, 5));
            Block b = new Block();
            b.write_integer(1200,45);
            b.write_float(76,(float)32.14);
            b.write_string(492,"httnb!");
            b.dirty(true);
            b.valid(true);
            b.set_filename("hello.txt");
            b.set_block_offset(0);
            BufferManager m = new BufferManager();
            m.buffer[0] = b;
            m.write_block_to_disk(0);
            m.read_block_from_disk("hello.txt",0,1);
            Block bb = m.buffer[1];
            System.out.println(bb.read_integer(1200));
            System.out.println(bb.read_float(76));
            System.out.println(bb.read_string(492,5));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


}
