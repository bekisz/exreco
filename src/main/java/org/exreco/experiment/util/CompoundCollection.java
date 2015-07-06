package org.exreco.experiment.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CompoundCollection<ElType> implements Collection<ElType>,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6308388063631214243L;

	class CompoundCollectionIterator<ElementType> implements
			Iterator<ElementType> {
		private final Iterator<Collection<ElementType>> outerIterator;
		private Iterator<ElementType> innerIterator;
		private Collection<ElementType> currentCollection;

		public CompoundCollectionIterator(
				CompoundCollection<ElementType> collection) {
			super();

			this.outerIterator = collection.getCollections().iterator();
			if (this.outerIterator.hasNext()) {
				this.currentCollection = this.outerIterator.next();
				this.innerIterator = currentCollection.iterator();
			}

		}

		@Override
		public boolean hasNext() {
			if (this.innerIterator == null) {
				return false;
			}
			if (this.innerIterator.hasNext()) {
				return true;
			} else {
				if (this.outerIterator.hasNext()) {

					this.currentCollection = this.outerIterator.next();
					this.innerIterator = currentCollection.iterator();
					return this.hasNext();
				}
			}
			return false;
		}

		@Override
		public ElementType next() {

			return this.innerIterator.next();

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	};

	private final Collection<Collection<ElType>> collections = new ArrayList<Collection<ElType>>();

	@Override
	public boolean add(ElType e) {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean addAll(Collection<? extends ElType> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean contains(Object o) {
		for (Collection<ElType> collection : this.getCollections()) {
			if (collection.contains(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		for (Object o : c) {
			if (!this.contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		for (Collection<ElType> collection : this.getCollections()) {
			if (!collection.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<ElType> iterator() {
		return new CompoundCollectionIterator<ElType>(this);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		int sizeCounter = 0;
		for (Collection<ElType> collection : this.getCollections()) {
			sizeCounter += collection.size();
		}
		return sizeCounter;
	}

	@Override
	public Object[] toArray() {
		Object[] objectArray = new Object[this.size()];
		int i = 0;
		for (Collection<ElType> outer : this.getCollections()) {
			for (ElType inner : outer) {
				objectArray[i] = inner;
				i++;
			}
		}

		return objectArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {

		return (T[]) this.toArray();
	}

	/**
	 * @return the collections
	 */
	public Collection<Collection<ElType>> getCollections() {
		return collections;
	}

}
