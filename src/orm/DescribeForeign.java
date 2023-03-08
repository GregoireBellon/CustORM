package orm;

public class DescribeForeign {
	private Class<? extends Entity> foreign_class;
	private String foreign_attribute_name;
	
	public DescribeForeign(Class<? extends Entity> foreign_class, String foreign_attribute_name) {
	
		this.foreign_class = foreign_class;
		this.foreign_attribute_name = foreign_attribute_name;
	
	}

	public Class<? extends Entity> getForeign_class() {
		return foreign_class;
	}

	public String getForeign_attribute_name() {
		return foreign_attribute_name;
	}
	
}
