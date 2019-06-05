import BUFFERMANAGER.BufferManager;
import CATALOGMANAGER.NumType;
import INDEXMANAGER.Index;
import CATALOGMANAGER.Attribute;
import CATALOGMANAGER.CatalogManager;
import CATALOGMANAGER.Table;

import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        buffer_unit_test(); //Buffer Manager test function
        catalog_unit_test1();
        //catalog_unit_test2();
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

    private static void catalog_unit_test2() {
        try {
            CatalogManager.initial_catalog();
            CatalogManager.show_catalog();
            CatalogManager.store_catalog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void catalog_unit_test1() {
        try {
            CatalogManager.initial_catalog();
            Attribute tmpAttribute1 = new Attribute("id", NumType.valueOf("int"), true);
            Attribute tmpAttribute2 = new Attribute("name", NumType.valueOf("char"), 12, true);
            Attribute tmpAttribute3 = new Attribute("category", NumType.valueOf("char"), 20, true);
            Vector<Attribute> tmpAttributeVector = new Vector<>();
            tmpAttributeVector.addElement(tmpAttribute1);
            tmpAttributeVector.addElement(tmpAttribute2);
            Table tmpTable1 = new Table("students", "id", tmpAttributeVector);
            CatalogManager.create_table(tmpTable1);
            CatalogManager.show_catalog();
            Index tmpIndex1 = new Index("idIndex", "students", "id");
            CatalogManager.create_index(tmpIndex1);
            CatalogManager.show_catalog();
            tmpAttributeVector.addElement(tmpAttribute3);
            Table tmpTable2 = new Table("book", "name", tmpAttributeVector);
            CatalogManager.create_table(tmpTable2);
            CatalogManager.show_catalog();
            //CatalogManager.drop_table("students");
            //CatalogManager.show_catalog();
            //CatalogManager.drop_index("idIndex");
            Index tmpIndex2 = new Index("categoryIndex", "book", "category");
            CatalogManager.create_index(tmpIndex2);
            CatalogManager.show_catalog();
            CatalogManager.store_catalog();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
