package net.northfuse.resources;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

/**
 * An implementation of Resource.
 * @author tylers2
 */
public final class AggregatedResource extends ByteArrayResource {

	/**
	 * Creates an Aggregated resource.
	 * @param byteArray a byte array
	 */
	public AggregatedResource(byte[] byteArray) {
		super(byteArray);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lastModified() throws IOException {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long contentLength() throws IOException {
		return getByteArray().length;
	}
}
