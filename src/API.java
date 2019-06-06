import CATALOGMANAGER.CatalogManager;
import CATALOGMANAGER.Table;
import INDEXMANAGER.Index;
import RECORDMANAGER.Condition;
import RECORDMANAGER.RecordManager;
import RECORDMANAGER.TableRow;

import java.util.Vector;

public class API {

    public API() {
        try {
            //Put Code Here: init Record Manager
            //Put Code Here: init Buffer Manager
            //Put Code Here: init Index Manager
            CatalogManager.initial_catalog();  //init Catalog Manager
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createTable(String tabName, Table tab) {
        //what if tabName already exists?
        if (RecordManager.create_table(tabName) && CatalogManager.create_table(tab)) {
            String indexName = tabName + "_index";
            Index index = new Index(indexName, tabName, CatalogManager.get_primary_key(tabName));
            //Put Code Here: create index on Index Manager
            CatalogManager.create_index(index);
            return true;
        }
        return false;
    }

    public static boolean dropTable(String tabName) {
        //what if tableName does not exist?
        for (int i = 0; i < CatalogManager.get_attribute_num(tabName); i++) {
            String attrName = CatalogManager.get_attribute_name(tabName, i);
            String indexName = CatalogManager.get_index_name(tabName, attrName);
            if (indexName != null) {
                //Put Code Here: drop index at Index Manager
            }
        }
        RecordManager.drop_table(tabName);
        CatalogManager.drop_table(tabName);
        return true;
    }

    public static boolean createIndex(Index index) {
        
        return true;
    }

    public static boolean dropIndex(Index index) {

        return true;
    }

    public static boolean insertRow(String tabName, TableRow row) {

        return true;
    }

    public static int deleteRow(TableRow row) {

        return 0;
    }

    public static Vector<TableRow> select(String tabName, Vector<String> attrName, Condition condition) {

        return null;
    }

}
