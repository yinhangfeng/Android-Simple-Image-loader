package com.damingdan.lib.imageloader;

import java.io.IOException;
import java.io.InputStream;

public class ContentLengthInputStream extends InputStream {

	private final InputStream in;
	private final long length;
	private long pos;

	public ContentLengthInputStream(InputStream is, long length) {
		this.in = is;
		this.length = length;
	}

	@Override
	public int available() {
		return (int) (length - pos);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public void mark(final int readlimit) {
		pos = readlimit;
		in.mark(readlimit);
	}

	@Override
	public int read() throws IOException {
		++pos;
		return in.read();
	}

	@Override
	public int read(final byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(final byte[] buffer, final int byteOffset, final int byteCount) throws IOException {
		pos += byteCount;
		return in.read(buffer, byteOffset, byteCount);
	}

	@Override
	public void reset() throws IOException {
		pos = 0;
		in.reset();
	}

	@Override
	public long skip(final long byteCount) throws IOException {
		pos += byteCount;
		return in.skip(byteCount);
	}
}