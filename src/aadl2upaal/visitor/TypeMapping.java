package aadl2upaal.visitor;

import java.util.HashMap;

public class TypeMapping {
	public HashMap<String, String> map = new HashMap<String, String>();
	public static TypeMapping instance = new TypeMapping();

	private TypeMapping() {
		// TODO Auto-generated constructor stub
		map.put("aadlinteger", "int");
		map.put("aadlreal", "double");
		map.put("aadlinteger", "int");
		map.put("aadlinteger", "int");

		map.put("CTCS_Types::Velocity","clock");
		map.put("CTCS_Types::Position","clock");
        map.put("CTCS_Types::Acceleration","clock");
        map.put("CTCS_Types::Time","clock");

		map.put("BLESS_Types::Real","clock");
	}

	public String getMappingType(String str) {
		if (null == map.get(str)) {
			return "double";
		} else {
			return map.get(str);
		}
	}
}
