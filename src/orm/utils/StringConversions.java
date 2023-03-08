package orm.utils;

public class StringConversions {
	public static String toLowerCamelCase(String str) {
		
		if(str.equals("")) return str;
		
		String[] splited = str.split("[^A-Za-z]+");
				
		StringBuilder ret = new StringBuilder();
		
		ret.append(Character.toLowerCase(splited[0].charAt(0)) + splited[0].substring(1));
		
		for(int i = 1; i < splited.length; i++) {
			ret.append(Character.toUpperCase(splited[i].charAt(0)) + splited[i].substring(1));
		}
		
		return ret.toString();
	}
}
