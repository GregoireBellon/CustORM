package orm.selection;

public class Selector {
	
	private String field_in_db;
	private Object comparated;
	
	private Comparator comparator;
	
	public Selector(String field_in_db, Comparator comp, Object comparated) {
		this.field_in_db = field_in_db; 
		this.comparator = comp; 
		this.comparated = comparated;
	}
		
	public String getFieldInDb() {
		return this.field_in_db;
	}
	
	public Comparator getComparator() {
		return this.comparator;
	}
	
	public Object getComparated() {
		return this.comparated;
	}	
	
}
