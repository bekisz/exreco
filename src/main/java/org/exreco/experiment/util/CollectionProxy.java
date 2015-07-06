package org.exreco.experiment.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionProxy<ElementType> implements Collection<ElementType>,
		CollectionProxyIf<ElementType> {
	final protected Collection<ElementType> proxiedCollection;

	// protected EventSource<CollectionEvent<ElementType>> eventSource = new
	// EventSource<CollectionEvent<ElementType>>();
	/*
	 * public EventSource<CollectionEvent<ElementType>> getEventSource() {
	 * return eventSource; }
	 */

	public CollectionProxy(Collection<ElementType> wrappedCollection) {
		this.proxiedCollection = wrappedCollection;
	}

	public CollectionProxy(Object wrappedCollection) {
		this.proxiedCollection = extracted(wrappedCollection);
	}

	@SuppressWarnings("unchecked")
	private Collection<ElementType> extracted(Object wrappedCollection) {
		return (Collection<ElementType>) wrappedCollection;
	}

	@Override
	public boolean add(ElementType element) {
		return proxiedCollection.add(element);

	}

	@Override
	public boolean addAll(Collection<? extends ElementType> collectionToAdd) {
		return proxiedCollection.addAll(collectionToAdd);
	}

	@Override
	public void clear() {
		proxiedCollection.clear();
	}

	@Override
	public boolean contains(Object element) {
		return proxiedCollection.contains(element);
	}

	@Override
	public boolean containsAll(Collection<?> element) {
		return proxiedCollection.containsAll(element);
	}

	@Override
	public boolean equals(Object element) {
		return proxiedCollection.equals(element);
	}

	@Override
	public int hashCode() {
		return proxiedCollection.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return proxiedCollection.isEmpty();
	}

	@Override
	public Iterator<ElementType> iterator() {
		return proxiedCollection.iterator();
	}

	@Override
	public boolean remove(Object element) {
		return this.proxiedCollection.remove(element);
	}

	@Override
	public boolean removeAll(Collection<?> collectionToBeRemoved) {
		return this.proxiedCollection.removeAll(collectionToBeRemoved);
	}

	@Override
	public boolean retainAll(Collection<?> retainedCollection) {
		return this.proxiedCollection.retainAll(retainedCollection);
	}

	@Override
	public int size() {
		return proxiedCollection.size();
	}

	@Override
	public Object[] toArray() {
		return proxiedCollection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return proxiedCollection.toArray(array);
	}

	@Override
	public Collection<ElementType> getProxiedCollection() {
		return proxiedCollection;
	}

}
