package org.exreco.experiment.util;

import java.util.Collection;

public interface CollectionProxyIf<E> extends Collection<E> {

	public Collection<E> getProxiedCollection();
}
