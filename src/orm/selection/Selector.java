package orm.selection;

import orm.DataField;

public class Selector {
	
	private DataField a;
	private Object b;
	
	private Comparator comparator;
	
	public Selector(DataField a, Comparator comp, Number b) {
		this.a = a; 
		this.comparator = comp; 
		this.b = b;
	}
		
	public DataField getA() {
		return a;
	}
	
	public Comparator getComparator() {
		return comparator;
	}
	
	public Object getB() {
		return b;
	}	
	
}
