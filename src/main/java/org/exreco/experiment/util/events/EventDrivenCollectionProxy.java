package org.exreco.experiment.util.events;

import java.util.Collection;

import org.exreco.experiment.util.CollectionProxy;


/**
 * Proxy class to java.util.Collection with hooks to event sources to signal the
 * addition and removal of elements
 * 
 * 
 * @param <ElementType>
 */
public class EventDrivenCollectionProxy<ElementType> extends
		CollectionProxy<ElementType> implements Collection<ElementType> {

	public class InboundSynchronizer implements
			LiffEventListener<CollectionEvent<ElementType>> {

		@Override
		public void eventOccurred(CollectionEvent<ElementType> event) {

			ElementType element = event.getElement();
			switch (event.getEventSubType()) {
			case ADDED:
				EventDrivenCollectionProxy.this.add(element);
				break;
			case REMOVED:
				EventDrivenCollectionProxy.this.remove(element);
				break;

			}

		}
	}

	public class Event extends LiffEvent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7596945128782784263L;

		public Collection<ElementType> getCollection() {
			return EventDrivenCollectionProxy.this;
		}
	}

	public class ElementEvent extends Event {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8540598553757690502L;
		final private ElementType element;

		public ElementEvent(ElementType element) {
			super();
			this.element = element;
		}

		@SuppressWarnings("unused")
		private ElementType getElement() {
			return element;
		}

	}

	public class AddElementEvent extends ElementEvent {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8564953018730769111L;

		public AddElementEvent(ElementType element) {
			super(element);
		}

	}

	public class RemoveElementEvent extends ElementEvent {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4516368802602324179L;

		public RemoveElementEvent(ElementType element) {
			super(element);
		}

	}

	protected EventSource<Event> eventSource = new EventSource<Event>();

	public EventDrivenCollectionProxy(Collection<ElementType> wrappedCollection) {
		super(wrappedCollection);
	}

	public EventDrivenCollectionProxy(Object wrappedCollection) {
		super(wrappedCollection);
	}

	public LiffEventListener<CollectionEvent<ElementType>> createInboundSynchronizer() {
		return new InboundSynchronizer();
	}

	public EventSource<Event> getEventSource() {
		return eventSource;
	}

	@Override
	public boolean add(ElementType element) {
		boolean result = proxiedCollection.add(element);
		try {
			this.eventSource.fireEvent(new AddElementEvent(element));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	@Override
	public boolean addAll(Collection<? extends ElementType> collectionToAdd) {
		boolean result = false;
		for (ElementType element : collectionToAdd) {
			result |= this.add(element);
		}
		return result;
	}

	@Override
	public void clear() {
		for (ElementType element : this.proxiedCollection) {
			this.remove(element);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object element) {
		boolean result = proxiedCollection.remove(element);
		try {
			this.eventSource.fireEvent(new RemoveElementEvent(
					(ElementType) element));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> collectionToBeRemoved) {
		boolean result = false;
		for (Object element : collectionToBeRemoved) {
			result |= this.remove(element);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> retainedCollection) {
		boolean result = false;
		for (Object element : this.proxiedCollection) {
			if (!retainedCollection.contains(element)) {
				result |= this.remove(element);
			}
		}
		return result;
	}

}
