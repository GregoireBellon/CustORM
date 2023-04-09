package orm.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringConversions {
	
	public static String hashSHA256(String conv_string) throws NoSuchAlgorithmException {
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(
				conv_string.getBytes(StandardCharsets.UTF_8));
//		byte[] hash = 
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		
		return hexString.toString();
	}
	
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
