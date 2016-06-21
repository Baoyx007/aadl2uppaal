package aadl2upaal.upaal;

import aadl2upaal.aadl.*;
import aadl2upaal.visitor.*;

public class Channel extends UNode {
	Flow flow;
	private int direction = 0;
	private String type = "";
	public String value = "";

	public Channel(String name, int direction, String type) {
		super(name);
		this.direction = direction;
		this.type = type;
	}

	Channel(Flow f) {
		flow = f;
	}

	public String toString() {
		return flow.toString().replaceAll("\\.", UpaalWriter.sep);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Channel))
			return false;
		Channel c = (Channel) o;
		// System.out.println("this channel: " + this);
		// System.out.println("other channel: " + c);
		return flow.equals(c.flow);
	}
}