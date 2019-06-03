package code;

import java.io.*;
import java.util.*;

public class CatalogManager {

    private static LinkedHashMap<String, Table> tables = new LinkedHashMap<>();
    private static LinkedHashMap<String, Index> indexes = new LinkedHashMap<>();
    private static String tableFilename = "table_catalog";
    private static String indexFilename = "index_catalog";

    public static void initial_catalog() throws IOException {
        initial_table();
        initial_index();
    }

    private static void initial_table() throws IOException {
        File file = new File(tableFilename);
        if (!file.exists()) return;
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        String tmpTableName, tmpPrimaryKey;
        int tmpIndexNum, tmpAttributeNum, tmpRowNum;

        while (dis.available() > 0) {
            Vector<Attribute> tmpAttributeVector = new Vector<Attribute>();
            Vector<Index> tmpIndexVector = new Vector<Index>();
            tmpTableName = dis.readUTF();
            tmpPrimaryKey = dis.readUTF();
            tmpRowNum = dis.readInt();
            tmpIndexNum = dis.readInt();
            for (int i = 0; i < tmpIndexNum; i++) {
                String tmpIndexName, tmpAttributeName;
                tmpIndexName = dis.readUTF();
                tmpAttributeName = dis.readUTF();
                tmpIndexVector.addElement(new Index(tmpIndexName, tmpTableName, tmpAttributeName));
            }
            tmpAttributeNum = dis.readInt();
            for (int i = 0; i < tmpAttributeNum; i++) {
                String tmpAttributeName, tmpType;
                int tmpLength;
                boolean tmpIsUnique;
                tmpAttributeName = dis.readUTF();
                tmpType = dis.readUTF();
                tmpLength = dis.readInt();
                tmpIsUnique = dis.readBoolean();
                tmpAttributeVector.addElement(new Attribute(tmpAttributeName, tmpType, tmpLength, tmpIsUnique));
            }
            tables.put(tmpTableName, new Table(tmpTableName, tmpPrimaryKey, tmpAttributeVector, tmpIndexVector, tmpRowNum));
        }
        dis.close();
    }

    private static void initial_index() throws IOException {
        File file = new File(indexFilename);
        if (!file.exists()) return;
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        String tmpIndexName, tmpTableName, tmpAttributeName;
        int tmpBlockNum, tmpRootNum;
        while (dis.available() > 0) {
            tmpIndexName = dis.readUTF();
            tmpTableName = dis.readUTF();
            tmpAttributeName = dis.readUTF();
            tmpBlockNum = dis.readInt();
            tmpRootNum = dis.readInt();
            indexes.put(tmpIndexName, new Index(tmpIndexName, tmpTableName, tmpAttributeName, tmpBlockNum, tmpRootNum));
        }
        dis.close();
    }

    public static void store_catalog() throws IOException {
        store_table();
        store_index();
    }

    private static void store_table() throws IOException {
        File file = new File(tableFilename);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        Table tmpTable;
        Iterator<Map.Entry<String, Table>> iter = tables.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            tmpTable = (Table) entry.getValue();
            dos.writeUTF(tmpTable.tableName);
            dos.writeUTF(tmpTable.primaryKey);
            dos.writeInt(tmpTable.rowNum);
            dos.writeInt(tmpTable.indexNum);
            for (int i = 0; i < tmpTable.indexNum; i++) {
                Index tmpIndex = tmpTable.indexVector.get(i);
                dos.writeUTF(tmpIndex.indexName);
                dos.writeUTF(tmpIndex.attributeName);
            }
            dos.writeInt(tmpTable.attributeNum);
            for (int i = 0; i < tmpTable.attributeNum; i++) {
                Attribute tmpAttribute = tmpTable.attributeVector.get(i);
                dos.writeUTF(tmpAttribute.attributeName);
                dos.writeUTF(tmpAttribute.type.get_type());
                dos.writeInt(tmpAttribute.type.get_length());
                dos.writeBoolean(tmpAttribute.isUnique);
            }
        }
        dos.close();
    }

    private static void store_index() throws IOException {
        File file = new File(indexFilename);
        if (file.exists()) file.delete();
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        Index tmpIndex;
        //Enumeration<Index> en = indexes.elements();
        Iterator<Map.Entry<String, Index>> iter = indexes.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            tmpIndex = (Index) entry.getValue();
            //tmpIndex = en.nextElement();
            dos.writeUTF(tmpIndex.indexName);
            dos.writeUTF(tmpIndex.tableName);
            dos.writeUTF(tmpIndex.attributeName);
            dos.writeInt(tmpIndex.blockNum);
            dos.writeInt(tmpIndex.rootNum);
        }
        dos.close();
    }

    public static void show_catalog() {
        show_table();
        System.out.println();
        show_index();
    }

    private static void show_index() {
        Index tmpIndex;
        //Enumeration<Index> en = indexes.elements();
        Iterator<Map.Entry<String, Index>> iter = indexes.entrySet().iterator();
        int row = 1;
        System.out.println("There are " + indexes.size() + " indexes in the database: ");
        System.out.println("\tIndex name\tTable name\tAttribute name:");
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            tmpIndex = (Index) entry.getValue();
            //tmpIndex = en.nextElement();
            System.out.println(row++ + "\t" + tmpIndex.indexName + "\t\t" + tmpIndex.tableName + "\t\t" + tmpIndex.attributeName);
        }
    }

    private static void show_table() {
        Table tmpTable;
        Index tmpIndex;
        Attribute tmpAttribute;
        //Enumeration<Table> en = tables.elements();
        Iterator<Map.Entry<String, Table>> iter = tables.entrySet().iterator();
        int tableNum = 1;
        System.out.println("There are " + tables.size() + " tables in the database: ");
        while (iter.hasNext()) {
            //tmpTable = en.nextElement();
            Map.Entry entry = iter.next();
            tmpTable = (Table) entry.getValue();
            System.out.println("\nTable " + tableNum++);
            System.out.println("Table name: " + tmpTable.tableName);
            System.out.println("Number of Columns: " + tmpTable.attributeNum);
            System.out.println("Primary key: " + tmpTable.primaryKey);
            System.out.println("Number of rows: " + tmpTable.rowNum);
            System.out.println("Number of Indexes : " + tmpTable.indexNum);
            System.out.println("\tIndex name\tTable name\tAttribute name:");
            for (int i = 0; i < tmpTable.indexNum; i++) {
                tmpIndex = tmpTable.indexVector.get(i);
                System.out.println("\t" + tmpIndex.indexName + "\t\t" + tmpIndex.tableName + "\t\t" + tmpIndex.attributeName);
            }
            System.out.println("Attributes: " + tmpTable.attributeNum);
            System.out.println("\tAttribute name\tType\tlength\tisUnique");
            for (int i = 0; i < tmpTable.attributeNum; i++) {
                tmpAttribute = tmpTable.attributeVector.get(i);
                System.out.println("\t" + tmpAttribute.attributeName + "\t\t\t" + tmpAttribute.type.get_type() + "\t\t" + tmpAttribute.type.get_length() + "\t\t" + tmpAttribute.isUnique);
            }
        }
    }

    public static Table get_table(String tableName) {
        return tables.get(tableName);
    }

    public static Index get_index(String indexName) {
        return indexes.get(indexName);
    }

    public static String get_primary_key(String tableName) {
        return get_table(tableName).primaryKey;
    }

    public static int get_row_length(String tableName) {
        return get_table(tableName).rowLength;
    }

    public static int get_attribute_num(String tableName) {
        return get_table(tableName).attributeNum;
    }

    public static int get_row_num(String tableName) {
        return get_table(tableName).rowNum;
    }

    //check
    public static boolean is_primary_key(String tableName, String attributeName) {
        if (tables.containsKey(tableName)) {
            Table tmpTable = get_table(tableName);
            return tmpTable.primaryKey.equals(attributeName);
        } else {
            System.out.println("The table " + tableName + " doesn't exist");
            return false;
        }
    }

    public static boolean is_unique(String tableName, String attributeName) {
        if (tables.containsKey(tableName)) {
            Table tmpTable = get_table(tableName);
            for (int i = 0; i < tmpTable.attributeVector.size(); i++) {
                Attribute tmpAttribute = tmpTable.attributeVector.get(i);
                if (tmpAttribute.attributeName.equals(attributeName)) {
                    return tmpAttribute.isUnique;
                }
            }
            //if (i >= tmpTable.attributeVector.size()) {
            System.out.println("The attribute " + attributeName + " doesn't exist");
            return false;
            //}
        }
        System.out.println("The table " + tableName + " doesn't exist");
        return false;

    }

    public static boolean is_index_key(String tableName, String attributeName) {
        if (tables.containsKey(tableName)) {
            Table tmpTable = get_table(tableName);
            if (is_attribute_exist(tableName, attributeName)) {
                for (int i = 0; i < tmpTable.indexVector.size(); i++) {
                    if (tmpTable.indexVector.get(i).attributeName.equals(attributeName))
                        return true;
                }
            } else {
                System.out.println("The attribute " + attributeName + " doesn't exist");
            }
        } else
            System.out.println("The table " + tableName + " doesn't exist");
        return false;
    }

    private static boolean is_index_exist(String indexName) {
        return indexes.containsKey(indexName);
    }

    private static boolean is_attribute_exist(String tableName, String attributeName) {
        Table tmpTable = get_table(tableName);
        for (int i = 0; i < tmpTable.attributeVector.size(); i++) {
            if (tmpTable.attributeVector.get(i).attributeName.equals(attributeName))
                return true;
        }
        return false;
    }

    public static String get_index_name(String tableName, String attriName) {
        if (tables.containsKey(tableName)) {
            Table tmpTable = get_table(tableName);
            if (is_attribute_exist(tableName, attriName)) {
                for (int i = 0; i < tmpTable.indexVector.size(); i++) {
                    if (tmpTable.indexVector.get(i).attributeName.equals(attriName))
                        return tmpTable.indexVector.get(i).indexName;
                }
            } else {
                System.out.println("The attribute " + attriName + " doesn't exist");
            }
        } else
            System.out.println("The table " + tableName + " doesn't exist");
        return null;
    }

    public static String get_attribute_name(String tableName, int i) {
        return tables.get(tableName).attributeVector.get(i).attributeName;
    }

    public static int get_attribute_index(String tableName, String attributeName) {
        Table tmpTable = tables.get(tableName);
        Attribute tmpAttribute;
        for (int i = 0; i < tmpTable.attributeVector.size(); i++) {
            tmpAttribute = tmpTable.attributeVector.get(i);
            if (tmpAttribute.attributeName.equals(attributeName))
                return i;
        }
        System.out.println("The attribute " + attributeName + " doesn't exist");
        return -1;
    }

    public static FieldType get_attribute_type(String tableName, String attributeName) {
        Table tmpTable = tables.get(tableName);
        Attribute tmpAttribute;
        for (int i = 0; i < tmpTable.attributeVector.size(); i++) {
            tmpAttribute = tmpTable.attributeVector.get(i);
            if (tmpAttribute.attributeName.equals(attributeName))
                return tmpAttribute.type;
        }
        System.out.println("The attribute " + attributeName + " doesn't exist");
        return null;
    }

    public static int get_length(String tableName, String attributeName) {
        Table tmpTable = tables.get(tableName);
        Attribute tmpAttribute;
        for (int i = 0; i < tmpTable.attributeVector.size(); i++) {
            tmpAttribute = tmpTable.attributeVector.get(i);
            if (tmpAttribute.attributeName.equals(attributeName))
                return tmpAttribute.type.get_length();
        }
        System.out.println("The attribute " + attributeName + " doesn't exist");
        return -1;
    }

    public static String get_type(String tableName, int i) {
        //Table tmpTable=tables.get(tableName);
        return tables.get(tableName).attributeVector.get(i).type.get_type();
    }

    public static int get_length(String tableName, int i) {
        //table tmpTable=tables.get(tableName);
        return tables.get(tableName).attributeVector.get(i).type.get_length();
    }

    public static void add_row_num(String tableName) {
        tables.get(tableName).rowNum++;
    }

    public static void delete_row_num(String tableName, int num) {
        tables.get(tableName).rowNum -= num;
    }

    public static boolean update_index_table(String indexName, Index tmpIndex) {
        indexes.replace(indexName, tmpIndex);
        return true;
    }

    public static boolean is_attribute_exist(Vector<Attribute> attributeVector, String attributeName) {
        for (int i = 0; i < attributeVector.size(); i++) {
            if (attributeVector.get(i).attributeName.equals(attributeName))
                return true;
        }
        return false;
    }

    //Interface
    public static boolean create_table(Table newTable) {
        try {
            tables.put(newTable.tableName, newTable);
            //indexes.put(newTable.indexes.firstElement().indexName, newTable.indexes.firstElement());
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean drop_table(String tableName) {
        try {
            Table tmpTable = tables.get(tableName);
            for (int i = 0; i < tmpTable.indexVector.size(); i++) {
                indexes.remove(tmpTable.indexVector.get(i).indexName);
            }
            tables.remove(tableName);
            return true;
        } catch (NullPointerException e) {
            System.out.println("Error: null table. " + e.getMessage());
            return false;
        }
    }

    public static boolean create_index(Index newIndex) {
        try {
            Table tmpTable = get_table(newIndex.tableName);
            tmpTable.indexVector.addElement(newIndex);
            tmpTable.indexNum = tmpTable.indexVector.size();
            indexes.put(newIndex.indexName, newIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean drop_index(String indexName) {
        try {
            Index tmpIndex = get_index(indexName);
            Table tmpTable = get_table(tmpIndex.tableName);
            tmpTable.indexVector.remove(tmpIndex);
            tmpTable.indexNum = tmpTable.indexVector.size();
            indexes.remove(indexName);
            return true;
        } catch (NullPointerException e) {
            //e.printStackTrace();
            System.out.println("Error: null index. " + e.getMessage());
            return false;
        }

    }

}
