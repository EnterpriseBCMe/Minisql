package code;


public class FieldType {

    public static final int CHARSIZE = 1;  //1 byte for a char
    public static final int INTSIZE = 4;   //4 bytes for an integer
    public static final int FLOATSIZE = 4; //4 bytes for a float number

    private String type; // type of number
    private int length; // length of char type

    FieldType() {
        //do noting
    }

    FieldType(String type) {
        this.type = type; //set type ( for integer and float number )
        this.length = 1;
    }

    FieldType(String type,int length) {
        this.type = type; //set type and length ( for char )
        this.length = length;
    }

    String get_type() {
        return this.type;
    }
    int get_length() {
        return this.length;
    }

    void set_type(String type) {
        this.type = type;
    }
    void set_length(int length) {
        this.length = length;
    }
}