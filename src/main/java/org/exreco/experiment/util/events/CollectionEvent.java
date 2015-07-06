package org.exreco.experiment.util.events;

import java.util.Collection;
import java.util.EventObject;

public class CollectionEvent<ElementType> extends EventObject {

	private static final long serialVersionUID = -4709703100989168474L;

	public enum EventSubType {
		ADDED, REMOVED
	}

	final private Collection<ElementType> collection;
	final private ElementType element;
	final private EventSubType eventSubType;

	public CollectionEvent(Collection<ElementType> collection,
			ElementType element, EventSubType eventSubType) {
		super(collection);
		this.collection = collection;
		this.element = element;
		this.eventSubType = eventSubType;
	}

	final public EventSubType getEventSubType() {
		return this.eventSubType;
	}

	final public ElementType getElement() {
		return this.element;
	}

	public Collection<ElementType> getCollection() {
		return collection;
	}

}
