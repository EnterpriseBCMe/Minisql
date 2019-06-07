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
            IndexManager.initial_index(); //init Index Manager
            CatalogManager.initial_catalog();  //init Catalog Manager
            Table tab = generate_testData();
            API.create_table(tab.tableName, tab);
            CatalogManager.show_catalog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Table generate_testData() {
        Attribute tmpAttribute1 = new Attribute("id", NumType.valueOf("INT"), true);
        Attribute tmpAttribute2 = new Attribute("name", NumType.valueOf("CHAR"), 12, true);
        Attribute tmpAttribute3 = new Attribute("category", NumType.valueOf("CHAR"), 20, true);
        Vector<Attribute> tmpAttributeVector = new Vector<>();
        tmpAttributeVector.addElement(tmpAttribute1);
        tmpAttributeVector.addElement(tmpAttribute2);
        tmpAttributeVector.addElement(tmpAttribute3);
        return new Table("student", "id", tmpAttributeVector);
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
                    IndexManager.drop_index(CatalogManager.get_index(indexName + "_index")); //drop index at Index Manager
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

    public static int delete_row(String tabName, Vector<Condition> conditions) {
        int numberOfRecords = RecordManager.delete(tabName, conditions);
        CatalogManager.delete_row_num(tabName, numberOfRecords);
        return numberOfRecords;
    }

    public static Vector<TableRow> select(String tabName, Vector<String> attrName, Vector<Condition> conditions) {
        Vector<TableRow> resultSet = new Vector<>();

        return null;
    }

}
