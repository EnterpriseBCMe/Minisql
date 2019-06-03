package code;

public class Condition {
    private String name;  //attribute name in condition
    private String value; //attribute value in condition
    private String operator; //condition operator

    public Condition() {
        //do nothing
    }

    public Condition(String name, String operator, String value) {
        this.name = name; //initialize name, operator and value
        this.operator = operator;
        this.value = value;
    }

    //if the data in table satisfy the condition, return true, else return false
    public boolean satisfy(Table table, TableRow data) {
        int index = CatalogManager.get_attribute_index(table.tableName, this.name); //get attribute index
        FieldType type = CatalogManager.get_attribute_type(table.tableName, this.name); //get type

        if (type.get_type().equals("char")) { //char type
            String cmpObject = data.get_attribute_value(index);
            String cmpValue = this.value;
            if (this.operator.equals("=")) {
                return cmpObject.compareTo(cmpValue) == 0;
            } else if (this.operator.equals("<>")) {
                return cmpObject.compareTo(cmpValue) != 0;
            } else if (this.operator.equals(">")) {
                return cmpObject.compareTo(cmpValue) > 0;
            } else if (this.operator.equals("<")) {
                return cmpObject.compareTo(cmpValue) < 0;
            } else if (this.operator.equals(">=")) {
                return cmpObject.compareTo(cmpValue) >= 0;
            } else if (this.operator.equals("<=")) {
                return cmpObject.compareTo(cmpValue) <= 0;
            } else { //undefined operator
                return false;
            }
        } else if (type.get_type().equals("int")) { //integer type
            int cmpObject = Integer.parseInt(data.get_attribute_value(index));
            int cmpValue = Integer.parseInt(this.value);
            if (this.operator.equals("=")) {
                return cmpObject == cmpValue;
            } else if (this.operator.equals("<>")) {
                return cmpObject != cmpValue;
            } else if (this.operator.equals(">")) {
                return cmpObject > cmpValue;
            } else if (this.operator.equals("<")) {
                return cmpObject < cmpValue;
            } else if (this.operator.equals(">=")) {
                return cmpObject >= cmpValue;
            } else if (this.operator.equals("<=")) {
                return cmpObject <= cmpValue;
            } else { //undefined operator
                return false;
            }
        } else if (type.get_type().equals("float")) { //float type
            float cmpObject = Float.parseFloat(data.get_attribute_value(index));
            float cmpValue = Float.parseFloat(this.value);
            if (this.operator.equals("=")) {
                return cmpObject == cmpValue;
            } else if (this.operator.equals("<>")) {
                return cmpObject != cmpValue;
            } else if (this.operator.equals(">")) {
                return cmpObject > cmpValue;
            } else if (this.operator.equals("<")) {
                return cmpObject < cmpValue;
            } else if (this.operator.equals(">=")) {
                return cmpObject >= cmpValue;
            } else if (this.operator.equals("<=")) {
                return cmpObject <= cmpValue;
            } else { //undefined operator
                return false;
            }
        } else { //undefined type
            return false;
        }
    }


    String get_name() {
        return this.name;
    }

    String get_value() {
        return this.value;
    }

    String get_operator() {
        return this.operator;
    }

    void set_name(String name) {
        this.name = name;
    }

    void set_value(String value) {
        this.value = value;
    }

    void set_operator(String operator) {
        this.operator = operator;
    }
}
