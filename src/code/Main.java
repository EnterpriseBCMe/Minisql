package code;

public class Main {

    public static void main(String[] args) {
        buffer_unit_test(); //Buffer Manager test function
    }

    public static void buffer_unit_test() {
        try {
            BufferManager m = new BufferManager();
            int bid = m.read_block_from_disk("hello.txt", 0);
            buffer_print(m,bid);
            bid = m.read_block_from_disk("hello.txt", 15);
            buffer_print(m,bid);
            //m.test_interface();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buffer_print(BufferManager m, int bid) {
        System.out.println(bid);
        System.out.println("isLock = " + m.buffer[bid].lock());
        System.out.println("isDirty = " + m.buffer[bid].dirty());
        System.out.println("isValid = " + m.buffer[bid].valid());
        System.out.println(m.buffer[bid].read_integer(1200));
        System.out.println(m.buffer[bid].read_float(76));
        System.out.println(m.buffer[bid].read_string(492, 6));
        m.buffer[bid].write_integer(128, -23333);
        System.out.println("isLock = " + m.buffer[bid].lock());
        System.out.println("isDirty = " + m.buffer[bid].dirty());
        System.out.println("isValid = " + m.buffer[bid].valid());
        System.out.println("LRUCnt = " + m.buffer[bid].get_LRU());
        System.out.println(m.buffer[bid].read_integer(128));
    }


}
