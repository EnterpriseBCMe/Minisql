package code;

import java.util.ArrayList;
import java.util.List;

public class TableRow {

    private List<String> attributeValue;

    public TableRow() {
        attributeValue = new ArrayList<String>();
    }

    public TableRow(List<String> attributeValue) {
        this.attributeValue = new ArrayList<String>(attributeValue);
    }

    //add one new attribute value in table row
    public void add_attribute_value(String attributeValue) {
        this.attributeValue.add(attributeValue);
    }

    public String get_attribute_value(int index) { 
		return attributeValue.get(index);
	}
}
