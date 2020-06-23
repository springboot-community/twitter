package io.springboot.twitter.utils;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * 无锁Writer
 * @author KevinBlandy
 *
 */
public class StringBuilderWriter extends Writer {
	
	private StringBuilder stringBuilder;
	
	public StringBuilderWriter() {
		this.stringBuilder = new StringBuilder();
	}
	public StringBuilderWriter(int capacity) {
		this.stringBuilder = new StringBuilder(capacity);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.stringBuilder.append(cbuf, off, len);
	}
	
	@Override
	public void write(String str) throws IOException {
		this.stringBuilder.append(str);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		this.stringBuilder.append(cbuf);
	}
	
	@Override
	public void write(int c) throws IOException {
		this.stringBuilder.append(c);
	}
	
	@Override
	public void write(String str, int off, int len) throws IOException {
		this.stringBuilder.append(str,off,len);
	}
	
	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}
	
	@Override
	public String toString() {
		return stringBuilder.toString();
	}
}
