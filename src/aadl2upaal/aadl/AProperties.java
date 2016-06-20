package aadl2upaal.aadl;

import java.util.HashMap;

public class AProperties {
	//Periodic 200
	private HashMap<String, String> items;

	public AProperties() {
		this.items = new HashMap<>();
	}

	public HashMap<String, String> getItems() {
		return items;
	}

	public void setItems(HashMap<String, String> items) {
		this.items = items;
	}

}
