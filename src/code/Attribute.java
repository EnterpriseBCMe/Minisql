package code;

public class Attribute {

    public String attributeName;
    public FieldType type;
    public boolean isUnique;

    public Attribute(String attributeName, String type, int length, boolean isUnique) {
        this.attributeName = attributeName;
        this.type = new FieldType(type, length);
        this.isUnique = isUnique;
    }
}