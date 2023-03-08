package tests;


import org.junit.jupiter.api.Test;

import orm.utils.StringConversions;


class TestUtil {
			
	
	@Test
	void testLowerCamel1() {
				
		String input = "bonjour_les_gens";
		
		String expected = "bonjourLesGens";
		
		System.out.println("Got " + StringConversions.toLowerCamelCase(input));
		
		assert StringConversions.toLowerCamelCase(input).equals(expected);				
	}
	
	@Test
	void testLowerCamel2() {
				
		String input = "A";
		
		String expected = "a";
		
		System.out.println("Got " + StringConversions.toLowerCamelCase(input));

		assert StringConversions.toLowerCamelCase(input).equals(expected);				
	}

	@Test
	void testLowerCamel3() {
				
		String input = "get_name";
		
		String expected = "getName";
		
		System.out.println("Got " + StringConversions.toLowerCamelCase(input));

		assert StringConversions.toLowerCamelCase(input).equals(expected);				
	}


}
