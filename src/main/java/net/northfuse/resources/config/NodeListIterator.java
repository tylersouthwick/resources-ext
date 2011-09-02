package net.northfuse.resources.config;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * @author tylers2
 */
public class NodeListIterator<T extends Node> implements Iterable<T> {
	private final NodeList nodeList;

	public NodeListIterator(NodeList nodeList) {
		this.nodeList = nodeList;
	}

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