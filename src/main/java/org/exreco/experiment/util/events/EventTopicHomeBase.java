package org.exreco.experiment.util.events;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EventTopicHomeBase implements EventTopicHome {
	Logger logger = LogManager.getLogger(EventTopicHome.class);
	private static EventTopicHome eventTopicHome;
	
	@SuppressWarnings("static-access")
	public  EventTopicHomeBase() {
		this.setEventTopicHome(this);
	}


	protected static void setEventTopicHome(EventTopicHome eventTopicH) {
		 eventTopicHome = eventTopicH; 
	}
	
	
	public static EventTopicHome locateEventTopicHome() {
		return eventTopicHome; 
	}
	synchronized private void writeObject(ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();
	}

	synchronized private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();

		setEventTopicHome(this);

	}
	
}

	
