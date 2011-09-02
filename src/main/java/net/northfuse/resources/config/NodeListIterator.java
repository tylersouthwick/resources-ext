package net.northfuse.resources.config;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * Converts a node list to an iterable.
 *
 * @param <T> Some type that extends Node
 *
 * @author tylers2
 */
public final class NodeListIterator<T extends Node> implements Iterable<T> {
	private final NodeList nodeList;

	/**
	 * Creates a NodeListIterator.
	 * @param nodeList A node list
	 */
	public NodeListIterator(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int count = 0;

			public boolean hasNext() {
				return count < nodeList.getLength();
			}

			@SuppressWarnings("unchecked")
			public T next() {
				return (T) nodeList.item(count++);
			}

			public void remove() {
			}
		};
	}
}
