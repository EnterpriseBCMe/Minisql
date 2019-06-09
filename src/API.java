import BUFFERMANAGER.BufferManager;
import CATALOGMANAGER.*;
import INDEXMANAGER.Index;
import INDEXMANAGER.IndexManager;
import RECORDMANAGER.Condition;
import RECORDMANAGER.RecordManager;
import RECORDMANAGER.TableRow;

import java.io.IOException;
import java.util.Vector;

public class API {

    public API() {
        try {
            BufferManager.initial_buffer();  //init Buffer Manager
            CatalogManager.initial_catalog();  //init Catalog Manager
            IndexManager.initial_index(); //init Index Manager
            Table tab1 = generate_testData1();
            //API.create_table(tab1.tableName, tab1);
            //CatalogManager.show_catalog();
            /*Table tab2 = generate_testData2();
            API.create_table(tab2.tableName,tab2);
            CatalogManager.show_catalog();
            API.drop_table(tab2.tableName);
            CatalogManager.show_catalog();*/
            //API.create_index(new Index("student_index_id", "student","id"));
            CatalogManager.show_catalog();
            /*API.create_index(new Index("student_index_name","student","name"));
            CatalogManager.show_catalog();
            API.drop_index(new Index("student_index_name","student","name"));
            CatalogManager.show_catalog();*/
//            TableRow tbr1 = generate_testData3();
//            insert_row("student", tbr1);
//            TableRow tbr2 = generate_testData4();
//            insert_row("student", tbr2);
//            TableRow tbr3 = generate_testData5();
//            insert_row("student", tbr3);
            /*Vector<Condition> tmpCond = new Vector<>();
            tmpCond.addElement(new Condition("name", "=", "Tom"));
            System.out.println(delete_row("student", tmpCond));
            tmpCond.clear();
            tmpCond.addElement(new Condition("name", "=", "Jack"));
            System.out.println(delete_row("student", tmpCond));*/
            Vector<String> attriNameVector = new Vector<>();
            attriNameVector.addElement("id");
            attriNameVector.addElement("name");
            Vector<Condition> conditions = new Vector<>();
            //conditions.addElement(new Condition("id","<","2"));
            //conditions.addElement(new Condition("id","=","2"));
            //conditions.addElement(new Condition("id", "<>", "2"));
            conditions.addElement(new Condition("name", "=", "Jack"));
            delete_row("student", conditions);
            Vector<TableRow> res = select("student", new Vector<>(), new Vector<>());
            //Vector<TableRow> res = select("student", attriNameVector, conditions);
            for (int i = 0; i < res.size(); i++) {
                for (int j = 0; j < res.get(i).get_attribute_size(); j++) {
                    System.out.println(res.get(i).get_attribute_value(j));
                }
            }
            //CatalogManager.store_catalog();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try { //store data back to disk
            CatalogManager.store_catalog();
            BufferManager.destruct_buffer_manager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Table generate_testData1() {
        Attribute tmpAttribute1 = new Attribute("id", NumType.valueOf("INT"), true);
        Attribute tmpAttribute2 = new Attribute("name", NumType.valueOf("CHAR"), 12, true);
        Attribute tmpAttribute3 = new Attribute("category", NumType.valueOf("CHAR"), 20, true);
        Vector<Attribute> tmpAttributeVector = new Vector<>();
        tmpAttributeVector.addElement(tmpAttribute1);
        tmpAttributeVector.addElement(tmpAttribute2);
        tmpAttributeVector.addElement(tmpAttribute3);
        return new Table("student", "id", tmpAttributeVector);
    }

    public static Table generate_testData2() {
        Attribute tmpAttribute1 = new Attribute("id", NumType.valueOf("INT"), true);
        Attribute tmpAttribute2 = new Attribute("card", NumType.valueOf("CHAR"), 12, true);
        Attribute tmpAttribute3 = new Attribute("record", NumType.valueOf("CHAR"), 20, true);
        Vector<Attribute> tmpAttributeVector = new Vector<>();
        tmpAttributeVector.addElement(tmpAttribute1);
        tmpAttributeVector.addElement(tmpAttribute2);
        tmpAttributeVector.addElement(tmpAttribute3);
        return new Table("card", "id", tmpAttributeVector);
    }

    public static TableRow generate_testData3() {
        TableRow tbr = new TableRow();
        tbr.add_attribute_value("1");
        tbr.add_attribute_value("Tom");
        tbr.add_attribute_value("CS");
        return tbr;
    }

    public static TableRow generate_testData4() {
        TableRow tbr = new TableRow();
        tbr.add_attribute_value("2");
        tbr.add_attribute_value("Jack");
        tbr.add_attribute_value("Math");
        return tbr;
    }

    public static TableRow generate_testData5() {
        TableRow tbr = new TableRow();
        tbr.add_attribute_value("3");
        tbr.add_attribute_value("Bob");
        tbr.add_attribute_value("Math");
        return tbr;
    }

    public static boolean create_table(String tabName, Table tab) {
        try {
            if (CatalogManager.create_table(tab) && RecordManager.create_table(tabName)) {
                String indexName = tabName + "_index";  //refactor index name
                Index index = new Index(indexName, tabName, CatalogManager.get_primary_key(tabName));
                IndexManager.create_index(index);  //create index on Index Manager
                CatalogManager.create_index(index); //create index on Catalog Manager
                return true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("RUNTIME ERROR STATUS 500: Table " + tabName + " already exist!");
            return false;
        } catch (IOException e) {
            System.out.println("RUNTIME ERROR STATUS 501: Failed to create an index on table " + tabName);
            return false;
        }
        System.out.println("RUNTIME ERROR STATUS 502: Failed to create table " + tabName);
        return false;
    }

    public static boolean drop_table(String tabName) {
        try {
            for (int i = 0; i < CatalogManager.get_attribute_num(tabName); i++) {
                String attrName = CatalogManager.get_attribute_name(tabName, i);
                String indexName = CatalogManager.get_index_name(tabName, attrName);  //find index if exists
                if (indexName != null) {
                    IndexManager.drop_index(CatalogManager.get_index(indexName)); //drop index at Index Manager
                }
            }
            if (CatalogManager.drop_table(tabName) && RecordManager.drop_table(tabName)) return true;
        } catch (NullPointerException e) {
            System.out.println("RUNTIME ERROR: Table " + tabName + " does not exist!");
            return false;
        }
        System.out.println("RUNTIME ERROR: Failed to drop table!" + tabName);
        return false;
    }

    public static boolean create_index(Index index) {
        try {
            if (CatalogManager.create_index(index) && IndexManager.create_index(index)) return true;
        } catch (IOException e) {
            //do nothing
        }
        System.out.println("RUNTIME ERROR: Failed to create index " + index.attributeName + " on table " + index.tableName);
        return false;
    }

    public static boolean drop_index(Index index) {
        if (IndexManager.drop_index(index) && CatalogManager.drop_index(index.indexName)) return true;
        System.out.println("RUNTIME ERROR: Failed to drop index " + index.attributeName + " on table " + index.tableName);
        return false;
    }

    public static boolean insert_row(String tabName, TableRow row) {
        try {
            Address recordAddr = RecordManager.insert(tabName, row);  //insert and get return address
            int attrNum = CatalogManager.get_attribute_num(tabName);  //get the number of attribute
            for (int i = 0; i < attrNum; i++) {
                String attrName = CatalogManager.get_attribute_name(tabName, i);
                String indexName = CatalogManager.get_index_name(tabName, attrName);  //find index if exists
                if (indexName != null) {  //index exists, then need to insert the key to BPTree
                    Index index = CatalogManager.get_index(indexName); //get index
                    String key = row.get_attribute_value(i);  //get value of the key
                    IndexManager.insert(index, key, recordAddr);  //insert to index manager
                    CatalogManager.update_index_table(indexName, index); //update index
                }
            }
            CatalogManager.add_row_num(tabName);  //update number of records in catalog
        } catch (Exception e) {
            System.out.println("RUNTIME ERROR: Failed to insert a row on table " + tabName);
            return false;
        }
        return true;
    }

//    public static int delete_row(String tabName, Vector<Condition> conditions) {
//        int numberOfRecords = RecordManager.delete(tabName, conditions);
//        CatalogManager.delete_row_num(tabName, numberOfRecords);
//        return numberOfRecords;
//    }

    public static int delete_row(String tabName, Vector<Condition> conditions) {
        Condition condition = API.find_index_condition(tabName, conditions);
        int numberOfRecords = 0;
        if (condition != null) {
            try {
                String indexName = CatalogManager.get_index_name(tabName, condition.get_name());
                Index idx = CatalogManager.get_index(indexName);
                Vector<Address> addresses = IndexManager.select(idx, condition);
                if (addresses != null) {
                    numberOfRecords = RecordManager.delete(addresses, conditions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            numberOfRecords = RecordManager.delete(tabName, conditions);
        }
        CatalogManager.delete_row_num(tabName, numberOfRecords);
        return numberOfRecords;
    }

//    public static Vector<TableRow> select(String tabName, Vector<String> attriName, Vector<Condition> conditions) {
//        Vector<TableRow> resultSet = new Vector<>();
//        if (conditions.size() == 1 && conditions.get(0).get_operator() == "=" && CatalogManager.get_index_name(tabName, conditions.get(0).get_name()) != null) {
//            String indexName = CatalogManager.get_index_name(tabName, conditions.get(0).get_name());
//            try {
//                Index idx = CatalogManager.get_index(indexName);
//                Vector<Address> addresses = IndexManager.select(idx, conditions.get(0));
//                if (addresses != null) {
//                    resultSet = RecordManager.select(addresses, conditions);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            resultSet = RecordManager.select(tabName, conditions);
//        }
//        if (!attriName.isEmpty()) {
//            return RecordManager.project(tabName, resultSet, attriName);
//        } else {
//            return resultSet;
//        }
//    }

    public static Vector<TableRow> select(String tabName, Vector<String> attriName, Vector<Condition> conditions) {
        Vector<TableRow> resultSet = new Vector<>();
        Condition condition = API.find_index_condition(tabName, conditions);
        if (condition != null) {
            try {
                String indexName = CatalogManager.get_index_name(tabName, condition.get_name());
                Index idx = CatalogManager.get_index(indexName);
                Vector<Address> addresses = IndexManager.select(idx, condition);
                if (addresses != null) {
                    resultSet = RecordManager.select(addresses, conditions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resultSet = RecordManager.select(tabName, conditions);
        }
        if (!attriName.isEmpty()) {
            return RecordManager.project(tabName, resultSet, attriName);
        } else {
            return resultSet;
        }
    }

    private static Condition find_index_condition(String tabName, Vector<Condition> conditions) {
        Condition condition = null;
        for (int i = 0; i < conditions.size(); i++) {
            if (CatalogManager.get_index_name(tabName, conditions.get(i).get_name()) != null) {
                condition = conditions.get(i);
                conditions.remove(condition);
                break;
            }
        }
        return condition;
    }

}
