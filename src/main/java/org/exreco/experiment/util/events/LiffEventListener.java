package org.exreco.experiment.util.events;

import java.io.Serializable;
import java.util.EventListener;

public interface LiffEventListener extends EventListener {

	void eventOccurred(Serializable event) throws Exception;
}
